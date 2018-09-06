/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import com.org.factories.Util;
import com.org.model.beans.DocumentoBean;
import com.org.ws.FacElectronica;
import com.org.ws.RetElectronica;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author oswaldo
 */
public class GeneralFunctions {

    private static Log log = LogFactory.getLog(LecturaXML.class);

    public static String enviarEmail(DocumentoBean items, String unidadEnvio, Connection conn, String resultado, String etiqueta) {
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        try {

            String[] email = resultado.split("\\|", 0);
            String[] rutasFile = {unidadEnvio, Util.getPathZipFilesRecepcion(items.getEmpr_nroruc())};
            if (email[0].equals("0")) {
                if (items.getClie_correo_cpe1() != null && items.getClie_correo_cpe1().trim().length() > 10) {
                    Matcher mather = pattern.matcher(items.getClie_correo_cpe1());

                    if (mather.find() == true) {
                        log.info(" GeneralFunctions(enviarEmail) - Enviando correo " + items.getEmpr_nroruc() + "-" + items.getDocu_tipodocumento() + "-" + items.getDocu_numero());
                        LecturaXML.guardarProcesoEstado(items.getDocu_codigo(), "C", " | ".split("\\|", 0), " ", conn);

                        System.out.println("___ ** _Vamos a enviar correo  **___");

                        EnvioMail em = new EnvioMail(etiqueta,
                                items.getDocu_tipodocumento(),
                                items.getDocu_numero(),
                                items.getDocu_fecha(),
                                items.getClie_nombre(),
                                items.getEmpr_razonsocial(),
                                rutasFile,
                                items.getEmpr_nroruc() + "-" + items.getDocu_tipodocumento() + "-" + items.getDocu_numero(),
                                items.getClie_correo_cpe1(),
                                items.getClie_correo_cpe2() == null ? null : items.getClie_correo_cpe2().equals("") ? null : items.getClie_correo_cpe2(),
                                items.getEmpr_nroruc(),
                                conn
                        );
                        em.sendMessage();
                        System.out.println("___ ** Se envio correo  **___");
                        log.info("GeneralFunctions(enviarEmail) - Correo enviado a  " + items.getClie_correo_cpe1());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //resultado = "0100|Error al enviar el correo al cliente.";
            log.error("GeneralFunctions(enviarEmail) - error  " + ex.toString());
            try {
                //LecturaXML.guardarProcesoEstado(iddocument, "Q", resultado.split("\\|", 0), ex.getStackTrace()[0].toString(), conn);
                LecturaXML.guardarProcesoEstado(items.getDocu_codigo(), "Q", resultado.split("\\|", 0), ex.getStackTrace()[0].toString(), conn);

            } catch (SQLException ex1) {
                log.error(ex1);
            }
        }
        return resultado;

    }

    public static String copiaAFtp(DocumentoBean items, String unidadEnvio, Connection conn) {
        String resultado = "";

        log.info("GeneralFunctions(copiaAFtp) - Subiendo PDF al ftp ");
        String ftpServer = Util.getFtpServer(items.getEmpr_nroruc());
        try {
            if (ftpServer != null && ftpServer.trim().length() > 8) {
                LecturaXML.guardarProcesoEstado(items.getDocu_codigo(), "F", " | ".split("\\|", 0), " ", conn);
                String[] ftpAMes = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SETIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};

                String ftpUser = Util.getFtpUser(items.getEmpr_nroruc());
                String ftpPass = Util.getFtpPass(items.getEmpr_nroruc());
                String ftpFilepdf = unidadEnvio + items.getEmpr_nroruc() + "-"+items.getDocu_tipodocumento()+"-" + items.getDocu_numero() + ".pdf";
                String ftpFilexml = unidadEnvio + items.getEmpr_nroruc() + "-"+items.getDocu_tipodocumento()+"-" + items.getDocu_numero() + ".xml";
                String ftpFilepng = unidadEnvio + items.getEmpr_nroruc() + "-"+items.getDocu_tipodocumento()+"-" + items.getDocu_numero() + ".png";
                String ftpAnio = items.getDocu_fecha().substring(0, 4);
                String ftpMes = ftpAMes[Integer.valueOf(items.getDocu_fecha().substring(5, 7)) - 1];
                String ftpBase = Util.getFtpBase(items.getEmpr_nroruc());

                FTPClient client = new FTPClient();
                client.connect(ftpServer);
                boolean login = client.login(ftpUser, ftpPass);
                if (login) {
                    // ingreso al directorio base
                    if (ftpBase != null && !ftpBase.isEmpty()) {
                        if (!client.changeWorkingDirectory(ftpBase)) {
                            // si no existe creo el directorio e ingreso
                            client.makeDirectory(ftpBase);
                            client.changeWorkingDirectory(ftpBase);
                        }
                    }
                    // ingreso al directorio 
                    if (!client.changeWorkingDirectory(ftpAnio)) {
                        // si no existe creo el directorio e ingreso
                        client.makeDirectory(ftpAnio);
                        client.changeWorkingDirectory(ftpAnio);
                    }
                    System.out.println(client.printWorkingDirectory());
                    if (!client.changeWorkingDirectory(ftpMes)) {
                        // si no existe creo el directorio e ingreso
                        client.makeDirectory(ftpMes);
                        client.changeWorkingDirectory(ftpMes);
                    }
                    System.out.println(client.printWorkingDirectory());
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    client.enterLocalPassiveMode();

                    FileInputStream fisf = new FileInputStream(ftpFilepdf);

                    // Guardando el archivo en el servidor
                    client.storeFile(items.getIdExterno() + ".pdf", fisf);

                    fisf = new FileInputStream(ftpFilexml);

                    // Guardando el archivo en el servidor
                    client.storeFile(items.getIdExterno() + ".xml", fisf);

                    fisf = new FileInputStream(ftpFilepng);

                    // Guardando el archivo en el servidor
                    client.storeFile(items.getIdExterno() + ".png", fisf);
                    // Cerrando sesión
                    client.logout();

                    // Desconectandose con el servidor
                    client.disconnect();
                    log.info("generarXMLZipiadoFactura - PDF en ftp");

                }
            }

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            Logger.getLogger(RetElectronica.class.getName()).log(Level.SEVERE, null, ioe);
            log.error("generarXMLZipiadoFactura - PDF " + ioe.getMessage());
            //throw new com.org.exceptiones.NoExisteWSSunatException("Error de Ftp: " + ftpServer);
            try {
                LecturaXML.guardarProcesoEstado(items.getDocu_codigo(), "Q", resultado.split("\\|", 0), ioe.getStackTrace()[0].toString(), conn);
            } catch (SQLException ex1) {
                log.error(ex1);
            }

        } catch (NumberFormatException e) {
            resultado = "-2|Ftp No existe ." + ftpServer;
            log.error("generarXMLZipiadoFactura - PDF " + resultado);
            //throw new com.org.exceptiones.NoExisteWSSunatException("Error de Ftp: " + ftpServer);
            try {
                LecturaXML.guardarProcesoEstado(items.getDocu_codigo(), "Q", resultado.split("\\|", 0), e.getStackTrace()[0].toString(), conn);
            } catch (SQLException ex1) {
                log.error(ex1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacElectronica.class.getName()).log(Level.SEVERE, null, ex);
            try {
                LecturaXML.guardarProcesoEstado(items.getDocu_codigo(), "Q", resultado.split("\\|", 0), ex.getStackTrace()[0].toString(), conn);
            } catch (SQLException ex1) {
                log.error(ex1);
            }
        }
        return resultado;
    }

    public static String creaPdf(DocumentoBean items, String unidadEnvio, Connection conn, String jasperDoc) {
        String resultado = "";
        try {

            log.info("generarXMLZipiadoFactura - Crear PDF");
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("P_docu", items.getDocu_codigo());
            parametros.put("SUBREPORT_DIR", Util.getPathJasperFiles(items.getEmpr_nroruc()));

            EjecutorReporte executor = new EjecutorReporte(Util.getPathJasperFiles(items.getEmpr_nroruc()) + jasperDoc,
                    parametros, unidadEnvio + items.getEmpr_nroruc() + "-" + items.getDocu_tipodocumento() + "-" + items.getDocu_numero() + ".pdf", Util.getPathFilesLogo(items.getEmpr_nroruc()) + "LogoEmpresa.gif");
            executor.execute(conn);
            log.info("generarXMLZipiadoFactura - PDF creado " + unidadEnvio + items.getEmpr_nroruc() + "-" + items.getDocu_tipodocumento() + "-" + items.getDocu_numero() + ".pdf");
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

    public static String crearZip(DocumentoBean items, String unidadEnvio, Connection conn, File signatureFile) {
        String resultado = "";
        try {
            //Mandar a zip
            log.info("generarXMLZipiadoFactura - Crear ZIP ");
            String inputFile = signatureFile.toString();
            FileInputStream in = new FileInputStream(inputFile);
            FileOutputStream out = new FileOutputStream(unidadEnvio + items.getEmpr_nroruc() + "-" + items.getDocu_tipodocumento() + "-" + items.getDocu_numero() + ".zip");

            byte b[] = new byte[2048];
            try (ZipOutputStream zipOut = new ZipOutputStream(out)) {
                ZipEntry entry2 = new ZipEntry(items.getEmpr_nroruc() + "-" + items.getDocu_tipodocumento() + "-" + items.getDocu_numero() + ".xml");
                zipOut.putNextEntry(entry2);
                System.out.println("==>Zip generado: " + items.getEmpr_nroruc() + "-" + items.getDocu_tipodocumento() + "-" + items.getDocu_numero() + ".zip");
                int len = 0;
                while ((len = in.read(b)) != -1) {
                    zipOut.write(b, 0, len);
                }
                zipOut.closeEntry();
            }
            out.close();
            in.close();
            log.info("generarXMLZipiadoFactura - Zip creado " + unidadEnvio + items.getEmpr_nroruc() + "-" + items.getDocu_tipodocumento() + "-" + items.getDocu_numero() + ".zip");

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
