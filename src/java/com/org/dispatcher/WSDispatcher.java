/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.dispatcher;

import com.org.factories.Util;
import com.org.util.HeaderHandlerResolver;
import com.org.util.LecturaXML;
import com.org.ws.RetElectronica;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author oswaldo
 */
public class WSDispatcher {

    private static Log log = LogFactory.getLog(WSDispatcher.class);

    public static String enviarZipASunat(String trans, String path, String zipFileName, Connection conn, String vruc) throws com.org.exceptiones.NoExisteWSSunatException {
        String resultado = "";
        String sws = Util.getWsOpcion(vruc);
        log.info("enviarASunat - Prepara ambiente: " + sws);
        try {

            javax.activation.FileDataSource fileDataSource = new javax.activation.FileDataSource(path + zipFileName);
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
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa ws3 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa();
                    HeaderHandlerResolver handlerResolver3 = new HeaderHandlerResolver();
                    handlerResolver3.setVruc(vruc);
                    ws3.setHandlerResolver(handlerResolver3);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService port3 = ws3.getBillServicePort();
                    respuestaSunat = port3.sendBill(zipFileName, dataHandler);
                    log.info("enviarASunat - Ambiente Produccion " + sws);
                    break;
                default:

                    respuestaSunat = null;
                    throw new com.org.exceptiones.NoExisteWSSunatException(sws);
            }

//            javax.activation.FileDataSource fileDataSource = new javax.activation.FileDataSource(path + zipFileName);
//            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(fileDataSource);
            //================Grabando la respuesta de sunat en archivo ZIP solo si es nulo
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
            zipFileName = zipFileName.substring(0, zipFileName.indexOf(".zip"));
            log.info("enviarASunat - Lectura del contenido del CDR ");
            resultado = LecturaXML.getRespuestaSunat(trans, pathRecepcion + "R-" + zipFileName + ".xml", conn);
            System.out.println("==>El envio del Zip a sunat fue exitoso");
            log.info("enviarASunat - Envio a Sunat Exitoso ");
        } catch (javax.xml.ws.soap.SOAPFaultException ex) {
            log.error("enviarASunat - Error " + ex.toString());
            String errorCode = ex.getFault().getFaultCodeAsQName().getLocalPart();
            errorCode = errorCode.substring(errorCode.indexOf(".") + 1, errorCode.length());
            resultado = Util.getErrorMesageByCode(errorCode, vruc);
            String sql = "insert into seguimiento(estado_seguimiento, docu_codigo, cdr_code, cdr_nota ) values (?,?,?,?)";
            try {

                PreparedStatement ps = conn.prepareStatement(sql);
                //===Inserta seguimiento de documento
                String[] cdr = resultado.split("\\|", 0);
                ps.setString(1, "ECDR");
                ps.setString(2, trans);
                ps.setString(3, cdr[0]);
                ps.setString(4, cdr[1]);
                ps.executeUpdate();
            } catch (SQLException ex1) {
                log.error("enviarASunat - Error " + ex1.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("enviarASunat - Error " + e.toString());
            resultado = "0100|Error en el envio de archivo zip al asunat";
            String sql = "insert into seguimiento(estado_seguimiento, docu_codigo, cdr_code, cdr_nota ) values (?,?,?,?)";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                //===Inserta seguimiento de documento
                ps.setString(1, "ECDR");
                ps.setString(2, trans);
                ps.setString(3, "0100");
                ps.setString(4, e.toString().substring(0, 5000));
                ps.executeUpdate();
            } catch (SQLException ex1) {
                log.error("enviarASunat - Error " + ex1.toString());
            }
        }
        return resultado;
    }
    
