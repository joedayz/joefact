package com.org.ws;

/**
 *
 * @author rmoscoso
 */
import com.org.factories.Util;
import com.org.model.beans.DocumentoBean;
import com.org.model.beans.Leyenda;
import com.org.model.despatchers.DElectronicoDespachador;
import com.org.util.GeneralFunctions;
import com.org.util.HeaderHandlerResolver;
import com.org.util.LecturaXML;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

public class NDElectronica {

    private static Log log = LogFactory.getLog(NDElectronica.class);

    static {
        org.apache.xml.security.Init.init();
    }

    public static String generarXMLZipiadoND(String iddocument, Connection conn) {
        log.info("generarXMLZipiadoND - Inicializamos el ambiente");
        org.apache.xml.security.Init.init();
        String resultado = "";
        String nrodoc = iddocument;//"943270";// request.getParameter("nrodoc");68
        String unidadEnvio; // = Util.getPathZipFilesEnvio();
        String pathXMLFile;
        try {
            //String nrodoc = iddocument;//"943270";// request.getParameter("nrodoc");

            log.info("generarXMLZipiadoND - Extraemos datos para preparar XML ");
            DocumentoBean items = DElectronicoDespachador.cargarDocElectronico(nrodoc, conn);
            List<DocumentoBean> detdocelec = DElectronicoDespachador.cargarDetDocElectronico(nrodoc, conn);
            List<DocumentoBean> anticipos = DElectronicoDespachador.cargarDetDocElectronicoAnticipo(nrodoc, conn);
            List<Leyenda> leyendas = DElectronicoDespachador.cargarDetDocElectronicoLeyenda(nrodoc, conn);
            unidadEnvio = Util.getPathZipFilesEnvio(items.getEmpr_nroruc());
            log.info("generarXMLZipiadoND - Ruita de directorios " + unidadEnvio);
            LecturaXML.guardarProcesoEstado(nrodoc, "P", " | ".split("\\|", 0), "", conn);
            log.info("generarXMLZipiadoND - Iniciamos cabecera ");
            if (items != null) {
                pathXMLFile = unidadEnvio + items.getEmpr_nroruc() + "-08-" + items.getDocu_numero() + ".xml";
                //======================crear XML =======================
                resultado = creaXml(items, detdocelec, anticipos, leyendas, unidadEnvio, conn);
                
                //======================guardar Hash Y Barcode PDF417 =======================
                log.info("generarXMLZipiadoND - Crear Hashcode y CodeBarPDF417");
//                LecturaXML.guardarHashYBarCodePDF417(items.getDocu_tipodocumento(), nrodoc, "DV", pathXMLFile, conn);
                LecturaXML.guardarHashYBarCodeQR(items.getDocu_tipodocumento(), nrodoc, "DV", pathXMLFile, conn);
                //======================guardar PDF =======================
                resultado = GeneralFunctions.creaPdf(items, unidadEnvio, conn,"NotaDebito.jasper");
                //====================== SUBIR PDF AL FTP INDICADO =======================
                resultado = GeneralFunctions.copiaAFtp(items, unidadEnvio, conn);
                /*=======================ENVIO A SUNAT=============*/
                if (items.getDocu_enviaws().equals("S")) {
                    log.info("generarXMLZipiadoND - Preparando para enviar a SUNAT");
                    LecturaXML.guardarProcesoEstado(nrodoc, "E", " | ".split("\\|", 0), "", conn);
                    resultado = enviarASunat(nrodoc, unidadEnvio, items.getEmpr_nroruc() + "-08-" + items.getDocu_numero() + ".zip", conn, items.getEmpr_nroruc());
                } else {
                    /*este caso de boleta no se envia al sunat*/
                    log.info("generarXMLZipiadoND - No se envia a SUNAT");
                    resultado = "0|El Comprobante numero " + items.getDocu_numero() + ", ha sido aceptado.";
                }
                /*=======================ENVIO CORREO AL CLIENTE=============*/
                if (items.getDocu_enviaws().equals("S")) {
                    resultado = GeneralFunctions.enviarEmail(items, unidadEnvio, conn, resultado, " COMPROBANTE DE BOLETA DE VENTA ELECTRONICA ");
                }
                log.info("generarXMLZipiadoND - Proceso creado correctamente  " + items.getEmpr_nroruc() + "-08-" + items.getDocu_numero());
                LecturaXML.guardarProcesoEstado(nrodoc, "O", resultado.split("\\|", 0), "", conn);
                //resultado="termino de generar el archivo xml de la Nota de Debito";

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            resultado = "0100|Error al generar el archivo de formato xml de la Nota de Debito";
            log.error("generarXMLZipiadoND - error  " + ex.toString());
            try {
                LecturaXML.guardarProcesoEstado(iddocument, "Q", resultado.split("\\|", 0), ex.toString(), conn);
            } catch (SQLException ex1) {
                log.error(ex1);
            }
        }
//        try {
//            LecturaXML.guardarProcesoEstado(nrodoc, "O", resultado.split("\\|", 0), conn);
//        } catch (SQLException ex) {
//            Logger.getLogger(NDElectronica.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return resultado;
    }

    public static String enviarASunat(String trans, String unidadEnvio, String zipFileName, Connection conn, String vruc) throws com.org.exceptiones.NoExisteWSSunatException {
        String resultado = "";
        String sws = Util.getWsOpcion(vruc);
        log.info("enviarASunat - Prepara ambiente: " + sws);
        try {

            javax.activation.FileDataSource fileDataSource = new javax.activation.FileDataSource(unidadEnvio + zipFileName);
            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(fileDataSource);
            byte[] respuestaSunat = null;
            //================Enviando a sunat
            switch (sws) {
                case "1":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_fe ws1 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_fe();
                    HeaderHandlerResolver handlerResolver1 = new HeaderHandlerResolver();
                    handlerResolver1.setVruc(vruc);
                    ws1.setHandlerResolver(handlerResolver1);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService port1 = ws1.getBillServicePort();
                    respuestaSunat = port1.sendBill(zipFileName, dataHandler);
                    log.info("enviarASunat - Ambiente Beta: " + sws);
                    break;
                case "2":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa ws2 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa();
                    HeaderHandlerResolver handlerResolver2 = new HeaderHandlerResolver();
                    handlerResolver2.setVruc(vruc);
                    ws2.setHandlerResolver(handlerResolver2);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService port2 = ws2.getBillServicePort();
                    respuestaSunat = port2.sendBill(zipFileName, dataHandler);
                    log.info("enviarASunat - Ambiente QA " + sws);
                    break;
                case "3":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service_fe ws3 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service_fe();
                    HeaderHandlerResolver handlerResolver3 = new HeaderHandlerResolver();
                    handlerResolver3.setVruc(vruc);
                    ws3.setHandlerResolver(handlerResolver3);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService port3 = ws3.getBillServicePort();
                    respuestaSunat = port3.sendBill(zipFileName, dataHandler);
                    log.info("enviarASunat - Ambiente Produccion " + sws);
                    break;
                default:

                    respuestaSunat = null;
                    throw new com.org.exceptiones.NoExisteWSSunatException(sws);
            }
            //================Grabando la respuesta de sunat en archivo ZIP
            String pathRecepcion = Util.getPathZipFilesRecepcion(vruc);
            FileOutputStream fos = new FileOutputStream(pathRecepcion + "R-" + zipFileName);
            fos.write(respuestaSunat);
            fos.close();
            //================Descompremiendo el zip de Sunat
            log.info("enviarASunat - Descomprimiendo CDR " + pathRecepcion + "R-" + zipFileName);
            ZipFile archive = new ZipFile(pathRecepcion + "R-" + zipFileName);
            Enumeration e = archive.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                File file = new File(pathRecepcion, entry.getName());
//                System.out.println("<<< Quien es: " + file.getName() + ">>>>>");
//                System.out.println("<<< Direcctorio: " + file.isDirectory() + ">>>>>");
//                System.out.println("<<< Archivo: " + file.isFile() + ">>>>>");
                if (!file.isDirectory()) {
                    if (entry.isDirectory() && !file.exists()) {
                        file.mkdirs();
                    } else {
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                        InputStream in = archive.getInputStream(entry);
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                        byte[] buffer = new byte[8192];
                        int read;
                        while (-1 != (read = in.read(buffer))) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        out.close();
                    }
                }
            }
            archive.close();
            //================leeyendo la resuesta de Sunat
            log.info("enviarASunat - Lectura del contenido del CDR ");
            zipFileName = zipFileName.substring(0, zipFileName.indexOf(".zip"));
            resultado = LecturaXML.getRespuestaSunat(trans, pathRecepcion + "R-" + zipFileName + ".xml", conn);
            System.out.println("==>El envio del Zip a sunat fue exitoso");
            log.info("enviarASunat - Envio a Sunat Exitoso ");
        } catch (javax.xml.ws.soap.SOAPFaultException ex) {
            log.error("enviarASunat - Error " + ex.toString());
            String errorCode = ex.getFault().getFaultCodeAsQName().getLocalPart();
            errorCode = errorCode.substring(errorCode.indexOf(".") + 1, errorCode.length());
            resultado = Util.getErrorMesageByCode(errorCode, vruc);
            try {
                LecturaXML.guardarProcesoEstado(trans, "R", resultado.split("\\|", 0), ex.toString(), conn);
            } catch (SQLException ex1) {
                log.error("enviarASunat - Error " + ex1.toString());
            }
            
        } catch (Exception e) {
            log.error("enviarASunat - Error " + e.toString());
            e.printStackTrace();
            resultado = "-1|Error en el envio de archivo zip a sunat";
            try {
                LecturaXML.guardarProcesoEstado(trans, "X", resultado.split("\\|", 0), e.toString(), conn);
            } catch (SQLException ex) {
                log.error("enviarASunat - Error " + ex.toString());
            }
        }
        return resultado;
    }
    private static String creaXml(DocumentoBean items, List<DocumentoBean> detdocelec, List<DocumentoBean> anticipos, List<Leyenda> leyendas, String unidadEnvio, Connection conn) {
        String resultado = "";
        try {
            ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "ds");
            //Parametros del keystore
            String keystoreType = Util.getPropertyValue("keystoreType", items.getEmpr_nroruc());//"JKS";
            String keystoreFile = Util.getPropertyValue("keystoreFile", items.getEmpr_nroruc());
            String keystorePass = Util.getPropertyValue("keystorePass", items.getEmpr_nroruc());
            String privateKeyAlias = Util.getPropertyValue("privateKeyAlias", items.getEmpr_nroruc());
            String privateKeyPass = Util.getPropertyValue("privateKeyPass", items.getEmpr_nroruc());
            String certificateAlias = Util.getPropertyValue("certificateAlias", items.getEmpr_nroruc());
            CDATASection cdata;
            log.info("generarXMLZipiadoND - Lectura de cerificado ");
                log.info("generarXMLZipiadoND - Iniciamos la generacion del XML");
                String pathXMLFile = unidadEnvio + items.getEmpr_nroruc() + "-08-" + items.getDocu_numero() + ".xml";
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
                log.info("generarXMLZipiadoND - cabecera XML ");
                Element envelope = doc.createElementNS("", "DebitNote");
                envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "urn:oasis:names:specification:ubl:schema:xsd:DebitNote-2");
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

