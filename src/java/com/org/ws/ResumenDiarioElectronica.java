package com.org.ws;

//import com.urp.util.HeaderHandlerResolver;
//import com.urp.util.Util;
//import com.sunat.ws.*;
//import com.urp.xml.beans.DocumentoBean;
//import com.urp.xml.despachadores.LecturaXML;
import com.org.factories.Util;
import com.org.model.beans.DocumentoBean;
import com.org.model.despatchers.ResumenDespachador;
import com.org.util.HeaderHandlerResolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

public class ResumenDiarioElectronica {

    private static Log log = LogFactory.getLog(ResumenDiarioElectronica.class);

    public static String enviarResumenASunat(String iddocument, Connection conn) {
        log.info("ResumenDiarioElectronica.enviarResumenASunat - Inicializamos el ambiente");
        org.apache.xml.security.Init.init();
        String resultado = "";
        String unidadEnvio; // = Util.getPathZipFilesEnvio();
        try {
            log.info("ResumenDiarioElectronica.enviarResumenASunat - Extramo datos para preparar XML ");
            String nrodoc = iddocument;//"1";
            DocumentoBean items = ResumenDespachador.cargarCabecera(nrodoc, conn);
            List<DocumentoBean> resu = ResumenDespachador.cargarResumenDia(items.getResu_identificador(), conn);
            unidadEnvio = Util.getPathZipFilesEnvio(items.getEmpr_nroruc());
            log.info("ResumenDiarioElectronica.enviarResumenASunat - ruta de directorios " + unidadEnvio);
            guardarStatus(nrodoc, "P", conn);
            log.info("ResumenDiarioElectronica.enviarResumenASunat - Inicializamos cabecera ");

            ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "ds");
            //Parametros del keystore
            String keystoreType = Util.getPropertyValue("keystoreType", items.getEmpr_nroruc());//"JKS";
            String keystoreFile = Util.getPropertyValue("keystoreFile", items.getEmpr_nroruc());
            String keystorePass = Util.getPropertyValue("keystorePass", items.getEmpr_nroruc());
            String privateKeyAlias = Util.getPropertyValue("privateKeyAlias", items.getEmpr_nroruc());
            String privateKeyPass = Util.getPropertyValue("privateKeyPass", items.getEmpr_nroruc());
            String certificateAlias = Util.getPropertyValue("certificateAlias", items.getEmpr_nroruc());
            log.info("enviarResumenASunat - Lectura certificado");
            CDATASection cdata;
            if (items != null) {
                log.info("enviarResumenASunat - Iniciamos la generacion del XML");
                String pathXMLFile = unidadEnvio + items.getEmpr_nroruc() + "-" + items.getResu_identificador() + ".xml";
                File signatureFile = new File(pathXMLFile);
                ///////////////////Creación del certificado//////////////////////////////
                KeyStore ks = KeyStore.getInstance(keystoreType);
                FileInputStream fis = new FileInputStream(keystoreFile);
                ks.load(fis, keystorePass.toCharArray());
                //obtener la clave privada para firmar
                PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias, privateKeyPass.toCharArray());
                if (privateKey == null) {
                    throw new RuntimeException("Private key is null");
                }
                X509Certificate cert = (X509Certificate) ks.getCertificate(certificateAlias);
                //////////////////Construyendo XML (DOM)////////////////////////////////
                javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                //Firma XML genera espacio para los nombres o tag
                dbf.setNamespaceAware(true);
                javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.newDocument();
                //-----------Construye el documento----------------
                log.info("enviarResumenASunat - cabecera del XML");
                Element envelope = doc.createElementNS("", "SummaryDocuments");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "urn:sunat:names:specification:ubl:peru:schema:xsd:SummaryDocuments-1");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:sac", "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                envelope.appendChild(doc.createTextNode("\n"));
                //doc.appendChild(doc.createComment(" Preamble "));
                doc.appendChild(envelope);

