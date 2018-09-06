/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.ws;

/**
 *
 * @author oswaldo
 */
import com.org.dispatcher.WSDispatcher;
import com.org.factories.Util;
import com.org.model.beans.DocumentoBean;
import com.org.model.despatchers.DElectronicoDespachador;
import com.org.util.EjecutorReporte;
import com.org.util.EnvioMail;
import com.org.util.Externo;
import com.org.util.GeneralFunctions;
import com.org.util.LecturaXML;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

public class RetElectronica {

    private static Log log = LogFactory.getLog(RetElectronica.class);

    static {
        org.apache.xml.security.Init.init();
    }

    public static String generarXMLZipiadoRetencion(String iddocument, Connection conn) {
        log.info("RetElectronica.generarXMLZipiadoRetencion - Inicializamos el ambiente");
        org.apache.xml.security.Init.init();
        String resultado = "";
        String nrodoc = iddocument;//"943270";// request.getParameter("nrodoc");68
        String unidadEnvio; // = Util.getPathZipFilesEnvio();
        try {
            //String nrodoc = iddocument;//"943270";// request.getParameter("nrodoc");68
            log.info("RetElectronica.generarXMLZipiadoRetencion - Extraemos datos para preparar XML ");
            DocumentoBean items = DElectronicoDespachador.cargarDocElectronico(nrodoc, conn);
            List<DocumentoBean> detdocelec = DElectronicoDespachador.cargarDetDocElectronico(nrodoc, conn);
            unidadEnvio = Util.getPathZipFilesEnvio(items.getEmpr_nroruc());
            log.info("RetElectronica.generarXMLZipiadoRetencion - ruta de directorios " + unidadEnvio);
            LecturaXML.guardarProcesoEstado(nrodoc, "P", " | ".split("\\|", 0), "", conn);
            log.info("RetElectronica.generarXMLZipiadoRetencion - Inicializamos cabecera ");
            ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "ds");
            //Parametros del keystore
            String keystoreType = Util.getPropertyValue("keystoreType", items.getEmpr_nroruc());//"JKS";
            String keystoreFile = Util.getPropertyValue("keystoreFile", items.getEmpr_nroruc());
            String keystorePass = Util.getPropertyValue("keystorePass", items.getEmpr_nroruc());
            String privateKeyAlias = Util.getPropertyValue("privateKeyAlias", items.getEmpr_nroruc());
            String privateKeyPass = Util.getPropertyValue("privateKeyPass", items.getEmpr_nroruc());
            String certificateAlias = Util.getPropertyValue("certificateAlias", items.getEmpr_nroruc());
            log.info("RetElectronica.generarXMLZipiadoRetencion - Lectura de certificado ");
            CDATASection cdata;
            if (items != null) {
                log.info("RetElectronica.generarXMLZipiadoRetencion - Inicializamos la generacion del XML ");
                String pathXMLFile = unidadEnvio + items.getEmpr_nroruc() + "-20-" + items.getDocu_numero() + ".xml";
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
                //////////////////////////////////////////////////
                javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                //Firma XML genera espacio para los nombres o tag
                dbf.setNamespaceAware(true);
                javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.newDocument();
                ////////////////////////////////////////////////// 
                log.info("RetElectronica.generarXMLZipiadoRetencion - cabecera de XML");
                Element envelope = doc.createElementNS("", "Retention");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "urn:sunat:names:specification:ubl:peru:schema:xsd:Retention-1");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:ccts", "urn:un:unece:uncefact:documentation:2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:qdt", "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:sac", "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:udt", "urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                envelope.appendChild(doc.createTextNode("\n"));
                //doc.appendChild(doc.createComment(" Preamble "));
                doc.appendChild(envelope);
                //doc.appendChild(doc.createComment(" Postamble "));

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

                //El baseURI es la URI que se utiliza para anteponer a URIs relativos
                String BaseURI = signatureFile.toURI().toURL().toString();
                //Crea un XML Signature objeto desde el documento, BaseURI and signature algorithm (in this case RSA)
                //XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA); Cadena URI que se ajusta a la sintaxis URI y representa el archivo XML de entrada
                XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA);