                Element AdditionalInformation = doc.createElementNS("", "sac:AdditionalInformation");
                envelope.appendChild(AdditionalInformation);
                AdditionalInformation.appendChild(doc.createTextNode("\n"));
                //agrupa1
                if (!items.getDocu_gravada().trim().equals("0.00")) {
                    Element AdditionalMonetaryTotal1 = doc.createElementNS("", "sac:AdditionalMonetaryTotal");
                    envelope.appendChild(AdditionalMonetaryTotal1);
                    AdditionalMonetaryTotal1.appendChild(doc.createTextNode("\n"));

                    Element ID1 = doc.createElementNS("", "cbc:ID");
                    envelope.appendChild(ID1);
                    ID1.appendChild(doc.createTextNode("1001"));

                    Element PayableAmount1 = doc.createElementNS("", "cbc:PayableAmount");
                    PayableAmount1.setAttributeNS(null, "currencyID", items.getDocu_moneda().trim());
                    PayableAmount1.setIdAttributeNS(null, "currencyID", true);
                    envelope.appendChild(PayableAmount1);
                    PayableAmount1.appendChild(doc.createTextNode(items.getDocu_gravada().trim()));

                    AdditionalInformation.appendChild(AdditionalMonetaryTotal1);
                    AdditionalMonetaryTotal1.appendChild(ID1);
                    AdditionalMonetaryTotal1.appendChild(PayableAmount1);
                }
                //agrupa2
                if (!items.getDocu_inafecta().trim().equals("0.00")) {
                    Element AdditionalMonetaryTotal2 = doc.createElementNS("", "sac:AdditionalMonetaryTotal");
                    envelope.appendChild(AdditionalMonetaryTotal2);
                    AdditionalMonetaryTotal2.appendChild(doc.createTextNode("\n"));

                    Element ID2 = doc.createElementNS("", "cbc:ID");
                    envelope.appendChild(ID2);
                    ID2.appendChild(doc.createTextNode("1002"));

                    Element PayableAmount2 = doc.createElementNS("", "cbc:PayableAmount");
                    PayableAmount2.setAttributeNS(null, "currencyID", items.getDocu_moneda());//=====================================>
                    PayableAmount2.setIdAttributeNS(null, "currencyID", true);
                    envelope.appendChild(PayableAmount2);

                    PayableAmount2.appendChild(doc.createTextNode(items.getDocu_inafecta().trim()));

                    AdditionalInformation.appendChild(AdditionalMonetaryTotal2);
                    AdditionalMonetaryTotal2.appendChild(ID2);
                    AdditionalMonetaryTotal2.appendChild(PayableAmount2);
                }
                //agrupa3
                if (!items.getDocu_exonerada().trim().equals("0.00")) {
                    Element AdditionalMonetaryTotal3 = doc.createElementNS("", "sac:AdditionalMonetaryTotal");
                    envelope.appendChild(AdditionalMonetaryTotal3);
                    AdditionalMonetaryTotal3.appendChild(doc.createTextNode("\n"));

                    Element ID3 = doc.createElementNS("", "cbc:ID");
                    envelope.appendChild(ID3);
                    ID3.appendChild(doc.createTextNode("1003"));

                    Element PayableAmount3 = doc.createElementNS("", "cbc:PayableAmount");
                    PayableAmount3.setAttributeNS(null, "currencyID", items.getDocu_moneda());//==========================>
                    PayableAmount3.setIdAttributeNS(null, "currencyID", true);
                    envelope.appendChild(PayableAmount3);
                    PayableAmount3.appendChild(doc.createTextNode(items.getDocu_exonerada().trim()));

                    AdditionalInformation.appendChild(AdditionalMonetaryTotal3);
                    AdditionalMonetaryTotal3.appendChild(ID3);
                    AdditionalMonetaryTotal3.appendChild(PayableAmount3);
                }
                //agrupa4
                if (!items.getDocu_gratuita().trim().equals("0.00")) {
                    Element AdditionalMonetaryTotal4 = doc.createElementNS("", "sac:AdditionalMonetaryTotal");
                    envelope.appendChild(AdditionalMonetaryTotal4);
                    AdditionalMonetaryTotal4.appendChild(doc.createTextNode("\n"));

                    Element ID4 = doc.createElementNS("", "cbc:ID");
                    envelope.appendChild(ID4);
                    ID4.appendChild(doc.createTextNode("1004"));

                    Element PayableAmount4 = doc.createElementNS("", "cbc:PayableAmount");
                    PayableAmount4.setAttributeNS(null, "currencyID", items.getDocu_moneda().trim());
                    PayableAmount4.setIdAttributeNS(null, "currencyID", true);
                    envelope.appendChild(PayableAmount4);
                    PayableAmount4.appendChild(doc.createTextNode(items.getDocu_gratuita().trim()));

                    AdditionalInformation.appendChild(AdditionalMonetaryTotal4);
                    AdditionalMonetaryTotal4.appendChild(ID4);
                    AdditionalMonetaryTotal4.appendChild(PayableAmount4);
                }
                //agrupa5
                if (!items.getDocu_descuento().trim().equals("0.00")) {
                    Element AdditionalMonetaryTotal5 = doc.createElementNS("", "sac:AdditionalMonetaryTotal");
                    envelope.appendChild(AdditionalMonetaryTotal5);
                    AdditionalMonetaryTotal5.appendChild(doc.createTextNode("\n"));

                    Element ID10 = doc.createElementNS("", "cbc:ID");
                    envelope.appendChild(ID10);
                    ID10.appendChild(doc.createTextNode("2005"));

                    Element PayableAmount5 = doc.createElementNS("", "cbc:PayableAmount");
                    PayableAmount5.setAttributeNS(null, "currencyID", items.getDocu_moneda().trim());
                    PayableAmount5.setIdAttributeNS(null, "currencyID", true);
                    envelope.appendChild(PayableAmount5);
                    PayableAmount5.appendChild(doc.createTextNode(items.getDocu_descuento().trim()));

                    AdditionalInformation.appendChild(AdditionalMonetaryTotal5);
                    AdditionalMonetaryTotal5.appendChild(ID10);
                    AdditionalMonetaryTotal5.appendChild(PayableAmount5);
                }
                //El baseURI es la URI que se utiliza para anteponer a URIs relativos
                String BaseURI = signatureFile.toURI().toURL().toString();
                //Crea un XML Signature objeto desde el documento, BaseURI and signature algorithm (in this case RSA)
                //XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA); Cadena URI que se ajusta a la sintaxis URI y representa el archivo XML de entrada
                XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA);

                ExtensionContent.appendChild(sig.getElement());
                UBLExtension.appendChild(ExtensionContent);
                UBLExtensions.appendChild(UBLExtension);
                UBLExtensions.appendChild(UBLExtension2);
                UBLExtension2.appendChild(ExtensionContent2);
                ExtensionContent2.appendChild(AdditionalInformation);