    public static String enviarRetencionesASunat(String trans, String unidadEnvio, String zipFileName, Connection conn, String vruc) throws com.org.exceptiones.NoExisteWSSunatException {
        String resultado = "";
        String sws = Util.getWsOpcion(vruc);
        log.info("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - Prepara ambiente: " + sws);

        try {
            //================Enviando a sunat
            javax.activation.FileDataSource fileDataSource = new javax.activation.FileDataSource(unidadEnvio + zipFileName);
            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(fileDataSource);
            System.out.println("___*** " + zipFileName);
            byte[] respuestaSunat = null;
            switch (sws) {
                case "1":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_ocp ws1 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_ocp();
                    HeaderHandlerResolver handlerResolver1 = new HeaderHandlerResolver();
                    handlerResolver1.setVruc(vruc);
                    ws1.setHandlerResolver(handlerResolver1);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService port1 = ws1.getBillServicePort();
                    respuestaSunat = port1.sendBill(zipFileName, dataHandler);
                    log.info("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - Ambiente Beta: " + sws);
                    break;
                case "3":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service_ocp ws3 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service_ocp();
                    HeaderHandlerResolver handlerResolver3 = new HeaderHandlerResolver();
                    handlerResolver3.setVruc(vruc);
                    ws3.setHandlerResolver(handlerResolver3);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService port3 = ws3.getBillServicePort();
                    respuestaSunat = port3.sendBill(zipFileName, dataHandler);
                    log.info("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - Ambiente Produccion " + sws);
                    break;
                default:
                    respuestaSunat = null;
                    log.info("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - No existe ambiente: " + sws);
                    throw new com.org.exceptiones.NoExisteWSSunatException("No existe WS " + sws);
            }
            System.out.println("___ ***" + respuestaSunat.length + "**___");
            System.out.println("___ se envio correctamete ___");
            //================Grabando la respuesta de sunat en archivo ZIP
            String pathRecepcion = Util.getPathZipFilesRecepcion(vruc);
            FileOutputStream fos = new FileOutputStream(pathRecepcion + "R-" + zipFileName);
            fos.write(respuestaSunat);
            fos.close();
            //================Descompremiendo el zip de Sunat
            log.info("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - Descomprimiendo CDR " + pathRecepcion + "R-" + zipFileName);
            ZipFile archive = new ZipFile(pathRecepcion + "R-" + zipFileName);
            Enumeration e = archive.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                File file = new File(pathRecepcion, entry.getName());
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
            zipFileName = zipFileName.substring(0, zipFileName.indexOf(".zip"));
            log.info("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - Lectura del contenido del CDR ");
            resultado = LecturaXML.getRespuestaSunat(trans, pathRecepcion + "R-" + zipFileName + ".xml", conn);
            System.out.println("==>El envio del Zip a sunat fue exitoso");
            log.info("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - Envio a Sunat Exitoso ");
        } catch (javax.xml.ws.soap.SOAPFaultException ex) {
            String errorCode = ex.getFault().getFaultCodeAsQName().getLocalPart();
            errorCode = errorCode.substring(errorCode.indexOf(".") + 1, errorCode.length());
            resultado = Util.getErrorMesageByCode(errorCode, vruc);
            Logger.getLogger(RetElectronica.class.getName()).log(Level.SEVERE, null, ex);
            log.error("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - Error " + ex.toString());
            try {
                LecturaXML.guardarProcesoEstado(trans, "R", resultado.split("\\|", 0), ex.toString(), conn);
            } catch (SQLException ex1) {
                log.error("enviarASunat - Error " + ex1.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("RetElectronica.generarXMLZipiadoRetencion.enviarASunat - Error " + e.toString());
            resultado = "0100|Error en el envio de archivo zip a sunat";
            try {
                LecturaXML.guardarProcesoEstado(trans, "X", resultado.split("\\|", 0), e.toString(), conn);
            } catch (SQLException ex) {
                log.error("enviarASunat - Error " + ex.toString());
            }
        }
        return resultado;
    }

    public static String enviarResumenASunat(String iddocument, String unidadEnvio, String zipFileName, Connection conn, String vruc) {
        String resultado = "";
        String sws = Util.getWsOpcion(vruc);
        try {
            System.out.println("===>Conversion de zip a byte[]...");
            javax.activation.FileDataSource fileDataSource = new javax.activation.FileDataSource(unidadEnvio + zipFileName);
            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(fileDataSource);
            switch (sws) {
                case "1":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_fe ws1 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_fe();
                    HeaderHandlerResolver handlerResolver1 = new HeaderHandlerResolver();
                    handlerResolver1.setVruc(vruc);
                    ws1.setHandlerResolver(handlerResolver1);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService port1 = ws1.getBillServicePort();
                    resultado = port1.sendSummary(zipFileName, dataHandler);
                    break;
                case "2":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa ws = new pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa();
                    HeaderHandlerResolver handlerResolver2 = new HeaderHandlerResolver();
                    handlerResolver2.setVruc(vruc);
                    ws.setHandlerResolver(handlerResolver2);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService port2 = ws.getBillServicePort();
                    resultado = port2.sendSummary(zipFileName, dataHandler);
                    break;
                case "3":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa ws3 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa();
                    HeaderHandlerResolver handlerResolver3 = new HeaderHandlerResolver();
                    handlerResolver3.setVruc(vruc);
                    ws3.setHandlerResolver(handlerResolver3);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService port3 = ws3.getBillServicePort();
                    resultado = port3.sendSummary(zipFileName, dataHandler);
                    break;
                default:

                    resultado = "";
                    throw new com.org.exceptiones.NoExisteWSSunatException(sws);
            }

            String sql = "update resumendia_cab set nroticket=?, resu_proce_status='O', resu_fecha_com = ? where codigo=?";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar ticket
            ps = conn.prepareStatement(sql);
            ps.setString(1, resultado);
            ps.setString(2, sdf.format(new Date()));
            ps.setString(3, iddocument);

            int reg = ps.executeUpdate();
            if (reg > 0) {
                System.out.println("==>Ticket grabado");
            } else {
                System.out.println("==>Error en grabar Ticket");
            }
            resultado = "0|" + resultado;
            System.out.println("==>El envio del Zip a sunat fue exitoso");
        } catch (javax.xml.ws.soap.SOAPFaultException ex) {
            String errorCode = ex.getFault().getFaultCodeAsQName().getLocalPart();
            errorCode = errorCode.substring(errorCode.indexOf(".") + 1, errorCode.length());
            resultado = Util.getErrorMesageByCode(errorCode, vruc);
        } catch (Exception e) {
            e.printStackTrace();
            resultado = "-1|Error en el envio de archivo resumen zip a sunat.";
        }
        return resultado;
    }

    public static String enviarBajaASunat(String iddocument, String unidadEnvio, String zipFileName, Connection conn, String vruc) throws com.org.exceptiones.NoExisteWSSunatException {
        String resultado = "";
        String sws = Util.getWsOpcion(vruc);
        try {

            javax.activation.FileDataSource fileDataSource = new javax.activation.FileDataSource(unidadEnvio + zipFileName);
            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(fileDataSource);

            switch (sws) {
                case "1":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_fe ws1 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_fe();
                    HeaderHandlerResolver handlerResolver1 = new HeaderHandlerResolver();
                    handlerResolver1.setVruc(vruc);
                    ws1.setHandlerResolver(handlerResolver1);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService port1 = ws1.getBillServicePort();
                    resultado = port1.sendSummary(zipFileName, dataHandler);
                    break;
                case "2":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa ws = new pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa();
                    HeaderHandlerResolver handlerResolver2 = new HeaderHandlerResolver();
                    handlerResolver2.setVruc(vruc);
                    ws.setHandlerResolver(handlerResolver2);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService port2 = ws.getBillServicePort();
                    resultado = port2.sendSummary(zipFileName, dataHandler);
                    System.out.println("Resultado ==> " + resultado);
                    break;
                case "3":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa ws3 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService_Service_sqa();
                    HeaderHandlerResolver handlerResolver3 = new HeaderHandlerResolver();
                    handlerResolver3.setVruc(vruc);
                    ws3.setHandlerResolver(handlerResolver3);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.servicesqa.BillService port3 = ws3.getBillServicePort();
                    resultado = port3.sendSummary(zipFileName, dataHandler);
                    break;
                default:

                    resultado = "";
                    throw new com.org.exceptiones.NoExisteWSSunatException(sws);
            }
            //leeyendo el zip
            //=== Guardar el Ticket
            String sql = "update resumendia_baja set nroticket=?, resu_proce_status='O', resu_fecha_com = ? where codigo=?";
//            conn = ConnectionPool.obtenerConexionMysql();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar ticket
            ps = conn.prepareStatement(sql);
            ps.setString(1, resultado);
            ps.setString(2, sdf.format(new Date()));
            ps.setString(3, iddocument);

            int reg = ps.executeUpdate();
            if (reg > 0) {
                System.out.println("==>Ticket grabado");
            } else {
                System.out.println("==>Error en grabar Ticket");
            }
            resultado = "0|" + resultado;
            System.out.println("==>El envio del Zip a sunat fue exitoso");
        } catch (javax.xml.ws.soap.SOAPFaultException ex) {
            String errorCode = ex.getFault().getFaultCodeAsQName().getLocalPart();
            errorCode = errorCode.substring(errorCode.indexOf(".") + 1, errorCode.length());
            resultado = Util.getErrorMesageByCode(errorCode, vruc);
        } catch (Exception e) {
            e.printStackTrace();
            resultado = "0100|Error en el envio de archivo resumen zip a sunat.";
        }
        return resultado;
    }

    public static String enviarResumenReversionesASunat(String iddocument, String unidadEnvio, String zipFileName, Connection conn, String vruc) {
        String resultado = "";
        String sws = Util.getWsOpcion(vruc);
        log.info("ResumenReversion.enviarReversionASunat.enviarZipASunat - Preparando Ambiente " + sws);

        try {

            javax.activation.FileDataSource fileDataSource = new javax.activation.FileDataSource(unidadEnvio + zipFileName);
            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(fileDataSource);
            System.out.println("___*** " + zipFileName);
            switch (sws) {
                case "1":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_ocp ws1 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_ocp();
                    HeaderHandlerResolver handlerResolver1 = new HeaderHandlerResolver();
                    handlerResolver1.setVruc(vruc);
                    ws1.setHandlerResolver(handlerResolver1);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService port1 = ws1.getBillServicePort();
                    //respuestaSunat = port1.sendBill(zipFileName, dataHandler);
                    resultado = port1.sendSummary(zipFileName, dataHandler);
                    log.info("ResumenReversion.enviarReversionASunat.enviarZipASunat - Ambiente beta" + sws);
                    break;
                case "3":
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service_ocp ws3 = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service_ocp();
                    HeaderHandlerResolver handlerResolver3 = new HeaderHandlerResolver();
                    handlerResolver3.setVruc(vruc);
                    ws3.setHandlerResolver(handlerResolver3);
                    pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService port3 = ws3.getBillServicePort();
                    resultado = port3.sendSummary(zipFileName, dataHandler);
                    log.info("ResumenReversion.enviarReversionASunat.enviarZipASunat - Ambiente Produccion" + sws);
                    break;
                default:
                    resultado = null;
                    log.info("ResumenReversion.enviarReversionASunat.enviarZipASunat - No existe ambiente " + sws);
                    throw new com.org.exceptiones.NoExisteWSSunatException("No existe WS " + sws);
            }
            //=== Guardar el Ticket
            String sql = "update resumendia_baja set nroticket=?, resu_proce_status='O' where codigo=?";
//            conn = ConnectionPool.obtenerConexionMysql();

            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar ticket
            ps = conn.prepareStatement(sql);
            ps.setString(1, resultado);
            ps.setString(2, iddocument);

            int reg = ps.executeUpdate();
            if (reg > 0) {
                System.out.println("==>Ticket grabado");
            } else {
                System.out.println("==>Error en grabar Ticket");
            }
            log.info("ResumenReversion.enviarReversionASunat.enviarZipASunat - Ticket " + resultado);
            resultado = "0|" + resultado;
            System.out.println("==>El envio del Zip a sunat fue exitoso");
        } catch (javax.xml.ws.soap.SOAPFaultException ex) {
            String errorCode = ex.getFault().getFaultCodeAsQName().getLocalPart();
            errorCode = errorCode.substring(errorCode.indexOf(".") + 1, errorCode.length());
            resultado = Util.getErrorMesageByCode(errorCode, vruc);
            log.error("ResumenReversion.enviarReversionASunat.enviarZipASunat - Error" + ex.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ResumenReversion.enviarReversionASunat.enviarZipASunat - Error" + e.toString());
            resultado = "0100|Error en el envio de archivo resumen zip a sunat.";
        }
        return resultado;
    }
    
}