                ExtensionContent.appendChild(sig.getElement());
                UBLExtension.appendChild(ExtensionContent);
                UBLExtensions.appendChild(UBLExtension);
                //ExtensionContent2.appendChild(AdditionalInformation);

                //1.-
                Element UBLVersionID = doc.createElementNS("", "cbc:UBLVersionID");
                envelope.appendChild(UBLVersionID);
                UBLVersionID.appendChild(doc.createTextNode("2.0"));
                //2.-
                Element CustomizationID = doc.createElementNS("", "cbc:CustomizationID");
                envelope.appendChild(CustomizationID);
                CustomizationID.appendChild(doc.createTextNode("1.0"));

//bloque2 cac:Signature--------------------------------------------------------
                Element Signature = doc.createElementNS("", "cac:Signature");
                envelope.appendChild(Signature);
                Signature.appendChild(doc.createTextNode("\n"));

                Element ID6 = doc.createElementNS("", "cbc:ID");
                Signature.appendChild(ID6);
                ID6.appendChild(doc.createTextNode("IDSignKG"));
                //ID6.appendChild(doc.createTextNode(items.getEmpr_nroruc().trim()));

                Element SignatoryParty = doc.createElementNS("", "cac:SignatoryParty");
                Signature.appendChild(SignatoryParty);
                SignatoryParty.appendChild(doc.createTextNode("\n"));

                Element PartyIdentification = doc.createElementNS("", "cac:PartyIdentification");
                SignatoryParty.appendChild(PartyIdentification);
                PartyIdentification.appendChild(doc.createTextNode("\n"));

                Element ID7 = doc.createElementNS("", "cbc:ID");
                PartyIdentification.appendChild(ID7);
                ID7.appendChild(doc.createTextNode(items.getEmpr_nroruc().trim()));

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
                URI.appendChild(doc.createTextNode("#signatureKG"));
                //URI.appendChild(doc.createTextNode(items.getEmpr_nroruc().trim()));

//
                Element ID5 = doc.createElementNS("", "cbc:ID");
                envelope.appendChild(ID5);
                ID5.appendChild(doc.createTextNode(items.getDocu_numero().trim()));

                Element IssueDate = doc.createElementNS("", "cbc:IssueDate");
                envelope.appendChild(IssueDate);
                IssueDate.appendChild(doc.createTextNode(items.getDocu_fecha().trim()));

//                Element InvoiceTypeCode = doc.createElementNS("", "cbc:InvoiceTypeCode");
//                envelope.appendChild(InvoiceTypeCode);
//                InvoiceTypeCode.appendChild(doc.createTextNode(items.getDocu_tipodocumento().trim()));//DIFERENCIA ENTRE FAC Y ND Y NC
//bloque3 cac:AgentParty-----------------------------------------
                Element AgentParty = doc.createElementNS("", "cac:AgentParty");
                envelope.appendChild(AgentParty);
                AgentParty.appendChild(doc.createTextNode("\n"));

                Element CustomerAssignedAccountID = doc.createElementNS("", "cac:PartyIdentification");
                AgentParty.appendChild(CustomerAssignedAccountID);
                CustomerAssignedAccountID.appendChild(doc.createTextNode("\n"));

                Element AdditionalAccountID = doc.createElementNS("", "cbc:ID");
                AdditionalAccountID.setAttributeNS(null, "schemeID", items.getEmpr_tipodoc().trim());
                AdditionalAccountID.setIdAttributeNS(null, "schemeID", true);
                CustomerAssignedAccountID.appendChild(AdditionalAccountID);
                AdditionalAccountID.appendChild(doc.createTextNode(items.getEmpr_nroruc().trim()));
//***********************************************************
                Element PartyName1 = doc.createElementNS("", "cac:PartyName");
                AgentParty.appendChild(PartyName1);//se anade al grupo party
                PartyName1.appendChild(doc.createTextNode("\n"));