                Element UBLExtensions = doc.createElementNS("", "ext:UBLExtensions");
                envelope.appendChild(UBLExtensions);
                Element UBLExtension2 = doc.createElementNS("", "ext:UBLExtension");
                UBLExtension2.appendChild(doc.createTextNode("\n"));
                Element ExtensionContent2 = doc.createElementNS("", "ext:ExtensionContent");
                ExtensionContent2.appendChild(doc.createTextNode("\n"));
                //2do grupo
                Element UBLExtension = doc.createElementNS("", "ext:UBLExtension");
                envelope.appendChild(UBLExtension);
                Element ExtensionContent = doc.createElementNS("", "ext:ExtensionContent");
                envelope.appendChild(ExtensionContent);

                String BaseURI = signatureFile.toURI().toURL().toString();
                //Crea un XML Signature objeto desde el documento, BaseURI and signature algorithm (in this case RSA)
                //XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA); Cadena URI que se ajusta a la sintaxis URI y representa el archivo XML de entrada
                XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA);

                ExtensionContent.appendChild(sig.getElement());
                UBLExtension.appendChild(ExtensionContent);
                UBLExtensions.appendChild(UBLExtension);
//bloque1
                Element UBLVersionID = doc.createElementNS("", "cbc:UBLVersionID");
                envelope.appendChild(UBLVersionID);
                UBLVersionID.appendChild(doc.createTextNode("2.0"));

                Element CustomizationID = doc.createElementNS("", "cbc:CustomizationID");
                envelope.appendChild(CustomizationID);
                CustomizationID.appendChild(doc.createTextNode("1.1"));

                Element ID5 = doc.createElementNS("", "cbc:ID");
                envelope.appendChild(ID5);
                ID5.appendChild(doc.createTextNode(items.getResu_identificador()));

                Element ReferenceDate = doc.createElementNS("", "cbc:ReferenceDate");
                envelope.appendChild(ReferenceDate);
                ReferenceDate.appendChild(doc.createTextNode(items.getResu_fecha()));

                Element IssueDate = doc.createElementNS("", "cbc:IssueDate");
                envelope.appendChild(IssueDate);
                IssueDate.appendChild(doc.createTextNode(items.getResu_fec()));

//bloque2 cac:Signature--------------------------------------------------------
                Element Signature = doc.createElementNS("", "cac:Signature");
                envelope.appendChild(Signature);
                Signature.appendChild(doc.createTextNode("\n"));

                Element ID6 = doc.createElementNS("", "cbc:ID");
                Signature.appendChild(ID6);
                ID6.appendChild(doc.createTextNode(items.getEmpr_nroruc()));

                Element SignatoryParty = doc.createElementNS("", "cac:SignatoryParty");
                Signature.appendChild(SignatoryParty);
                SignatoryParty.appendChild(doc.createTextNode("\n"));

                Element PartyIdentification = doc.createElementNS("", "cac:PartyIdentification");
                SignatoryParty.appendChild(PartyIdentification);
                PartyIdentification.appendChild(doc.createTextNode("\n"));

                Element ID7 = doc.createElementNS("", "cbc:ID");
                PartyIdentification.appendChild(ID7);
                ID7.appendChild(doc.createTextNode(items.getEmpr_nroruc()));

                Element PartyName = doc.createElementNS("", "cac:PartyName");
                SignatoryParty.appendChild(PartyName);
                PartyName.appendChild(doc.createTextNode("\n"));

                Element Name = doc.createElementNS("", "cbc:Name");
                PartyName.appendChild(Name);
                cdata = doc.createCDATASection(items.getEmpr_razonsocial().trim());
                Name.appendChild(cdata);

                Element DigitalSignatureAttachment = doc.createElementNS("", "cac:DigitalSignatureAttachment");
                Signature.appendChild(DigitalSignatureAttachment);
                DigitalSignatureAttachment.appendChild(doc.createTextNode("\n"));

                Element ExternalReference = doc.createElementNS("", "cac:ExternalReference");
                DigitalSignatureAttachment.appendChild(ExternalReference);
                ExternalReference.appendChild(doc.createTextNode("\n"));

                Element URI = doc.createElementNS("", "cbc:URI");
                ExternalReference.appendChild(URI);
                URI.appendChild(doc.createTextNode(items.getEmpr_nroruc()));
