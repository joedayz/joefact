/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.ws;

import com.org.dispatcher.WSDispatcher;
import com.org.factories.Util;
import com.org.model.beans.DocumentoBean;
import com.org.model.despatchers.ResumenBajaDespachador;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

/**
 *
 * @author vpipa
 */
public class ResumenReversion {

    private static Log log = LogFactory.getLog(ResumenReversion.class);

    public static String enviarReversionASunat(String iddocument, Connection conn) {
        log.info("ResumenReversion.enviarReversionASunat - Inicializamos el ambiente");
        org.apache.xml.security.Init.init();
        String resultado = "";
        String unidadEnvio; // = Util.getPathZipFilesEnvio();
        try {
            String nrodoc = iddocument;//"1";
            log.info("ResumenReversion.enviarReversionASunat - extraemos datos para generar XML ");
            DocumentoBean items = ResumenBajaDespachador.cargarCabecera(nrodoc, conn);
            List<DocumentoBean> resu = ResumenBajaDespachador.cargarResumen(nrodoc, conn);
            unidadEnvio = Util.getPathZipFilesEnvio(items.getEmpr_nroruc());
            log.info("ResumenReversion.enviarReversionASunat - ruta de directorios " + unidadEnvio);
            guardarStatus(nrodoc, "P", conn);
            log.info("ResumenReversion.enviarReversionASunat - Inicializamos cabecera ");
            ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "ds");
            //Parametros del keystore
            String keystoreType = Util.getPropertyValue("keystoreType", items.getEmpr_nroruc());//"JKS";
            String keystoreFile = Util.getPropertyValue("keystoreFile", items.getEmpr_nroruc());
            String keystorePass = Util.getPropertyValue("keystorePass", items.getEmpr_nroruc());
            String privateKeyAlias = Util.getPropertyValue("privateKeyAlias", items.getEmpr_nroruc());
            String privateKeyPass = Util.getPropertyValue("privateKeyPass", items.getEmpr_nroruc());
            String certificateAlias = Util.getPropertyValue("certificateAlias", items.getEmpr_nroruc());
            log.info("ResumenReversion.enviarReversionASunat - Lectura de certificado ");
            CDATASection cdata;
            if (items != null) {
                log.info("ResumenReversion.enviarReversionASunat - Inicializamos la generacion del xml ");
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
                log.info("ResumenReversion.enviarReversionASunat - Inicializamos cabecera XML");
                Element envelope = doc.createElementNS("", "VoidedDocuments");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "urn:sunat:names:specification:ubl:peru:schema:xsd:VoidedDocuments-1");
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
                CustomizationID.appendChild(doc.createTextNode("1.0"));

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

                log.info("ResumenReversion.enviarReversionASunat - detalle del XML ");
                for (DocumentoBean listaRes : resu) {
                    Element SummaryDocumentsLine = doc.createElementNS("", "sac:VoidedDocumentsLine");
                    envelope.appendChild(SummaryDocumentsLine);
                    SummaryDocumentsLine.appendChild(doc.createTextNode("\n"));

                    Element LineID = doc.createElementNS("", "cbc:LineID");
                    SummaryDocumentsLine.appendChild(LineID);
                    LineID.appendChild(doc.createTextNode(listaRes.getResu_fila()));

                    Element DocumentTypeCode = doc.createElementNS("", "cbc:DocumentTypeCode");
                    SummaryDocumentsLine.appendChild(DocumentTypeCode);
                    DocumentTypeCode.appendChild(doc.createTextNode(listaRes.getResu_tipodoc()));

                    Element DocumentSerialID = doc.createElementNS("", "sac:DocumentSerialID");
                    SummaryDocumentsLine.appendChild(DocumentSerialID);
                    DocumentSerialID.appendChild(doc.createTextNode(listaRes.getResu_serie()));

                    Element DocumentNumberID = doc.createElementNS("", "sac:DocumentNumberID");
                    SummaryDocumentsLine.appendChild(DocumentNumberID);
                    DocumentNumberID.appendChild(doc.createTextNode(listaRes.getResu_numero()));

                    Element VoidReasonDescription = doc.createElementNS("", "sac:VoidReasonDescription");
                    SummaryDocumentsLine.appendChild(VoidReasonDescription);
                    cdata = doc.createCDATASection(listaRes.getResu_motivo().trim());
                    VoidReasonDescription.appendChild(cdata);
                }
                sig.setId(items.getEmpr_nroruc());
                sig.addKeyInfo(cert);
                {
                    Transforms transforms = new Transforms(doc);
                    //Parte del elemento de la firma necesita ser canónica . Es un tipo de algoritmo para la normalización de XML. 
                    //Para obtener más información, por favor , eche un vistazo a la página web de la firma W3C XML Digital.
                    transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
                    //Añade los datos del Documento/Referencia
                    sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
                }
                {
                    //Firmar el documento
                    log.info("ResumenReversion.enviarReversionASunat - firmado XML ");
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

                log.info("ResumenReversion.enviarReversionASunat - XML creado " + pathXMLFile);
                //Mandar a zip
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
                log.info("ResumenReversion.enviarReversionASunat - ZIP creado " + unidadEnvio + items.getEmpr_nroruc() + "-" + items.getResu_identificador() + ".zip");

                /*=======================guardarHashYBarCodePDF417*/
                //LecturaXML.guardarHashYBarCodePDF417(nrodoc,"AN",pathXMLFile);
                /*=======================ENVIO A SUNAT=============*/
                guardarStatus(nrodoc, "E", conn);

                log.info("ResumenReversion.enviarReversionASunat - Preparando envio a SUNAT ");
                resultado = WSDispatcher.enviarResumenReversionesASunat(iddocument, unidadEnvio, items.getEmpr_nroruc() + "-" + items.getResu_identificador() + ".zip", conn, items.getEmpr_nroruc());
                //resultado="termino de generar el archivo xml de las bajas";
                guardarStatus(nrodoc, "O", conn);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultado = "0100|Error al generar el archivo de formato xml de Resumen de Reversiones.";
            guardarStatus(iddocument, "X", conn);

        }
        return resultado;
    }

    public static void guardarStatus(String iddocument, String status, Connection conn) {
        String resultado = "";
        try {

            //=== Guardar el Ticket
            String sql = "update resumendia_baja set resu_proce_status=? where codigo=?";
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