                Element Name2 = doc.createElementNS("", "cbc:Name");
                PartyName1.appendChild(Name2);//se anade al grupo partyname1
                cdata = doc.createCDATASection(items.getEmpr_razonsocial().trim());
                Name2.appendChild(cdata);

                Element PostalAddress = doc.createElementNS("", "cac:PostalAddress");
                AgentParty.appendChild(PostalAddress);//se anade al grupo party
                PostalAddress.appendChild(doc.createTextNode("\n"));

                Element ID8 = doc.createElementNS("", "cbc:ID");
                PostalAddress.appendChild(ID8);//se anade al grupo PostalAddress
                ID8.appendChild(doc.createTextNode(items.getEmpr_ubigeo().trim()));

                Element StreetName = doc.createElementNS("", "cbc:StreetName");
                PostalAddress.appendChild(StreetName);//se anade al grupo PostalAddress
                cdata = doc.createCDATASection(items.getEmpr_direccion().trim());
                StreetName.appendChild(cdata);

                Element CityName = doc.createElementNS("", "cbc:CityName");
                PostalAddress.appendChild(CityName);//se anade al grupo PostalAddress
                cdata = doc.createCDATASection(items.getEmpr_provincia().trim());
                CityName.appendChild(cdata);

                Element CountrySubentity = doc.createElementNS("", "cbc:CountrySubentity");
                PostalAddress.appendChild(CountrySubentity);//se anade al grupo PostalAddress
                cdata = doc.createCDATASection(items.getEmpr_departamento().trim());
                CountrySubentity.appendChild(cdata);

                Element District = doc.createElementNS("", "cbc:District");
                PostalAddress.appendChild(District);//se anade al grupo PostalAddress
                cdata = doc.createCDATASection(items.getEmpr_distrito().trim());
                District.appendChild(cdata);

                Element Country = doc.createElementNS("", "cac:Country");
                PostalAddress.appendChild(Country);//se anade al grupo PostalAddress
                Country.appendChild(doc.createTextNode("\n"));

                Element IdentificationCode = doc.createElementNS("", "cbc:IdentificationCode");
                Country.appendChild(IdentificationCode);//se anade al grupo Country
                cdata = doc.createCDATASection(items.getEmpr_pais().trim());
                IdentificationCode.appendChild(cdata);

                Element PartyLegalEntity = doc.createElementNS("", "cac:PartyLegalEntity");
                AgentParty.appendChild(PartyLegalEntity);//se anade al grupo party
                PartyLegalEntity.appendChild(doc.createTextNode("\n"));

                Element RegistrationName = doc.createElementNS("", "cbc:RegistrationName");
                PartyLegalEntity.appendChild(RegistrationName);//se anade al grupo Country
                cdata = doc.createCDATASection(items.getEmpr_razonsocial().trim());
                RegistrationName.appendChild(cdata);
// cac:ReceiverParty---
                Element ReceiverParty = doc.createElementNS("", "cac:ReceiverParty");
                envelope.appendChild(ReceiverParty);
                ReceiverParty.appendChild(doc.createTextNode("\n"));

                Element ReceiverPartyIdentification = doc.createElementNS("", "cac:PartyIdentification");
                ReceiverParty.appendChild(ReceiverPartyIdentification);//se anade al grupo AccountingCustomerParty
                ReceiverPartyIdentification.appendChild(doc.createTextNode("\n"));

                Element AdditionalAccountID1 = doc.createElementNS("", "cbc:ID");
                AdditionalAccountID1.setAttributeNS(null, "schemeID", items.getClie_tipodoc().trim());
                AdditionalAccountID1.setIdAttributeNS(null, "schemeID", true);
                ReceiverPartyIdentification.appendChild(AdditionalAccountID1);
                AdditionalAccountID1.appendChild(doc.createTextNode(items.getClie_numero().trim()));