//bloque3 cac:AccountingSupplierParty-----------------------------------------

                Element AccountingSupplierParty = doc.createElementNS("", "cac:AccountingSupplierParty");
                envelope.appendChild(AccountingSupplierParty);
                AccountingSupplierParty.appendChild(doc.createTextNode("\n"));

                Element CustomerAssignedAccountID = doc.createElementNS("", "cbc:CustomerAssignedAccountID");
                AccountingSupplierParty.appendChild(CustomerAssignedAccountID);
                CustomerAssignedAccountID.appendChild(doc.createTextNode(items.getEmpr_nroruc()));

                Element AdditionalAccountID = doc.createElementNS("", "cbc:AdditionalAccountID");
                AccountingSupplierParty.appendChild(AdditionalAccountID);
                AdditionalAccountID.appendChild(doc.createTextNode("6"));
//***********************************************************
                Element Party = doc.createElementNS("", "cac:Party");
                AccountingSupplierParty.appendChild(Party);
                Party.appendChild(doc.createTextNode("\n"));

                Element PartyLegalEntity = doc.createElementNS("", "cac:PartyLegalEntity");
                Party.appendChild(PartyLegalEntity);//se anade al grupo party
                PartyLegalEntity.appendChild(doc.createTextNode("\n"));

                Element RegistrationName = doc.createElementNS("", "cbc:RegistrationName");
                PartyLegalEntity.appendChild(RegistrationName);//se anade al grupo Country
                cdata = doc.createCDATASection(items.getEmpr_razonsocial().trim());
                RegistrationName.appendChild(cdata);