//bloque1
                Element UBLVersionID = doc.createElementNS("", "cbc:UBLVersionID");
                envelope.appendChild(UBLVersionID);
                UBLVersionID.appendChild(doc.createTextNode("2.0"));

                Element CustomizationID = doc.createElementNS("", "cbc:CustomizationID");
                envelope.appendChild(CustomizationID);
                CustomizationID.appendChild(doc.createTextNode("1.0"));

                Element ID5 = doc.createElementNS("", "cbc:ID");
                envelope.appendChild(ID5);
                ID5.appendChild(doc.createTextNode(items.getDocu_numero().trim()));

                Element IssueDate = doc.createElementNS("", "cbc:IssueDate");
                envelope.appendChild(IssueDate);
                IssueDate.appendChild(doc.createTextNode(items.getDocu_fecha().trim()));

                Element DocumentCurrencyCode = doc.createElementNS("", "cbc:DocumentCurrencyCode");
                envelope.appendChild(DocumentCurrencyCode);
                DocumentCurrencyCode.appendChild(doc.createTextNode(items.getDocu_moneda().trim()));

                ///*****nuevo bloque
                Element DiscrepancyResponse = doc.createElementNS("", "cac:DiscrepancyResponse");
                envelope.appendChild(DiscrepancyResponse);
                DiscrepancyResponse.appendChild(doc.createTextNode("\n"));

                Element ReferenceID = doc.createElementNS("", "cbc:ReferenceID");
                DiscrepancyResponse.appendChild(ReferenceID);
                ReferenceID.appendChild(doc.createTextNode(items.getNota_documento().trim()));

                Element ResponseCode = doc.createElementNS("", "cbc:ResponseCode");
                DiscrepancyResponse.appendChild(ResponseCode);
                ResponseCode.appendChild(doc.createTextNode(items.getNota_motivo().trim()));//debe ser 01

                Element Description11 = doc.createElementNS("", "cbc:Description");
                DiscrepancyResponse.appendChild(Description11);
                cdata = doc.createCDATASection(items.getNota_sustento().trim());
                Description11.appendChild(cdata);

                Element BillingReference = doc.createElementNS("", "cac:BillingReference");
                envelope.appendChild(BillingReference);
                BillingReference.appendChild(doc.createTextNode("\n"));

                Element InvoiceDocumentReference = doc.createElementNS("", "cac:InvoiceDocumentReference");
                BillingReference.appendChild(InvoiceDocumentReference);
                InvoiceDocumentReference.appendChild(doc.createTextNode("\n"));

                Element ID20 = doc.createElementNS("", "cbc:ID");
                InvoiceDocumentReference.appendChild(ID20);
                ID20.appendChild(doc.createTextNode(items.getNota_documento().trim()));

                Element DocumentTypeCode = doc.createElementNS("", "cbc:DocumentTypeCode");
                InvoiceDocumentReference.appendChild(DocumentTypeCode);
                DocumentTypeCode.appendChild(doc.createTextNode(items.getNota_tipodoc().trim()));

