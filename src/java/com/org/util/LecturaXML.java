/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

/**
 *
 * @author oswaldo
 */
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.pdf.BarcodePDF417;
import com.org.factories.ConnectionPool;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LecturaXML {

    private static Log log = LogFactory.getLog(LecturaXML.class);

    public static String getRespuestaSunat(String trans, String path, Connection conn) {
        String respuesta = null;
        String nota = "";
//        Connection conn = null;

        try {
            log.info("LecturaXML.getRespuestaSunat - iniciamos Lectura del contenido del CDR " + path);
            DocumentBuilderFactory fabricaCreadorDocumento = DocumentBuilderFactory.newInstance();
            DocumentBuilder creadorDocumento = fabricaCreadorDocumento.newDocumentBuilder();
            Document documento = creadorDocumento.parse(path);
            //Obtener el elemento raíz del documento
            Element raiz = documento.getDocumentElement();

            //Obtener la lista de nodos que tienen etiqueta "ds:Reference"
            NodeList responsecode = raiz.getElementsByTagName("cbc:ResponseCode");
            for (int i = 0; i < responsecode.getLength(); i++) {
                Node empleado = responsecode.item(i);
                Node datoContenido = empleado.getFirstChild();
                respuesta = datoContenido.getNodeValue();
            }
            NodeList nodesc = raiz.getElementsByTagName("cbc:Description");
            for (int i = 0; i < nodesc.getLength(); i++) {
                Node empleado = nodesc.item(i);
                Node datoContenido = empleado.getFirstChild();
                respuesta = respuesta + "|" + datoContenido.getNodeValue();
            }
            NodeList note = raiz.getElementsByTagName("cbc:Note");
            for (int i = 0; i < note.getLength(); i++) {
                Node empleado = note.item(i);
                Node datoContenido = empleado.getFirstChild();
                nota = nota + datoContenido.getNodeValue() + "\\n";
            }

            String[] cdr = respuesta.split("\\|", 0);
            //=== Guardar el link
            LecturaXML.guardarProcesoEstado(trans, "ECDR", cdr, nota, conn);
            String sql = "update cabecera set docu_link_cdr=?, cdr_observacion =?, cdr=?, cdr_nota=? where docu_codigo=?";
//            conn = ConnectionPool.obtenerConexionMysql();

            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar link
            ps = conn.prepareStatement(sql);
            ps.setString(1, path);
            ps.setString(2, nota);
            ps.setString(3, cdr[0]);
            ps.setString(4, cdr[1]);
            ps.setString(5, trans);

            log.info("LecturaXML.getRespuestaSunat - resultado del CDR Observacion: " + nota);
            log.info("LecturaXML.getRespuestaSunat - resultado del CDR codigo: " + cdr[0]);
            log.info("LecturaXML.getRespuestaSunat - resultado del CDR Nota: " + cdr[1]);
            int reg = ps.executeUpdate();
//            if (reg > 0) {
//                System.out.println("==>Link CDR grabado");
//                LecturaXML.guardarProcesoEstado(trans, "ECDR",cdr,nota, conn);
//            } else {
//                System.out.println("==>Error en Leer CDR");
//            }

//            sql = "insert into seguimiento(estado_seguimiento, docu_codigo, cdr_code, cdr_nota, cdr_observacion) values (?,?,?,?,?)";
//
//            ps = conn.prepareStatement(sql);
//            //===Inserta seguimiento de documento
//
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, "ECDR");
//            ps.setString(2, trans);
//            ps.setString(3, cdr[0]);
//            ps.setString(4, cdr[1]);
//            ps.setString(5, nota);
//            reg = ps.executeUpdate();
        } catch (org.xml.sax.SAXException ex) {
            System.out.println("ERROR: El formato XML del fichero no es correcto\n" + ex.getMessage());
            ex.printStackTrace();
            log.error("LecturaXML.getRespuestaSunat - Error : " + ex.toString());
            Logger.getLogger(LecturaXML.class.getName()).log(Level.SEVERE, null, ex);
            respuesta = "Error al leer el archivo de respuesta";
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo de respuesta\n" + ex.getMessage());
            ex.printStackTrace();
            log.error("LecturaXML.getRespuestaSunat - Error : " + ex.toString());
            respuesta = "Error al leer el archivo de respuesta";
            Logger.getLogger(LecturaXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            System.out.println("ERROR: No se ha podido crear el generador de documentos XML\n" + ex.getMessage());
            ex.printStackTrace();
            log.error("LecturaXML.getRespuestaSunat - Error : " + ex.toString());
            respuesta = "Error al leer el archivo de respuesta";
            Logger.getLogger(LecturaXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            log.error("LecturaXML.getRespuestaSunat - Error : " + ex.toString());
            Logger.getLogger(LecturaXML.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return respuesta;
    }

    public static void guardarHashYBarCodePDF417(String tipoDoc, String trans, String accion, String pathXMLFile, Connection conn) throws SQLException {
//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();
            String hashCode = LecturaXML.obtenerDigestValue(pathXMLFile);
            /*============ALMACENANDO  BarcodePDF417(codigo de Barra)=========*/
            BarcodePDF417 barcode = new BarcodePDF417();
            String infobarcode = tipoDoc.equals("20") ? LecturaXML.getInfoToBarCodeOD(pathXMLFile) : LecturaXML.getInfoToBarCode(pathXMLFile);
            infobarcode = infobarcode.replaceAll("\n", "");
            //System.out.println("==>infoBarcode:"+infobarcode);
            barcode.setText(infobarcode);
            java.awt.Image img = barcode.createAwtImage(Color.BLACK, Color.WHITE);
            BufferedImage outImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
            outImage.getGraphics().drawImage(img, 0, 0, null);
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ImageIO.write(outImage, "png", bytesOut);
            bytesOut.flush();
            byte[] pngImageData = bytesOut.toByteArray();
            InputStream is = new ByteArrayInputStream(pngImageData);
            //actualizamos el grafico del barcode";
            String sql = "update cabecera set hashcode=?,barcode=?, docu_link_xml=? where docu_codigo=?";
            //===eliminar si existente
            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar borcode
            ps = conn.prepareStatement(sql);
            ps.setString(1, hashCode);
            ps.setBinaryStream(2, is, pngImageData.length);
            ps.setString(3, pathXMLFile);
            ps.setString(4, trans);

            int reg = ps.executeUpdate();
            if (reg > 0) {
                System.out.println("==>BarCode grabado");
            } else {
                System.out.println("==>Error en generación de BarCode");
            }
            /*================================================================*/
        } catch (Exception ex) {
            System.out.println("==>Error en generación de hash o barCode:" + ex.getMessage());
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
    }

    public static void guardarHashYBarCodeQR(String tipoDoc, String trans, String accion, String pathXMLFile, Connection conn) throws SQLException {
//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();
            String hashCode = LecturaXML.obtenerDigestValue(pathXMLFile);
            /*============ALMACENANDO  BarcodePDF417(codigo de Barra)=========*/
            //QRCodeWriter barcode = new QRCodeWriter();
            String infobarcode = tipoDoc.equals("20") ? LecturaXML.getInfoToBarCodeOD(pathXMLFile) : LecturaXML.getInfoToBarCode(pathXMLFile);
            infobarcode = infobarcode.replaceAll("\n", "");
            //System.out.println("==>infoBarcode:"+infobarcode);
            //-----------------------
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(infobarcode, BarcodeFormat.QR_CODE, 350, 350);

            Path path = FileSystems.getDefault().getPath(pathXMLFile.replace("xml", "png"));
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            //-----------------------
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngImageData = pngOutputStream.toByteArray();

            InputStream is = new ByteArrayInputStream(pngImageData);
            //actualizamos el grafico del barcode";
            String sql = "update cabecera set hashcode=?,barcode=?, docu_link_xml=? where docu_codigo=?";
            //===eliminar si existente
            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar borcode
            ps = conn.prepareStatement(sql);
            ps.setString(1, hashCode);
            ps.setBinaryStream(2, is, pngImageData.length);
            ps.setString(3, pathXMLFile);
            ps.setString(4, trans);

            int reg = ps.executeUpdate();
            if (reg > 0) {
                System.out.println("==>BarCode grabado");
            } else {
                System.out.println("==>Error en generación de BarCode");
            }
            /*================================================================*/
        } catch (Exception ex) {
            System.out.println("==>Error en generación de hash o barCode:" + ex.getMessage());
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
    }

    public static String obtenerDigestValue(String path) {
        String firma = null;
        try {
            DocumentBuilderFactory fabricaCreadorDocumento = DocumentBuilderFactory.newInstance();
            DocumentBuilder creadorDocumento = fabricaCreadorDocumento.newDocumentBuilder();
            Document documento = creadorDocumento.parse(path);
            //Obtener el elemento raíz del documento
            Element raiz = documento.getDocumentElement();
            //Obtener la lista de nodos que tienen etiqueta "ds:Reference"
            NodeList listaEmpleados = raiz.getElementsByTagName("ds:Reference");
            //Recorrer la lista de empleados
            for (int i = 0; i < listaEmpleados.getLength(); i++) {
                //Obtener de la lista un empleado tras otro
                Node empleado = listaEmpleados.item(i);
                //Obtener la lista de los datos que contiene ese ds:Reference
                NodeList datosEmpleado = empleado.getChildNodes();
                //Recorrer la lista de los datos que contiene el ds:Reference
                for (int j = 0; j < datosEmpleado.getLength(); j++) {
                    //Obtener de la lista de datos un dato tras otro
                    Node dato = datosEmpleado.item(j);
                    //Comprobar que el dato se trata de un nodo de tipo Element
                    if (dato.getNodeType() == Node.ELEMENT_NODE) {
                        //Mostrar el nombre del tipo de dato
                        if (dato.getNodeName() == "ds:DigestValue") {
                            //El valor está contenido en un hijo del nodo Element
                            Node datoContenido = dato.getFirstChild();
                            //Mostrar el valor contenido en el nodo que debe ser de tipo Text
                            if (datoContenido != null && datoContenido.getNodeType() == Node.TEXT_NODE) {
                                //System.out.println(datoContenido.getNodeValue());
                                if (datoContenido.getNodeValue() != null) {
                                    firma = datoContenido.getNodeValue();
                                }
                            }
                        }
                    }
                }
            }

        } catch (org.xml.sax.SAXException ex) {
            System.out.println("ERROR: El formato XML del fichero no es correcto\n" + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("ERROR: Se ha producido un error al leer el fichero\n" + ex.getMessage());
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            System.out.println("ERROR: No se ha podido crear el generador de documentos XML\n" + ex.getMessage());
            ex.printStackTrace();
        }
        return firma;
    }

    public static String getInfoToBarCode(String path) {
        String respuesta = "";
        try {
            String IssueDate = "";
            DocumentBuilderFactory fabricaCreadorDocumento = DocumentBuilderFactory.newInstance();
            DocumentBuilder creadorDocumento = fabricaCreadorDocumento.newDocumentBuilder();
            Document documento = creadorDocumento.parse(path);
            //Obtener el elemento raíz del documento
            Element raiz = documento.getDocumentElement();

            //=====RUC
            NodeList nodlist = raiz.getElementsByTagName("cac:PartyIdentification");
            respuesta += nodlist.item(0).getTextContent() + "|";

            //=====InvoiceTypeCode
            NodeList InvoiceTypeCode = raiz.getElementsByTagName("cbc:InvoiceTypeCode").item(0) == null ? raiz.getElementsByTagName("cbc:ResponseCode") : raiz.getElementsByTagName("cbc:InvoiceTypeCode");
            respuesta += InvoiceTypeCode.item(0).getTextContent() + "|";
            //=====cbc:ID
            nodlist = raiz.getChildNodes();
            for (int i = 0; i < nodlist.getLength(); i++) {
                Node nod = nodlist.item(i);
                if (nod.getNodeName().equals("cbc:ID")) {
                    respuesta += nod.getTextContent().replaceAll("-", "|") + "|";
                }
                if (nod.getNodeName().equals("cbc:IssueDate")) {
                    IssueDate = nod.getTextContent();
                }
            }
            //=====>LegalMonetaryTotal

            nodlist = raiz.getElementsByTagName("cac:LegalMonetaryTotal").item(0) == null ? raiz.getElementsByTagName("cac:RequestedMonetaryTotal").item(0).getChildNodes() : raiz.getElementsByTagName("cac:LegalMonetaryTotal").item(0).getChildNodes();
            for (int i = 0; i < nodlist.getLength(); i++) {
                Node nod = nodlist.item(i);
                if (nod.getNodeName().equals("cbc:TaxExclusiveAmount")) { //NO EN : ND
                    respuesta += nod.getTextContent() + "|";
                }
                if (nod.getNodeName().equals("cbc:PayableAmount")) {
                    respuesta += nod.getTextContent() + "|";
                }
            }
            //=====>IssueDate
            respuesta += IssueDate + "|";

            //=====>AccountingCustomerParty
            nodlist = raiz.getElementsByTagName("cac:AccountingCustomerParty").item(0).getChildNodes();
            for (int i = 0; i < nodlist.getLength(); i++) {
                Node nod = nodlist.item(i);
                if (nod.getNodeName().equals("cbc:AdditionalAccountID")) {
                    respuesta += nod.getTextContent() + "|";
                }
            }
            for (int i = 0; i < nodlist.getLength(); i++) {
                Node nod = nodlist.item(i);
                if (nod.getNodeName().equals("cbc:CustomerAssignedAccountID")) {
                    respuesta += nod.getTextContent() + "|";
                }
            }

            //=====ds:DigestValue
            //NodeList DigestValue = raiz.getElementsByTagName("ds:DigestValue");
            //respuesta += DigestValue.item(0).getTextContent() + "|";

            //=====ds:ds:SignatureValue
            //NodeList SignatureValue = raiz.getElementsByTagName("ds:SignatureValue");
            //respuesta += SignatureValue.item(0).getTextContent();

        } catch (org.xml.sax.SAXException ex) {
            System.out.println("ERROR: El formato XML del fichero no es correcto\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo de respuesta\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (ParserConfigurationException ex) {
            System.out.println("ERROR: No se ha podido crear el generador de documentos XML\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (Exception exg) {
            System.out.println("==>Error while reading file " + path + " on getInfoToBarCode(path)\n");
            exg.printStackTrace();
        }
        return respuesta;
    }

    public static String getInfoToBarCodeOD(String path) {
        String respuesta = "";
        try {
            String IssueDate = "";
            DocumentBuilderFactory fabricaCreadorDocumento = DocumentBuilderFactory.newInstance();
            DocumentBuilder creadorDocumento = fabricaCreadorDocumento.newDocumentBuilder();
            Document documento = creadorDocumento.parse(path);
            //Obtener el elemento raíz del documento
            Element raiz = documento.getDocumentElement();

            //=====RUC
            NodeList nodlist = raiz.getElementsByTagName("cac:PartyIdentification");
            respuesta += nodlist.item(0).getTextContent() + "|";

            //=====InvoiceTypeCode
            //NodeList InvoiceTypeCode = raiz.getElementsByTagName("cbc:InvoiceTypeCode").item(0) == null ? raiz.getElementsByTagName("cbc:ResponseCode") : raiz.getElementsByTagName("cbc:InvoiceTypeCode");
            respuesta += "20" + "|";//InvoiceTypeCode.item(0).getTextContent() + "|";
            //=====cbc:ID
            nodlist = raiz.getChildNodes();
            for (int i = 0; i < nodlist.getLength(); i++) {
                Node nod = nodlist.item(i);
                if (nod.getNodeName().equals("cbc:ID")) {
                    respuesta += nod.getTextContent().replaceAll("-", "|") + "|";
                }
                if (nod.getNodeName().equals("cbc:IssueDate")) {
                    IssueDate = nod.getTextContent();
                }
            }
            //=====>LegalMonetaryTotal

            nodlist = raiz.getElementsByTagName("cbc:TotalInvoiceAmount");
            respuesta += nodlist.item(0).getTextContent() + "|";

            nodlist = raiz.getElementsByTagName("sac:SUNATTotalPaid");
            respuesta += nodlist.item(0).getTextContent() + "|";
            //=====>IssueDate
            respuesta += IssueDate + "|";

            //=====>AccountingCustomerParty
            nodlist = raiz.getElementsByTagName("cac:ReceiverParty").item(0).getChildNodes();
            for (int i = 0; i < nodlist.getLength(); i++) {
                Node nod = nodlist.item(i);
                if (nod.getNodeName().equals("cac:PartyIdentification")) {
                    NodeList noi = nod.getChildNodes();
                    for (int x = 0; x < noi.getLength(); x++) {
                        Node nodx = noi.item(x);
                        if (nodx.getNodeName().equals("cbc:ID")) {

                            respuesta += nodx.getAttributes().getNamedItem("schemeID").getTextContent() + "|";
                            respuesta += nodx.getTextContent() + "|";
                        }
                    }
                }
            }
            for (int i = 0; i < nodlist.getLength(); i++) {
                Node nod = nodlist.item(i);
                if (nod.getNodeName().equals("cbc:CustomerAssignedAccountID")) {
                    respuesta += nod.getTextContent() + "|";
                }
            }

            //=====ds:DigestValue
//            NodeList DigestValue = raiz.getElementsByTagName("ds:DigestValue");
//            respuesta += DigestValue.item(0).getTextContent() + "|";
//
//            //=====ds:ds:SignatureValue
//            NodeList SignatureValue = raiz.getElementsByTagName("ds:SignatureValue");
//            respuesta += SignatureValue.item(0).getTextContent();

        } catch (org.xml.sax.SAXException ex) {
            System.out.println("ERROR: El formato XML del fichero no es correcto\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo de respuesta\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (ParserConfigurationException ex) {
            System.out.println("ERROR: No se ha podido crear el generador de documentos XML\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (Exception exg) {
            System.out.println("==>Error while reading file " + path + " on getInfoToBarCode(path)\n");
            exg.printStackTrace();
        }
        return respuesta;
    }

//    public static void guardarProceso(String trans, String proceso, String[] cdr,String error ,Connection conn) throws SQLException {
////        Connection conn = null;
//        try {
////            conn = ConnectionPool.obtenerConexionMysql();
//
//            String sql = "insert into seguimiento(estado_seguimiento, docu_codigo) values (?,?)";
//
//            PreparedStatement ps = conn.prepareStatement(sql);
//            //===Inserta seguimiento de documento
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, proceso);
//            ps.setString(2, trans);
//            int reg = ps.executeUpdate();
//
//            sql = "update cabecera set docu_proce_status=?, docu_proce_fecha = now(), cdr = ?, cdr_nota = ? where docu_codigo=?";
//
//            ps = conn.prepareStatement(sql);
//            //===Actualiza estado
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, proceso);
//            ps.setString(2, trans);
//            reg = ps.executeUpdate();
//            /*================================================================*/
//        } catch (Exception ex) {
//            System.out.println("==>Error en generación de hash o barCode:" + ex.getMessage());
//            ex.printStackTrace();
//        } finally {
////            ConnectionPool.closeConexion(conn);
//        }
//    }
    public static void guardarProcesoEstado(String trans, String proceso, String[] cdr, String error, Connection conn) throws SQLException {
//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();

            String sql = "update cabecera set docu_proce_status=?, docu_proce_fecha = now(), cdr = ?, cdr_nota = ?, cdr_observacion = ? where docu_codigo=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            //===Actualiza estado
            System.out.println("__ÑÑ__" + cdr[0]);
            System.out.println("__99__" + cdr[1]);
            ps = conn.prepareStatement(sql);
            ps.setString(1, proceso);
            ps.setString(2, cdr[0]);
            ps.setString(3, cdr[1]);
            ps.setString(4, error);
            ps.setString(5, trans);
            int reg = ps.executeUpdate();

            sql = "insert into seguimiento(estado_seguimiento, docu_codigo, cdr_code, cdr_nota, cdr_observacion ) values (?,?,?,?,?)";

            ps = conn.prepareStatement(sql);
            //===Inserta seguimiento de documento
            ps = conn.prepareStatement(sql);
            ps.setString(1, proceso);
            ps.setString(2, trans);
            ps.setString(3, cdr[0]);
            ps.setString(4, cdr[1]);
            ps.setString(5, error);
            reg = ps.executeUpdate();

            /*================================================================*/
        } catch (Exception ex) {
            System.out.println("==>Error en generación de hash o barCode:" + ex.getMessage());
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
    }

    public static String getImptime(String trans, String path) {
        String respuesta = null;
        Connection conn = null;

        try {
            DocumentBuilderFactory fabricaCreadorDocumento = DocumentBuilderFactory.newInstance();
            DocumentBuilder creadorDocumento = fabricaCreadorDocumento.newDocumentBuilder();
            Document documento = creadorDocumento.parse(path);
            //Obtener el elemento raíz del documento
            Element raiz = documento.getDocumentElement();

            //Obtener la lista de nodos que tienen etiqueta "ds:Reference"
            NodeList responsecode = raiz.getElementsByTagName("cbc:ResponseCode");
            for (int i = 0; i < responsecode.getLength(); i++) {
                Node empleado = responsecode.item(i);
                Node datoContenido = empleado.getFirstChild();
                respuesta = datoContenido.getNodeValue();
            }
            NodeList nodesc = raiz.getElementsByTagName("cbc:Description");
            for (int i = 0; i < nodesc.getLength(); i++) {
                Node empleado = nodesc.item(i);
                Node datoContenido = empleado.getFirstChild();
                respuesta = respuesta + "|" + datoContenido.getNodeValue();
            }
            String sql = "update cabecera set docu_link_cdr=? where docu_codigo=?";
            //===eliminar si existente
            conn = ConnectionPool.obtenerConexionMysql();

            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar link
            ps = conn.prepareStatement(sql);
            ps.setString(1, path);
            ps.setString(2, trans);

            int reg = ps.executeUpdate();
            if (reg > 0) {
                System.out.println("==>BarCode grabado");
            } else {
                System.out.println("==>Error en generación de BarCode");
            }

        } catch (org.xml.sax.SAXException ex) {
            System.out.println("ERROR: El formato XML del fichero no es correcto\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo de respuesta\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (ParserConfigurationException ex) {
            System.out.println("ERROR: No se ha podido crear el generador de documentos XML\n" + ex.getMessage());
            ex.printStackTrace();
            respuesta = "Error al leer el archivo de respuesta";
        } catch (Exception exg) {
        } finally {
            ConnectionPool.closeConexion(conn);
        }
        return respuesta;
    }

    /**
     *
     * @throws SQLException
     */
//    public void pdf() throws SQLException {
//        JasperReport jasperReport;
//        JasperPrint jasperPrint;
//        try {
//            //se carga el reporte
//            URL in = this.getClass().getResource("reportes/BoletaElectronica.jasper");
//            jasperReport = (JasperReport) JRLoader.loadObject(in);
//            //se procesa el archivo jasper
//            jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), ConnectionPool.obtenerConexionMysql());
//            //se crea el archivo PDF
//            JasperExportManager.exportReportToPdfFile(jasperPrint, Util.getPathZipFilesEnvio() + "reporte.pdf");
//        } catch (JRException ex) {
//            System.err.println("Error iReport: " + ex.getMessage());
//        }
//    }
}