//*****
                log.info("enviarResumenASunat - Iniciamos detalle XML");
                for (DocumentoBean listaRes : resu) {
                    Element SummaryDocumentsLine = doc.createElementNS("", "sac:SummaryDocumentsLine");
                    envelope.appendChild(SummaryDocumentsLine);
                    SummaryDocumentsLine.appendChild(doc.createTextNode("\n"));

                    Element LineID = doc.createElementNS("", "cbc:LineID");
                    SummaryDocumentsLine.appendChild(LineID);
                    LineID.appendChild(doc.createTextNode(listaRes.getResu_fila().trim()));

                    Element DocumentTypeCode = doc.createElementNS("", "cbc:DocumentTypeCode");
                    SummaryDocumentsLine.appendChild(DocumentTypeCode);
                    DocumentTypeCode.appendChild(doc.createTextNode(listaRes.getDocu_tipodocumento()));

                    Element DocumentNumberID = doc.createElementNS("", "cbc:ID");
                    SummaryDocumentsLine.appendChild(DocumentNumberID);
                    DocumentNumberID.appendChild(doc.createTextNode(listaRes.getDocu_numero()));

                    Element AccountingCustomerParty = doc.createElementNS("", "cac:AccountingCustomerParty");
                    SummaryDocumentsLine.appendChild(AccountingCustomerParty);
                    AccountingCustomerParty.appendChild(doc.createTextNode("\n"));

                    Element CustomerAssignedAccountIDc = doc.createElementNS("", "cbc:CustomerAssignedAccountID");
                    AccountingCustomerParty.appendChild(CustomerAssignedAccountIDc);
                    CustomerAssignedAccountIDc.appendChild(doc.createTextNode(listaRes.getClie_numero()));

                    Element AdditionalAccountIDc = doc.createElementNS("", "cbc:AdditionalAccountID");
                    AccountingCustomerParty.appendChild(AdditionalAccountIDc);
                    AdditionalAccountIDc.appendChild(doc.createTextNode(listaRes.getClie_tipodoc().trim()));

                    // notas referencias inicio solo si corresponde
                    if (!(listaRes.getNota_documento() == null || listaRes.getNota_documento().trim().isEmpty())) {

                        Element BillingReference = doc.createElementNS("", "cac:BillingReference");
                        SummaryDocumentsLine.appendChild(BillingReference);
                        BillingReference.appendChild(doc.createTextNode("\n"));

                        Element InvoiceDocumentReference = doc.createElementNS("", "cac:InvoiceDocumentReference");
                        BillingReference.appendChild(InvoiceDocumentReference);
                        InvoiceDocumentReference.appendChild(doc.createTextNode("\n"));

                        Element DocumentNumberIDRef = doc.createElementNS("", "cbc:ID");
                        InvoiceDocumentReference.appendChild(DocumentNumberIDRef);
                        DocumentNumberIDRef.appendChild(doc.createTextNode(listaRes.getNota_documento()));

                        Element DocumentTypeCodeRef = doc.createElementNS("", "cbc:DocumentTypeCode");
                        InvoiceDocumentReference.appendChild(DocumentTypeCodeRef);
                        DocumentTypeCodeRef.appendChild(doc.createTextNode(listaRes.getNota_tipodoc()));
                    }
                    // notas referencias fin

                    // estado inicio
                    Element Status = doc.createElementNS("", "cac:Status");
                    SummaryDocumentsLine.appendChild(Status);
                    Status.appendChild(doc.createTextNode("\n"));

                    Element ConditionCode = doc.createElementNS("", "cbc:ConditionCode");
                    Status.appendChild(ConditionCode);
                    ConditionCode.appendChild(doc.createTextNode(listaRes.getItem_estado()));

                    // estado fin
                    Element TotalAmount = doc.createElementNS("", "sac:TotalAmount");
                    TotalAmount.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                    TotalAmount.setIdAttributeNS(null, "currencyID", true);
                    SummaryDocumentsLine.appendChild(TotalAmount);
                    TotalAmount.appendChild(doc.createTextNode(listaRes.getDocu_total()));
                    /*1er grupo*/
                    if (!"0.00".equals(listaRes.getDocu_gravada())) {
                        Element BillingPayment = doc.createElementNS("", "sac:BillingPayment");
                        SummaryDocumentsLine.appendChild(BillingPayment);
                        BillingPayment.appendChild(doc.createTextNode("\n"));

                        Element PaidAmount = doc.createElementNS("", "cbc:PaidAmount");
                        PaidAmount.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                        PaidAmount.setIdAttributeNS(null, "currencyID", true);
                        BillingPayment.appendChild(PaidAmount);
                        PaidAmount.appendChild(doc.createTextNode(listaRes.getDocu_gravada()));

                        Element InstructionID = doc.createElementNS("", "cbc:InstructionID");
                        BillingPayment.appendChild(InstructionID);
                        InstructionID.appendChild(doc.createTextNode("01"));
                    }
                    /* 2do grupo*/
                    if (!"0.00".equals(listaRes.getDocu_exonerada())) {
                        Element BillingPayment1 = doc.createElementNS("", "sac:BillingPayment");
                        SummaryDocumentsLine.appendChild(BillingPayment1);
                        BillingPayment1.appendChild(doc.createTextNode("\n"));

                        Element PaidAmount1 = doc.createElementNS("", "cbc:PaidAmount");
                        PaidAmount1.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                        PaidAmount1.setIdAttributeNS(null, "currencyID", true);
                        BillingPayment1.appendChild(PaidAmount1);
                        PaidAmount1.appendChild(doc.createTextNode(listaRes.getDocu_exonerada()));

                        Element InstructionID1 = doc.createElementNS("", "cbc:InstructionID");
                        BillingPayment1.appendChild(InstructionID1);
                        InstructionID1.appendChild(doc.createTextNode("02"));
                    }
                    /*3er grupo*/
                    if (!"0.00".equals(listaRes.getDocu_inafecta())) {
                        Element BillingPayment2 = doc.createElementNS("", "sac:BillingPayment");
                        SummaryDocumentsLine.appendChild(BillingPayment2);
                        BillingPayment2.appendChild(doc.createTextNode("\n"));

                        Element PaidAmount2 = doc.createElementNS("", "cbc:PaidAmount");
                        PaidAmount2.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                        PaidAmount2.setIdAttributeNS(null, "currencyID", true);
                        BillingPayment2.appendChild(PaidAmount2);
                        PaidAmount2.appendChild(doc.createTextNode(listaRes.getDocu_inafecta()));

                        Element InstructionID2 = doc.createElementNS("", "cbc:InstructionID");
                        BillingPayment2.appendChild(InstructionID2);
                        InstructionID2.appendChild(doc.createTextNode("03"));
                    }
                    /*4er grupo*/
                    if (!"0.00".equals(listaRes.getDocu_gratuita())) {
                        Element BillingPayment3 = doc.createElementNS("", "sac:BillingPayment");
                        SummaryDocumentsLine.appendChild(BillingPayment3);
                        BillingPayment3.appendChild(doc.createTextNode("\n"));

                        Element PaidAmount3 = doc.createElementNS("", "cbc:PaidAmount");
                        PaidAmount3.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                        PaidAmount3.setIdAttributeNS(null, "currencyID", true);
                        BillingPayment3.appendChild(PaidAmount3);
                        PaidAmount3.appendChild(doc.createTextNode(listaRes.getDocu_gratuita()));

                        Element InstructionID3 = doc.createElementNS("", "cbc:InstructionID");
                        BillingPayment3.appendChild(InstructionID3);
                        InstructionID3.appendChild(doc.createTextNode("05"));
                    }
                    if (!"0.00".equals(listaRes.getDocu_otroscargos())) {
                        Element AllowanceCharge = doc.createElementNS("", "cac:AllowanceCharge");
                        SummaryDocumentsLine.appendChild(AllowanceCharge);
                        AllowanceCharge.appendChild(doc.createTextNode("\n"));

                        Element ChargeIndicator = doc.createElementNS("", "cbc:ChargeIndicator");
                        AllowanceCharge.appendChild(ChargeIndicator);
                        ChargeIndicator.appendChild(doc.createTextNode("true"));

                        Element Amount = doc.createElementNS("", "cbc:Amount");
                        Amount.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                        Amount.setIdAttributeNS(null, "currencyID", true);
                        AllowanceCharge.appendChild(Amount);
                        Amount.appendChild(doc.createTextNode(listaRes.getDocu_otroscargos()));
                    }
                    //******************************
                    Element TaxTotal = doc.createElementNS("", "cac:TaxTotal");
                    SummaryDocumentsLine.appendChild(TaxTotal);
                    TaxTotal.appendChild(doc.createTextNode("\n"));

                    Element TaxAmount = doc.createElementNS("", "cbc:TaxAmount");
                    TaxAmount.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                    TaxAmount.setIdAttributeNS(null, "currencyID", true);
                    TaxTotal.appendChild(TaxAmount);
                    TaxAmount.appendChild(doc.createTextNode(listaRes.getDocu_isc()));

                    Element TaxSubtotal = doc.createElementNS("", "cac:TaxSubtotal");
                    TaxTotal.appendChild(TaxSubtotal);
                    TaxSubtotal.appendChild(doc.createTextNode("\n"));

                    Element TaxAmount1 = doc.createElementNS("", "cbc:TaxAmount");
                    TaxAmount1.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                    TaxAmount1.setIdAttributeNS(null, "currencyID", true);
                    TaxSubtotal.appendChild(TaxAmount1);
                    TaxAmount1.appendChild(doc.createTextNode(listaRes.getDocu_isc()));

                    Element TaxCategory = doc.createElementNS("", "cac:TaxCategory");
                    TaxSubtotal.appendChild(TaxCategory);
                    TaxCategory.appendChild(doc.createTextNode("\n"));

                    Element TaxScheme = doc.createElementNS("", "cac:TaxScheme");
                    TaxCategory.appendChild(TaxScheme);
                    TaxScheme.appendChild(doc.createTextNode("\n"));

                    Element ID = doc.createElementNS("", "cbc:ID");
                    TaxScheme.appendChild(ID);
                    ID.appendChild(doc.createTextNode("2000"));

                    Element Name1 = doc.createElementNS("", "cbc:Name");
                    TaxScheme.appendChild(Name1);
                    Name1.appendChild(doc.createTextNode("ISC"));

                    Element TaxTypeCode = doc.createElementNS("", "cbc:TaxTypeCode");
                    TaxScheme.appendChild(TaxTypeCode);
                    TaxTypeCode.appendChild(doc.createTextNode("EXC"));

                    ///***************************
                    Element TaxTotal2 = doc.createElementNS("", "cac:TaxTotal");
                    SummaryDocumentsLine.appendChild(TaxTotal2);
                    TaxTotal2.appendChild(doc.createTextNode("\n"));

                    Element TaxAmount2 = doc.createElementNS("", "cbc:TaxAmount");
                    TaxAmount2.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                    TaxAmount2.setIdAttributeNS(null, "currencyID", true);
                    TaxTotal2.appendChild(TaxAmount2);
                    TaxAmount2.appendChild(doc.createTextNode(listaRes.getDocu_igv()));

                    Element TaxSubtotal2 = doc.createElementNS("", "cac:TaxSubtotal");
                    TaxTotal2.appendChild(TaxSubtotal2);
                    TaxSubtotal2.appendChild(doc.createTextNode("\n"));

                    Element TaxAmount3 = doc.createElementNS("", "cbc:TaxAmount");
                    TaxAmount3.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                    TaxAmount3.setIdAttributeNS(null, "currencyID", true);
                    TaxSubtotal2.appendChild(TaxAmount3);
                    TaxAmount3.appendChild(doc.createTextNode(listaRes.getDocu_igv()));

                    Element TaxCategory2 = doc.createElementNS("", "cac:TaxCategory");
                    TaxSubtotal2.appendChild(TaxCategory2);
                    TaxCategory2.appendChild(doc.createTextNode("\n"));

                    Element TaxScheme2 = doc.createElementNS("", "cac:TaxScheme");
                    TaxCategory2.appendChild(TaxScheme2);
                    TaxScheme2.appendChild(doc.createTextNode("\n"));

                    Element ID2 = doc.createElementNS("", "cbc:ID");
                    TaxScheme2.appendChild(ID2);
                    ID2.appendChild(doc.createTextNode("1000"));

                    Element Name12 = doc.createElementNS("", "cbc:Name");
                    TaxScheme2.appendChild(Name12);
                    Name12.appendChild(doc.createTextNode("IGV"));

                    Element TaxTypeCode2 = doc.createElementNS("", "cbc:TaxTypeCode");
                    TaxScheme2.appendChild(TaxTypeCode2);
                    TaxTypeCode2.appendChild(doc.createTextNode("VAT"));
                    ///***************************
                    if (!"0.00".equals(listaRes.getDocu_otrostributos())) {
                        Element TaxTotal3 = doc.createElementNS("", "cac:TaxTotal");
                        SummaryDocumentsLine.appendChild(TaxTotal3);
                        TaxTotal3.appendChild(doc.createTextNode("\n"));

                        Element TaxAmount5 = doc.createElementNS("", "cbc:TaxAmount");
                        TaxAmount5.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                        TaxAmount5.setIdAttributeNS(null, "currencyID", true);
                        TaxTotal3.appendChild(TaxAmount5);
                        TaxAmount5.appendChild(doc.createTextNode(listaRes.getDocu_otrostributos()));

                        Element TaxSubtotal3 = doc.createElementNS("", "cac:TaxSubtotal");
                        TaxTotal3.appendChild(TaxSubtotal3);
                        TaxSubtotal3.appendChild(doc.createTextNode("\n"));

                        Element TaxAmount8 = doc.createElementNS("", "cbc:TaxAmount");
                        TaxAmount8.setAttributeNS(null, "currencyID", listaRes.getDocu_moneda());
                        TaxAmount8.setIdAttributeNS(null, "currencyID", true);
                        TaxSubtotal3.appendChild(TaxAmount8);
                        TaxAmount8.appendChild(doc.createTextNode(listaRes.getDocu_otrostributos()));

                        Element TaxCategory8 = doc.createElementNS("", "cac:TaxCategory");
                        TaxSubtotal3.appendChild(TaxCategory8);
                        TaxCategory8.appendChild(doc.createTextNode("\n"));

                        Element TaxScheme9 = doc.createElementNS("", "cac:TaxScheme");
                        TaxCategory8.appendChild(TaxScheme9);
                        TaxScheme9.appendChild(doc.createTextNode("\n"));

                        Element ID8 = doc.createElementNS("", "cbc:ID");
                        TaxScheme9.appendChild(ID8);
                        ID8.appendChild(doc.createTextNode("9999"));

                        Element Name18 = doc.createElementNS("", "cbc:Name");
                        TaxScheme9.appendChild(Name18);
                        Name18.appendChild(doc.createTextNode("OTROS"));

                        Element TaxTypeCode28 = doc.createElementNS("", "cbc:TaxTypeCode");
                        TaxScheme9.appendChild(TaxTypeCode28);
                        TaxTypeCode28.appendChild(doc.createTextNode("OTH"));
                    }

                }
                //detalle factura
                log.info("enviarResumenASunat - Prepara firma digital");
                sig.setId(items.getEmpr_nroruc());
                sig.addKeyInfo(cert);
                {
                    //crean las transformaciones de objetos para el Documento/Referencia
                    Transforms transforms = new Transforms(doc);
                    //Parte del elemento de la firma necesita ser canónica . Es un tipo de algoritmo para la normalización de XML. 
                    //Para obtener más información, por favor , eche un vistazo a la página web de la firma W3C XML Digital.
                    transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);

                    //Añade los datos del Documento/Referencia
                    sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
                }
                {
                    log.info("enviarResumenASunat - Firma XML");
                    sig.sign(privateKey);

                }
                //--------------------fin de construccion del xml---------------------
                ///*combinacion de firma y construccion xml////
                FileOutputStream f = new FileOutputStream(signatureFile);
                Transformer tf = TransformerFactory.newInstance().newTransformer();
                tf.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
                //tf.setOutputProperty(OutputKeys.INDENT, "yes");
                tf.setOutputProperty(OutputKeys.STANDALONE, "no");
                //Writer out = new StringWriter();
                StreamResult sr = new StreamResult(f);
                tf.transform(new DOMSource(doc), sr);
                sr.getOutputStream().close();

                log.info("enviarResumenASunat - XML creado " + pathXMLFile);

                /*for (int i = 0; i < sig.getSignedInfo().getSignedContentLength(); i++) {
             System.out.println("--- Signed Content follows ---");
             System.out.println(new String(sig.getSignedInfo().getSignedContentItem(i)));
             }*/
                //Mandar a zip
                log.info("enviarResumenASunat - crear zip");
                String inputFile = signatureFile.toString();
                FileInputStream in = new FileInputStream(inputFile);
                FileOutputStream out = new FileOutputStream(unidadEnvio + items.getEmpr_nroruc() + "-" + items.getResu_identificador() + ".zip");

                byte b[] = new byte[2048];
                try (ZipOutputStream zipOut = new ZipOutputStream(out)) {
                    ZipEntry entry2 = new ZipEntry(items.getEmpr_nroruc() + "-" + items.getResu_identificador() + ".xml");
                    zipOut.putNextEntry(entry2);
                    System.out.println("==>Zip generado: " + items.getEmpr_nroruc() + "-" + items.getResu_identificador() + ".zip");
                    int len = 0;
                    while ((len = in.read(b)) != -1) {
                        zipOut.write(b, 0, len);
                    }
                    zipOut.closeEntry();
                }
                out.close();
                in.close();
                log.info("enviarResumenASunat - ZIP creado " + items.getEmpr_nroruc() + "-" + items.getResu_identificador() + ".zip");
                /*=======================guardarHashYBarCodePDF417*/
                //LecturaXML.guardarHashYBarCodePDF417(nrodoc, "RS",pathXMLFile);
                /*=======================ENVIO A SUNAT=============*/
                guardarStatus(nrodoc, "E", conn);
                log.info("enviarResumenASunat - Prepara para enviar a SUNAP");
                resultado = enviarZipASunat(iddocument, unidadEnvio, items.getEmpr_nroruc() + "-" + items.getResu_identificador() + ".zip", conn, items.getEmpr_nroruc());
                guardarStatus(nrodoc, "O", conn);
                log.info("enviarResumenASunat - creado correctamente" + items.getEmpr_nroruc() + "-" + items.getResu_identificador());
                //resultado = "termino de generar el archivo xml de la Resumen ";

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("enviarResumenASunat - Error: " + ex.toString());
            resultado = "Error al generar el archivo de formato xml de la Resumen.";
        }
        return resultado;
    }

    public static String enviarZipASunat(String iddocument, String unidadEnvio, String zipFileName, Connection conn, String vruc) {
        String resultado = "";
        String sws = Util.getWsOpcion(vruc);
        log.info("enviarZipASunat - Prepara ambiente: " + sws);
        try {
            System.out.println("===>Conversion de zip a byte[]...");
//            BillService_Service_fe ws = new BillService_Service_fe();
//            HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
//            ws.setHandlerResolver(handlerResolver);
//            BillService port = ws.getBillServicePort();
            javax.activation.FileDataSource fileDataSource = new javax.activation.FileDataSource(unidadEnvio + zipFileName);
            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(fileDataSource);
            //resultado = port.sendSummary(zipFileName, dataHandler);
            switch (sws) {
                case "1":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_fe ws1 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_fe();
                    HeaderHandlerResolver handlerResolver1 = new HeaderHandlerResolver();
                    handlerResolver1.setVruc(vruc);
                    ws1.setHandlerResolver(handlerResolver1);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService port1 = ws1.getBillServicePort();
                    resultado = port1.sendSummary(zipFileName, dataHandler);
                    log.info("enviarZipASunat - enviado al ambiente beta: " + sws);
                    break;
                case "2":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa ws = new pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa();
                    HeaderHandlerResolver handlerResolver2 = new HeaderHandlerResolver();
                    handlerResolver2.setVruc(vruc);
                    ws.setHandlerResolver(handlerResolver2);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService port2 = ws.getBillServicePort();
                    resultado = port2.sendSummary(zipFileName, dataHandler);
                    log.info("enviarZipASunat - enviado al ambiente QA: " + sws);
                    break;
                case "3":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service_fe ws3 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service_fe();
                    HeaderHandlerResolver handlerResolver3 = new HeaderHandlerResolver();
                    handlerResolver3.setVruc(vruc);
                    ws3.setHandlerResolver(handlerResolver3);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService port3 = ws3.getBillServicePort();
                    resultado = port3.sendSummary(zipFileName, dataHandler);
                    log.info("enviarZipASunat - enviado al ambiente produccion: " + sws);
                    break;
                default:

                    resultado = "";
                    log.error("enviarZipASunat - no existe ambiente " + sws);

                    throw new com.org.exceptiones.NoExisteWSSunatException(sws);
            }

            String sql = "update resumendia_cab set nroticket=?, resu_proce_status='O', resu_fecha_com = ? where codigo=?";
//            conn = ConnectionPool.obtenerConexionMysql();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar ticket
            ps = conn.prepareStatement(sql);
            ps.setString(1, resultado);
            ps.setString(2, sdf.format(new Date()));
            ps.setString(3, iddocument);
            log.info("enviarZipASunat - ticket " + resultado);

            int reg = ps.executeUpdate();
            if (reg > 0) {
                System.out.println("==>Ticket grabado");
            } else {
                System.out.println("==>Error en grabar Ticket");
            }
            resultado = "0|" + resultado;
            System.out.println("==>El envio del Zip a sunat fue exitoso");
        } catch (javax.xml.ws.soap.SOAPFaultException ex) {
            log.error("enviarZipASunat - error: " + ex.toString());
            String errorCode = ex.getFault().getFaultCodeAsQName().getLocalPart();
            errorCode = errorCode.substring(errorCode.indexOf(".") + 1, errorCode.length());
            resultado = Util.getErrorMesageByCode(errorCode, vruc);
            log.error("enviarZipASunat - error: " + resultado);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("enviarZipASunat - Error " + e.toString());
            resultado = "-1|Error en el envio de archivo resumen zip a sunat.";
        }
        return resultado;
    }

    public static void guardarStatus(String iddocument, String status, Connection conn) {
        String resultado = "";
        try {

            //=== Guardar el Ticket
            String sql = "update resumendia_cab set resu_proce_status=? where codigo=?";
//            conn = ConnectionPool.obtenerConexionMysql();

            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar ticket
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, iddocument);

            int reg = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