//bloque2 cac:Signature--------------------------------------------------------
                Element Signature = doc.createElementNS("", "cac:Signature");
                envelope.appendChild(Signature);
                Signature.appendChild(doc.createTextNode("\n"));

                Element ID6 = doc.createElementNS("", "cbc:ID");
                Signature.appendChild(ID6);
                ID6.appendChild(doc.createTextNode(items.getEmpr_nroruc().trim()));

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
                URI.appendChild(doc.createTextNode(items.getEmpr_nroruc().trim()));
//bloque3 cac:AccountingSupplierParty-----------------------------------------

                Element AccountingSupplierParty = doc.createElementNS("", "cac:AccountingSupplierParty");
                envelope.appendChild(AccountingSupplierParty);
                AccountingSupplierParty.appendChild(doc.createTextNode("\n"));

                Element CustomerAssignedAccountID = doc.createElementNS("", "cbc:CustomerAssignedAccountID");
                AccountingSupplierParty.appendChild(CustomerAssignedAccountID);
                CustomerAssignedAccountID.appendChild(doc.createTextNode(items.getEmpr_nroruc().trim()));

                Element AdditionalAccountID = doc.createElementNS("", "cbc:AdditionalAccountID");
                AccountingSupplierParty.appendChild(AdditionalAccountID);
                AdditionalAccountID.appendChild(doc.createTextNode(items.getEmpr_tipodoc().trim()));