                Element PartyName2 = doc.createElementNS("", "cac:PartyName");
                ReceiverParty.appendChild(PartyName2);//se anade al grupo Party1
                PartyName2.appendChild(doc.createTextNode("\n"));

                Element RegistrationName2 = doc.createElementNS("", "cbc:Name");
                PartyName2.appendChild(RegistrationName2);//se anade al grupo PartyLegalEntity1
                cdata = doc.createCDATASection(items.getClie_nombre().trim());
                RegistrationName2.appendChild(cdata);

                Element PartyLegalEntity1 = doc.createElementNS("", "cac:PartyLegalEntity");
                ReceiverParty.appendChild(PartyLegalEntity1);//se anade al grupo Party1
                PartyLegalEntity1.appendChild(doc.createTextNode("\n"));

                Element RegistrationName1 = doc.createElementNS("", "cbc:RegistrationName");
                PartyLegalEntity1.appendChild(RegistrationName1);//se anade al grupo PartyLegalEntity1
                cdata = doc.createCDATASection(items.getClie_nombre().trim());
                RegistrationName1.appendChild(cdata);

//bloque 5
                Element RetencionCode = doc.createElementNS("", "sac:SUNATRetentionSystemCode");
                envelope.appendChild(RetencionCode);
                RetencionCode.appendChild(doc.createTextNode(items.getRete_regi().trim()));

                Element RetencionPercent = doc.createElementNS("", "sac:SUNATRetentionPercent");
                envelope.appendChild(RetencionPercent);
                RetencionPercent.appendChild(doc.createTextNode(items.getRete_tasa().trim()));

                Element RetencionNota = doc.createElementNS("", "cbc:Note");
                envelope.appendChild(RetencionNota);
                cdata = doc.createCDATASection(items.getNota_documento().trim());
                RetencionNota.appendChild(cdata);

                Element TotalInvoiceAmount = doc.createElementNS("", "cbc:TotalInvoiceAmount");
                TotalInvoiceAmount.setAttributeNS(null, "currencyID", items.getDocu_moneda().trim());
                TotalInvoiceAmount.setIdAttributeNS(null, "currencyID", true);
                envelope.appendChild(TotalInvoiceAmount);//se anade al grupo TaxTotal
                TotalInvoiceAmount.appendChild(doc.createTextNode(items.getDocu_total().trim()));

                Element SUNATTotalPaid = doc.createElementNS("", "sac:SUNATTotalPaid");
                SUNATTotalPaid.setAttributeNS(null, "currencyID", items.getDocu_moneda().trim());
                SUNATTotalPaid.setIdAttributeNS(null, "currencyID", true);
                envelope.appendChild(SUNATTotalPaid);//se anade al grupo TaxTotal
                SUNATTotalPaid.appendChild(doc.createTextNode(items.getRete_total_elec().trim()));

