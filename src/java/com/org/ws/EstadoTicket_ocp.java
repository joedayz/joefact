/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.ws;

import com.org.factories.ConnectionPool;
import com.org.factories.Util;
import com.org.model.beans.DocumentoBean;
import com.org.model.despatchers.ResumenDespachador;
import com.org.util.HeaderHandlerResolver;
import com.org.util.LecturaXML;
//import com.urp.xml.despachadores.ResumenDespachador;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import pe.gob.sunat.service.StatusResponse;
import pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService;
import pe.gob.sunat.servicio.registro.comppago.factura.gem.service_bta.BillService_Service_ocp;

/**
 *
 * @author vpipa
 */
public class EstadoTicket_ocp {

    public static String obtenerEstado(String iddocument) {
        String resultado = "";
        String unidadRecepcion; // = Util.getPathZipFilesRecepcion();
        String vruc = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.obtenerConexionMysql();
            String statusCode = "";
            DocumentoBean doc = ResumenDespachador.cargarCabecera(iddocument, conn);
            unidadRecepcion = Util.getPathZipFilesRecepcion(doc.getEmpr_nroruc());
            vruc = doc.getEmpr_nroruc();
            String nroTiket = doc.getCdesu_nroticket();
            String zipFileName = "R-" + doc.getEmpr_nroruc() + "-" + doc.getResu_identificador() + ".zip";
            System.out.println("==>Consultando a sunat");
            BillService_Service_ocp ws = new BillService_Service_ocp();
            HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
            handlerResolver.setVruc(vruc);
            ws.setHandlerResolver(handlerResolver);
            BillService port = ws.getBillServicePort();
            StatusResponse statusResponse = port.getStatus(nroTiket);
            statusCode = statusResponse.getStatusCode();
            if (statusCode.equals("0") || statusCode.equals("99")) {
                /*0:Aceptado 99:procesado con errores(Rechazado) 98:En proceso*/
                //grabamos la respuesta sunat en zip
                byte[] wikiArray = statusResponse.getContent();
                //================Grabando la respuesta de sunat en archivo ZIP
                FileOutputStream fos = new FileOutputStream(unidadRecepcion + zipFileName);
                fos.write(wikiArray);
                fos.close();
                //================Descompremiendo el zip de Sunat
                ZipFile archive = new ZipFile(unidadRecepcion + zipFileName);
                Enumeration e = archive.entries();
                while (e.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    File file = new File(unidadRecepcion, entry.getName());
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
                archive.close();
                //================leeyendo la resuesta de Sunat
                System.out.println("==>Leeyendo el archivo de respuesta");
                zipFileName = zipFileName.substring(0, zipFileName.indexOf(".zip"));
                resultado = LecturaXML.getRespuestaSunat("0", unidadRecepcion + zipFileName + ".xml", conn);
            } else if (statusCode.equals("98")) {
                resultado = "98|En Proceso";
            }
        } catch (javax.xml.ws.soap.SOAPFaultException ex) {
            String errorCode = ex.getFault().getFaultCodeAsQName().getLocalPart();
            errorCode = errorCode.substring(errorCode.indexOf(".") + 1, errorCode.length());
            resultado = Util.getErrorMesageByCode(errorCode, vruc);
        } catch (Exception e) {
            e.printStackTrace();
            resultado = "0100|Error en la consulta del estado del tiket. " + iddocument;
        } finally {
            ConnectionPool.closeConexion(conn);
        }
        return resultado;
    }

}