//***********************************************************
                Element Party = doc.createElementNS("", "cac:Party");
                AccountingSupplierParty.appendChild(Party);
                Party.appendChild(doc.createTextNode("\n"));

                Element PartyName1 = doc.createElementNS("", "cac:PartyName");
                Party.appendChild(PartyName1);//se anade al grupo party
                PartyName1.appendChild(doc.createTextNode("\n"));

                Element Name2 = doc.createElementNS("", "cbc:Name");
                PartyName1.appendChild(Name2);//se anade al grupo partyname1
                cdata = doc.createCDATASection(items.getEmpr_razonsocial().trim());
                Name2.appendChild(cdata);

                Element PostalAddress = doc.createElementNS("", "cac:PostalAddress");
                Party.appendChild(PostalAddress);//se anade al grupo party
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
                Party.appendChild(PartyLegalEntity);//se anade al grupo party
                PartyLegalEntity.appendChild(doc.createTextNode("\n"));

                Element RegistrationName = doc.createElementNS("", "cbc:RegistrationName");
                PartyLegalEntity.appendChild(RegistrationName);//se anade al grupo Country
                cdata = doc.createCDATASection(items.getEmpr_razonsocial().trim());
                RegistrationName.appendChild(cdata);
// bloque4
                Element AccountingCustomerParty = doc.createElementNS("", "cac:AccountingCustomerParty");
                envelope.appendChild(AccountingCustomerParty);
                AccountingCustomerParty.appendChild(doc.createTextNode("\n"));

                Element CustomerAssignedAccountID1 = doc.createElementNS("", "cbc:CustomerAssignedAccountID");
                AccountingCustomerParty.appendChild(CustomerAssignedAccountID1);//se anade al grupo AccountingCustomerParty
                CustomerAssignedAccountID1.appendChild(doc.createTextNode(items.getClie_numero().trim()));

                Element AdditionalAccountID1 = doc.createElementNS("", "cbc:AdditionalAccountID");
                AccountingCustomerParty.appendChild(AdditionalAccountID1);//se anade al grupo AccountingCustomerParty
                AdditionalAccountID1.appendChild(doc.createTextNode(items.getClie_tipodoc().trim()));

                Element Party1 = doc.createElementNS("", "cac:Party");
                AccountingCustomerParty.appendChild(Party1);//se anade al grupo AccountingCustomerParty
                Party1.appendChild(doc.createTextNode("\n"));

                Element PartyLegalEntity1 = doc.createElementNS("", "cac:PartyLegalEntity");
                Party1.appendChild(PartyLegalEntity1);//se anade al grupo Party1
                PartyLegalEntity1.appendChild(doc.createTextNode("\n"));

                Element RegistrationName1 = doc.createElementNS("", "cbc:RegistrationName");
                PartyLegalEntity1.appendChild(RegistrationName1);//se anade al grupo PartyLegalEntity1
                cdata = doc.createCDATASection(items.getClie_nombre().trim());
                RegistrationName1.appendChild(cdata);