                log.info("RetElectronica.generarXMLZipiadoRetencion - Inicializamos detalle XML ");
                for (DocumentoBean listaDet : detdocelec) {
                    Element InvoiceLine = doc.createElementNS("", "sac:SUNATRetentionDocumentReference");
                    envelope.appendChild(InvoiceLine);
                    InvoiceLine.appendChild(doc.createTextNode("\n"));

                    Element ID11 = doc.createElementNS("", "cbc:ID");
                    ID11.setAttributeNS(null, "schemeID", listaDet.getRete_rela_tipo_docu().trim());
                    ID11.setIdAttributeNS(null, "schemeID", true);
                    InvoiceLine.appendChild(ID11);//se anade al grupo InvoiceLine
                    ID11.appendChild(doc.createTextNode(listaDet.getRete_rela_nume_docu().trim()));

                    Element DetaIssueDate = doc.createElementNS("", "cbc:IssueDate");
                    InvoiceLine.appendChild(DetaIssueDate);
                    DetaIssueDate.appendChild(doc.createTextNode(listaDet.getRete_rela_fech_docu().trim()));

                    Element DetaTotalInvoiceAmount = doc.createElementNS("", "cbc:TotalInvoiceAmount");
                    DetaTotalInvoiceAmount.setAttributeNS(null, "currencyID", listaDet.getRete_rela_tipo_moneda().trim());
                    DetaTotalInvoiceAmount.setIdAttributeNS(null, "currencyID", true);
                    InvoiceLine.appendChild(DetaTotalInvoiceAmount);//se anade al grupo InvoiceLine
                    DetaTotalInvoiceAmount.appendChild(doc.createTextNode(listaDet.getRete_rela_total_original().trim()));

                    Element DetPayment = doc.createElementNS("", "cac:Payment");
                    InvoiceLine.appendChild(DetPayment);
                    DetPayment.appendChild(doc.createTextNode("\n"));

                    Element ID12 = doc.createElementNS("", "cbc:ID");
                    DetPayment.appendChild(ID12);//se anade al grupo InvoiceLine
                    ID12.appendChild(doc.createTextNode(listaDet.getRete_rela_numero_pago().trim()));

                    Element DetaPaidAmount = doc.createElementNS("", "cbc:PaidAmount");
                    DetaPaidAmount.setAttributeNS(null, "currencyID", listaDet.getRete_rela_tipo_moneda().trim());
                    DetaPaidAmount.setIdAttributeNS(null, "currencyID", true);
                    DetPayment.appendChild(DetaPaidAmount);//se anade al grupo InvoiceLine
                    //DetaPaidAmount.appendChild(doc.createTextNode(listaDet.getRete_rela_total_original().trim()));
                    DetaPaidAmount.appendChild(doc.createTextNode(listaDet.getRete_rela_importe_pagado_original().trim()));

                    Element DetaPaidDate = doc.createElementNS("", "cbc:PaidDate");
                    DetPayment.appendChild(DetaPaidDate);
                    DetaPaidDate.appendChild(doc.createTextNode(listaDet.getRete_rela_fecha_pago().trim()));

                    Element DetSUNATRetentionInformation = doc.createElementNS("", "sac:SUNATRetentionInformation");
                    InvoiceLine.appendChild(DetSUNATRetentionInformation);
                    DetSUNATRetentionInformation.appendChild(doc.createTextNode("\n"));

                    Element DetaSUNATRetentionAmount = doc.createElementNS("", "sac:SUNATRetentionAmount");
                    DetaSUNATRetentionAmount.setAttributeNS(null, "currencyID", "PEN");
                    DetaSUNATRetentionAmount.setIdAttributeNS(null, "currencyID", true);
                    DetSUNATRetentionInformation.appendChild(DetaSUNATRetentionAmount);//se anade al grupo InvoiceLine
                    DetaSUNATRetentionAmount.appendChild(doc.createTextNode(listaDet.getRete_importe_retenido_nacional().trim()));

                    Element DetaSUNATRetentionDate = doc.createElementNS("", "sac:SUNATRetentionDate");
                    DetSUNATRetentionInformation.appendChild(DetaSUNATRetentionDate);
                    DetaSUNATRetentionDate.appendChild(doc.createTextNode(listaDet.getRete_rela_fecha_pago().trim()));

                    Element DetaSUNATNetTotalPaid = doc.createElementNS("", "sac:SUNATNetTotalPaid");
                    DetaSUNATNetTotalPaid.setAttributeNS(null, "currencyID", "PEN");
                    DetaSUNATNetTotalPaid.setIdAttributeNS(null, "currencyID", true);
                    DetSUNATRetentionInformation.appendChild(DetaSUNATNetTotalPaid);//se anade al grupo InvoiceLine
                    DetaSUNATNetTotalPaid.appendChild(doc.createTextNode(listaDet.getRete_importe_neto_nacional().trim()));

                    Element DetExchangeRate = doc.createElementNS("", "cac:ExchangeRate");
                    DetSUNATRetentionInformation.appendChild(DetExchangeRate);
                    DetExchangeRate.appendChild(doc.createTextNode("\n"));

                    Element DetaSourceCurrencyCode = doc.createElementNS("", "cbc:SourceCurrencyCode");
                    DetExchangeRate.appendChild(DetaSourceCurrencyCode);//se anade al grupo InvoiceLine
                    DetaSourceCurrencyCode.appendChild(doc.createTextNode(listaDet.getRete_tipo_moneda_referencia().trim()));

                    Element DetaTargetCurrencyCode = doc.createElementNS("", "cbc:TargetCurrencyCode");
                    DetExchangeRate.appendChild(DetaTargetCurrencyCode);//se anade al grupo InvoiceLine
                    DetaTargetCurrencyCode.appendChild(doc.createTextNode(listaDet.getRete_tipo_moneda_objetivo() == null ? "PEN" : listaDet.getRete_tipo_moneda_objetivo().trim())); // revisar

                    Element DetaCalculationRate = doc.createElementNS("", "cbc:CalculationRate");
                    DetExchangeRate.appendChild(DetaCalculationRate);//se anade al grupo InvoiceLine
                    DetaCalculationRate.appendChild(doc.createTextNode(listaDet.getRete_tipo_moneda_tipo_cambio() == null ? "1.00" : listaDet.getRete_tipo_moneda_tipo_cambio().equals("PEN") ? "1.00" : listaDet.getRete_tipo_moneda_tipo_cambio().trim())); // revisar

                    Element DetaDate = doc.createElementNS("", "cbc:Date");
                    DetExchangeRate.appendChild(DetaDate);//se anade al grupo InvoiceLine
                    DetaDate.appendChild(doc.createTextNode(listaDet.getRete_rela_fecha_pago().trim())); // revisar
                }