//bloque 5
                if (items.getDocu_igv() != null && !items.getDocu_igv().equals("0")) {
                    Element TaxTotal = doc.createElementNS("", "cac:TaxTotal");
                    envelope.appendChild(TaxTotal);
                    TaxTotal.appendChild(doc.createTextNode("\n"));

                    Element TaxAmount = doc.createElementNS("", "cbc:TaxAmount");
                    TaxAmount.setAttributeNS(null, "currencyID", items.getDocu_moneda().trim());
                    TaxAmount.setIdAttributeNS(null, "currencyID", true);
                    TaxTotal.appendChild(TaxAmount);//se anade al grupo TaxTotal
                    TaxAmount.appendChild(doc.createTextNode(items.getDocu_igv().trim()));

                    Element TaxSubtotal = doc.createElementNS("", "cac:TaxSubtotal");
                    TaxTotal.appendChild(TaxSubtotal);//se anade al grupo TaxTotal
                    TaxSubtotal.appendChild(doc.createTextNode("\n"));

                    Element TaxAmount1 = doc.createElementNS("", "cbc:TaxAmount");
                    TaxAmount1.setAttributeNS(null, "currencyID", items.getDocu_moneda().trim());
                    TaxAmount1.setIdAttributeNS(null, "currencyID", true);
                    TaxSubtotal.appendChild(TaxAmount1);//se anade al grupo TaxSubtotal
                    TaxAmount1.appendChild(doc.createTextNode(items.getDocu_igv().trim()));

                    Element TaxCategory = doc.createElementNS("", "cac:TaxCategory");
                    TaxSubtotal.appendChild(TaxCategory);//se anade al grupo TaxSubtotal
                    TaxCategory.appendChild(doc.createTextNode("\n"));

                    Element TaxScheme = doc.createElementNS("", "cac:TaxScheme");
                    TaxCategory.appendChild(TaxScheme);//se anade al grupo TaxCategory
                    TaxScheme.appendChild(doc.createTextNode("\n"));

                    Element ID9 = doc.createElementNS("", "cbc:ID");
                    TaxScheme.appendChild(ID9);//se anade al grupo TaxScheme
                    ID9.appendChild(doc.createTextNode("1000")); ///================================faltaba poner 1000

                    Element Name3 = doc.createElementNS("", "cbc:Name");
                    TaxScheme.appendChild(Name3);//se anade al grupo TaxScheme
                    Name3.appendChild(doc.createTextNode("IGV"));

                    Element TaxTypeCode = doc.createElementNS("", "cbc:TaxTypeCode");
                    TaxScheme.appendChild(TaxTypeCode);//se anade al grupo TaxScheme
                    TaxTypeCode.appendChild(doc.createTextNode("VAT"));
                }
//bloque 6     
                Element RequestedMonetaryTotal = doc.createElementNS("", "cac:RequestedMonetaryTotal");
                envelope.appendChild(RequestedMonetaryTotal);
                RequestedMonetaryTotal.appendChild(doc.createTextNode("\n"));

                /*Element LineExtensionAmount = doc.createElementNS("", "cbc:LineExtensionAmount");
                 LineExtensionAmount.setAttributeNS(null, "currencyID", items.getDocu_moneda());
                 LineExtensionAmount.setIdAttributeNS(null, "currencyID", true);
                 LegalMonetaryTotal.appendChild(LineExtensionAmount);//se anade al grupo LegalMonetaryTotal
                 LineExtensionAmount.appendChild(doc.createTextNode(items.getDocu_subtotal()));*/

 /*Element TaxExclusiveAmount = doc.createElementNS("", "cbc:TaxExclusiveAmount");
                 TaxExclusiveAmount.setAttributeNS(null, "currencyID", items.getDocu_moneda());
                 TaxExclusiveAmount.setIdAttributeNS(null, "currencyID", true);
                 LegalMonetaryTotal.appendChild(TaxExclusiveAmount);//se anade al grupo LegalMonetaryTotal
                 TaxExclusiveAmount.appendChild(doc.createTextNode(items.getDocu_igv()));*/
                Element PayableAmount = doc.createElementNS("", "cbc:PayableAmount");
                PayableAmount.setAttributeNS(null, "currencyID", items.getDocu_moneda().trim());
                PayableAmount.setIdAttributeNS(null, "currencyID", true);
                RequestedMonetaryTotal.appendChild(PayableAmount);//se anade al grupo LegalMonetaryTotal
                PayableAmount.appendChild(doc.createTextNode(items.getDocu_total().trim()));
//detalle factura
                log.info("generarXMLZipiadoND - Iniciamos detalle XML ");
                for (DocumentoBean listaDet : detdocelec) {
                    Element DebitNoteLine = doc.createElementNS("", "cac:DebitNoteLine");
                    envelope.appendChild(DebitNoteLine);
                    DebitNoteLine.appendChild(doc.createTextNode("\n"));

                    Element ID11 = doc.createElementNS("", "cbc:ID");
                    DebitNoteLine.appendChild(ID11);//se anade al grupo DebitNoteLine
                    ID11.appendChild(doc.createTextNode(listaDet.getItem_orden().trim()));

                    Element DebitedQuantity = doc.createElementNS("", "cbc:DebitedQuantity");
                    DebitedQuantity.setAttributeNS(null, "unitCode", listaDet.getItem_unidad().trim());
                    DebitedQuantity.setIdAttributeNS(null, "unitCode", true);

                    DebitNoteLine.appendChild(DebitedQuantity);//se anade al grupo DebitNoteLine
                    DebitedQuantity.appendChild(doc.createTextNode(listaDet.getItem_cantidad().trim()));

                    Element LineExtensionAmount1 = doc.createElementNS("", "cbc:LineExtensionAmount");
                    LineExtensionAmount1.setAttributeNS(null, "currencyID", listaDet.getItem_moneda().trim());
                    LineExtensionAmount1.setIdAttributeNS(null, "currencyID", true);

                    DebitNoteLine.appendChild(LineExtensionAmount1);//se anade al grupo DebitNoteLine
                    LineExtensionAmount1.appendChild(doc.createTextNode(listaDet.getItem_ti_subtotal().trim()));

                    Element PricingReference = doc.createElementNS("", "cac:PricingReference");
                    DebitNoteLine.appendChild(PricingReference);//se anade al grupo DebitNoteLine
                    PricingReference.appendChild(doc.createTextNode("\n"));

                    Element AlternativeConditionPrice = doc.createElementNS("", "cac:AlternativeConditionPrice");
                    PricingReference.appendChild(AlternativeConditionPrice);//se anade al grupo PricingReference
                    AlternativeConditionPrice.appendChild(doc.createTextNode("\n"));

                    Element PriceAmount = doc.createElementNS("", "cbc:PriceAmount");
                    PriceAmount.setAttributeNS(null, "currencyID", listaDet.getItem_moneda().trim());
                    PriceAmount.setIdAttributeNS(null, "currencyID", true);
                    AlternativeConditionPrice.appendChild(PriceAmount);//se anade al grupo AlternativeConditionPrice
                    PriceAmount.appendChild(doc.createTextNode(listaDet.getItem_pventa().trim()));

                    Element PriceTypeCode = doc.createElementNS("", "cbc:PriceTypeCode");
                    AlternativeConditionPrice.appendChild(PriceTypeCode);//se anade al grupo AlternativeConditionPrice
                    PriceTypeCode.appendChild(doc.createTextNode("01")); //=================================>Faltaba especificar ite

                    Element TaxTotal1 = doc.createElementNS("", "cac:TaxTotal");
                    DebitNoteLine.appendChild(TaxTotal1);//se anade al grupo DebitNoteLine
                    TaxTotal1.appendChild(doc.createTextNode("\n"));

                    Element TaxAmount2 = doc.createElementNS("", "cbc:TaxAmount");
                    TaxAmount2.setAttributeNS(null, "currencyID", listaDet.getItem_moneda().trim());
                    TaxAmount2.setIdAttributeNS(null, "currencyID", true);
                    TaxTotal1.appendChild(TaxAmount2);//se anade al grupo TaxTotal1
                    TaxAmount2.appendChild(doc.createTextNode(listaDet.getItem_ti_igv().trim()));

                    Element TaxSubtotal1 = doc.createElementNS("", "cac:TaxSubtotal");
                    TaxTotal1.appendChild(TaxSubtotal1);//se anade al grupo TaxTotal1
                    TaxSubtotal1.appendChild(doc.createTextNode("\n"));

                    /*Element TaxableAmount = doc.createElementNS("", "cbc:TaxableAmount");
                     TaxableAmount.setAttributeNS(null, "currencyID", listaDet.getItem_moneda());
                     TaxableAmount.setIdAttributeNS(null, "currencyID", true);

                     TaxSubtotal1.appendChild(TaxableAmount);//se anade al grupo TaxSubtotal1
                     TaxableAmount.appendChild(doc.createTextNode(listaDet.getItem_ti_igv()));*/
                    Element TaxAmount3 = doc.createElementNS("", "cbc:TaxAmount");
                    TaxAmount3.setAttributeNS(null, "currencyID", listaDet.getItem_moneda().trim()); //================>errror estaba con item..getItem_moneda()
                    TaxAmount3.setIdAttributeNS(null, "currencyID", true);
                    TaxSubtotal1.appendChild(TaxAmount3);//se anade al grupo TaxSubtotal1
                    TaxAmount3.appendChild(doc.createTextNode(listaDet.getItem_ti_igv().trim()));

                    /*Element Percent = doc.createElementNS("", "cbc:Percent");
                     TaxSubtotal1.appendChild(Percent);//se anade al grupo TaxSubtotal1
                     Percent.appendChild(doc.createTextNode("0.0"));*/
                    Element TaxCategory1 = doc.createElementNS("", "cac:TaxCategory");
                    TaxSubtotal1.appendChild(TaxCategory1);//se anade al grupo TaxSubtotal1
                    TaxCategory1.appendChild(doc.createTextNode("\n"));

                    /*Element ID12 = doc.createElementNS("", "cbc:ID");
                     TaxCategory1.appendChild(ID12);//se anade al grupo TaxCategory1
                     ID12.appendChild(doc.createTextNode("VAT"));*/
                    Element TaxExemptionReasonCode = doc.createElementNS("", "cbc:TaxExemptionReasonCode");
                    TaxCategory1.appendChild(TaxExemptionReasonCode);//se anade al grupo TaxCategory1
                    TaxExemptionReasonCode.appendChild(doc.createTextNode(listaDet.getItem_afectacion().trim()));

                    /*Element TierRange = doc.createElementNS("", "cbc:TierRange");
                     TaxCategory1.appendChild(TierRange);//se anade al grupo TaxCategory1
                     TierRange.appendChild(doc.createTextNode("00"));*/
                    Element TaxScheme1 = doc.createElementNS("", "cac:TaxScheme");
                    TaxCategory1.appendChild(TaxScheme1);//se anade al grupo TaxCategory1
                    TaxScheme1.appendChild(doc.createTextNode("\n"));

                    Element ID15 = doc.createElementNS("", "cbc:ID");
                    TaxScheme1.appendChild(ID15);//se anade al grupo TaxCategory1
                    ID15.appendChild(doc.createTextNode("1000"));

                    Element Name9 = doc.createElementNS("", "cbc:Name");
                    TaxScheme1.appendChild(Name9);//se anade al grupo TaxCategory1
                    Name9.appendChild(doc.createTextNode("IGV"));

                    Element TaxTypeCode1 = doc.createElementNS("", "cbc:TaxTypeCode");
                    TaxScheme1.appendChild(TaxTypeCode1);//se anade al grupo TaxCategory1
                    TaxTypeCode1.appendChild(doc.createTextNode("VAT"));

                    Element Item = doc.createElementNS("", "cac:Item");
                    DebitNoteLine.appendChild(Item);//se anade al grupo DebitNoteLine
                    Item.appendChild(doc.createTextNode("\n"));

                    Element Description = doc.createElementNS("", "cbc:Description");
                    Item.appendChild(Description);//se anade al grupo Item
                    cdata = doc.createCDATASection(listaDet.getItem_descripcion().trim());
                    Description.appendChild(cdata);

                    Element SellersItemIdentification = doc.createElementNS("", "cac:SellersItemIdentification");
                    Item.appendChild(SellersItemIdentification);//se anade al grupo Item
                    SellersItemIdentification.appendChild(doc.createTextNode("\n"));

                    Element ID18 = doc.createElementNS("", "cbc:ID");
                    SellersItemIdentification.appendChild(ID18);//se anade al grupo Item
                    ID18.appendChild(doc.createTextNode(listaDet.getItem_codproducto().trim()));

                    Element Price = doc.createElementNS("", "cac:Price");
                    DebitNoteLine.appendChild(Price);//se anade al grupo DebitNoteLine
                    Price.appendChild(doc.createTextNode("\n"));

                    Element PriceAmount2 = doc.createElementNS("", "cbc:PriceAmount");
                    PriceAmount2.setAttributeNS(null, "currencyID", listaDet.getItem_moneda().trim());
                    PriceAmount2.setIdAttributeNS(null, "currencyID", true);
                    Price.appendChild(PriceAmount2);//se anade al grupo Price
                    PriceAmount2.appendChild(doc.createTextNode(listaDet.getItem_pventa().trim()));
                }

                log.info("generarXMLZipiadoND - Prepara firma digital ");
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
                    log.info("generarXMLZipiadoND - firma el XML ");
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

                log.info("generarXMLZipiadoND - XML creado " + pathXMLFile);
                //====================== CREAR ZIP PARA EL ENVIO A SUNAT =======================
                resultado = GeneralFunctions.crearZip(items, unidadEnvio, conn, signatureFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultado = "0100|Error al generar el archivo de formato xml de la Factura.";
            log.error("generarXMLZipiadoFactura - error  " + ex.toString());
            try {
                //LecturaXML.guardarProcesoEstado(iddocument, "Q", resultado.split("\\|", 0), ex.getStackTrace()[0].toString(), conn);
                LecturaXML.guardarProcesoEstado(items.getDocu_codigo(), "Q", resultado.split("\\|", 0), ex.getStackTrace()[0].toString(), conn);

            } catch (SQLException ex1) {
                log.error(ex1);
            }

        }
        return resultado;
    }

}