                log.info("RetElectronica.generarXMLZipiadoRetencion - Prepara Firma digital");
                sig.setId(items.getEmpr_nroruc());
                sig.addKeyInfo(cert);
                {
                    Transforms transforms = new Transforms(doc);
                    transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
                    sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
                }
                /* 
                 * Add KeyInfo and sign() 
                 */
                {
                    //Firmar el documento
                    log.info("RetElectronica.generarXMLZipiadoRetencion - Firma del XML ");
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

                log.info("RetElectronica.generarXMLZipiadoRetencion - XML creado " + pathXMLFile);

                //Mandar a zip
                log.info("RetElectronica.generarXMLZipiadoRetencion - Crear Zip ");
                String inputFile = signatureFile.toString();
                FileInputStream in = new FileInputStream(inputFile);
                FileOutputStream out = new FileOutputStream(unidadEnvio + items.getEmpr_nroruc() + "-20-" + items.getDocu_numero() + ".zip");

                byte b[] = new byte[2048];
                try (ZipOutputStream zipOut = new ZipOutputStream(out)) {
                    ZipEntry entry2 = new ZipEntry(items.getEmpr_nroruc() + "-20-" + items.getDocu_numero() + ".xml");
                    zipOut.putNextEntry(entry2);
                    System.out.println("==>Zip generado: " + items.getEmpr_nroruc() + "-20-" + items.getDocu_numero() + ".zip");
                    int len = 0;
                    while ((len = in.read(b)) != -1) {
                        zipOut.write(b, 0, len);
                    }
                    zipOut.closeEntry();
                } catch (Exception e) {
                }
                out.close();
                in.close();
                log.info("RetElectronica.generarXMLZipiadoRetencion - Zip creado" + unidadEnvio + items.getEmpr_nroruc() + "-20-" + items.getDocu_numero() + ".zip");
                //======================guardar Hash Y Barcode PDF417 =======================
                log.info("RetElectronica.generarXMLZipiadoRetencion - Crear Hashcode y CodeBarPDF417");
//                LecturaXML.guardarHashYBarCodePDF417(items.getDocu_tipodocumento(), nrodoc, "DV", pathXMLFile, conn);
                LecturaXML.guardarHashYBarCodeQR(items.getDocu_tipodocumento(), nrodoc, "DV", pathXMLFile, conn);
                //======================guardar PDF =======================
                log.info("RetElectronica.generarXMLZipiadoRetencion - Crear PDF ");
                Map<String, Object> parametros = new HashMap<String, Object>();
                parametros.put("P_docu", nrodoc);
                parametros.put("SUBREPORT_DIR", Util.getPathJasperFiles(items.getEmpr_nroruc()));
                parametros.put("P_url", Util.getPurl(items.getEmpr_nroruc()));
                parametros.put("P_resolucion", Util.getPResolucion(items.getEmpr_nroruc()));

                EjecutorReporte executor = new EjecutorReporte(Util.getPathJasperFiles(items.getEmpr_nroruc()) + "RetencionElectronica.jasper",
                        parametros, unidadEnvio + items.getEmpr_nroruc() + "-20-" + items.getDocu_numero() + ".pdf", Util.getPathFilesLogo(items.getEmpr_nroruc()) + "LogoEmpresa.gif");
                executor.execute(conn);
                log.info("RetElectronica.generarXMLZipiadoRetencion - PDF creado " + unidadEnvio + items.getEmpr_nroruc() + "-20-" + items.getDocu_numero() + ".pdf");
                //====================== SUBIR PDF AL FTP INDICADO =======================
                resultado = GeneralFunctions.copiaAFtp(items, unidadEnvio, conn);
                /*=======================EVALUANDO EMPRESA =============*/
                log.info("generarXMLZipiadoFactura - Evaluando Empresa para crear Link");
                Externo.creaEmpresa(items.getClie_numero(), items.getClie_nombre(), items.getClie_correo_cpe0(), conn);
                /*=======================ENVIO A SUNAT=============*/
                log.info("RetElectronica.generarXMLZipiadoRetencion - Preparando para enviar a SUNAT");
                LecturaXML.guardarProcesoEstado(nrodoc, "E", " | ".split("\\|", 0), "", conn);
                resultado = WSDispatcher.enviarRetencionesASunat(nrodoc, unidadEnvio, items.getEmpr_nroruc() + "-20-" + items.getDocu_numero() + ".zip", conn, items.getEmpr_nroruc());

                //resultado="termino de generar el archivo xml de la factura";
                /*=======================ENVIO CORREO AL CLIENTE=============*/
                resultado = GeneralFunctions.enviarEmail(items, unidadEnvio, conn, resultado, " COMPROBANTE DE FACTURA ELECTRONICA ");
                log.info("RetElectronica.generarXMLZipiadoRetencion - Proceso envio correo correctamente  " + items.getEmpr_nroruc() + "-20-" + items.getDocu_numero());
                LecturaXML.guardarProcesoEstado(nrodoc, "O", resultado.split("\\|", 0), "", conn);

            }

        } catch (Exception ex) {
            //ex.printStackTrace();
            resultado = "0100|Error al generar el archivo de formato xml de la Retencion.";
            Logger.getLogger(RetElectronica.class.getName()).log(Level.SEVERE, null, ex);
            log.error("generarXMLZipiadoRetencion - error " + ex.toString());
            try {
                LecturaXML.guardarProcesoEstado(iddocument, "Q", resultado.split("\\|", 0), ex.toString(), conn);
            } catch (SQLException ex1) {
                log.error("generarXMLZipiadoBoleta - error  " + ex1.toString());
            }
        }
//        try {
//            LecturaXML.guardarProcesoEstado(nrodoc, "O", resultado.split("\\|", 0), conn);
//        } catch (SQLException ex) {
//            Logger.getLogger(RetElectronica.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return resultado;
    }

}
