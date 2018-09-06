package com.org.util;

import com.org.factories.Util;
import com.outjected.email.api.ContentDisposition;
import com.outjected.email.api.MailMessage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;

public class EnvioMail {

    private String tipoDoc;
    private String docMsg;
    private String asunto;
    private String nrodoc;
    private String fechaDoc;
    private String clieNombre;
    private String emprNombre;
    private String[] rutasFiles;
    private String nomFile;
    private String destinoTo;
    private String destinoCC;
    private String empr_nroruc;
    private Connection conn;

    private boolean reporteGenerado;

    public EnvioMail(String docMsg, String tipoDoc, String nrodoc, String fechaDoc, String clieNombre, String emprNombre, String[] rutasFiles, String nomFile, String destinoTo, String destinoCC, String empr_nroruc, Connection conn) {
        this.docMsg = docMsg;
        this.tipoDoc = tipoDoc;
        this.asunto = emprNombre + " - USTED HA RECIBIDO UN COMPROBANTE ELECTRONICO - " + docMsg + " # " + nrodoc;
        this.nrodoc = nrodoc;
        this.fechaDoc = fechaDoc;
        this.clieNombre = clieNombre;
        this.emprNombre = emprNombre;
        this.rutasFiles = rutasFiles;
        this.nomFile = nomFile;
        this.destinoTo = destinoTo;
        this.destinoCC = destinoCC;
        this.empr_nroruc = empr_nroruc;
        this.conn = conn;

    }

    public void sendMessage() throws MessagingException, IOException {
        String emprlink = Interno.buscarEmpresa(this.empr_nroruc, this.conn);
        if (emprlink == null) {
            emprlink = "";
        }
        String mensajeHtml = ""
                + "Estimado : " + this.clieNombre + "<BR>"
                + "Se adjunta al correo, el documento electrónico (" + this.docMsg + ") ya emitido y declarado a Sunat con los siguientes datos:\n"
                //                + "Le comunicamos que se ha emitido el siguiente comprobante: \n"
                + "<BR>"
                + "<BR>"
                //+ "Tipo de Documento: " + this.docMsg + "<BR>"
                + "Numero de Documento Electrónico: " + this.nrodoc + "<BR>"
                + "Fecha de Emisión: " + this.fechaDoc + "<BR>"
                + "<BR><BR>"
                + "Si desea consultar u obtener este u otros documentos generado a su empresa puede hacerlo entrando en el siguiente link: " + emprlink + " "
                + "<BR>"
                + "Nota: Si es la primera vez que entra al servidor de consulta, el usuario y clave a pedir es su RUC. Luego de ingresar podrá cambiar su clave a una privada. Su usuario siempre será su RUC."
                + "<BR>"
                + "<BR>"
                + "Atentamente,"
                + "<BR>"
                + this.emprNombre;

        Mailer mailer = new Mailer();

        MailMessage mensaje = mailer.nuevoEmail(this.empr_nroruc);
        mensaje.from(Util.getMailUser(this.empr_nroruc))
                .to(this.destinoTo)
                .subject(this.asunto)
                .bodyHtml(mensajeHtml);
//                .addHeader( "Content-Type", "text/html; charset=UTF-8" );
        if (this.destinoCC != null) {
            mensaje.cc(this.destinoCC);
        }

        // Archivo PDF
        String nombreArchivo = this.nomFile + ".pdf";
        DataSource fuente = new FileDataSource(this.rutasFiles[0] + nombreArchivo);

        InputStream inputStream = new DataHandler(fuente).getInputStream();

        mensaje.addAttachment(nombreArchivo, "application/pdf", ContentDisposition.INLINE, inputStream);

        // Archivo XML
        nombreArchivo = this.nomFile + ".xml";
        fuente = new FileDataSource(this.rutasFiles[0] + nombreArchivo);
        inputStream = new DataHandler(fuente).getInputStream();

        mensaje.addAttachment(nombreArchivo, "application/xml-dtd", ContentDisposition.INLINE, inputStream);

        // Archivo XML CDR
        System.out.println("com.org.util.EnvioMail.sendMessage(): tipoDoc : " + this.tipoDoc);
        if (!"03".equals(this.tipoDoc)) {
            if (!"B".equals(this.nrodoc.substring(0, 1))) {
                nombreArchivo = "R-" + this.nomFile + ".xml";
                fuente = new FileDataSource(this.rutasFiles[1] + nombreArchivo);
                inputStream = new DataHandler(fuente).getInputStream();

                mensaje.addAttachment(nombreArchivo, "application/xml-dtd", ContentDisposition.INLINE, inputStream);
            }
        }
        mensaje.send();

//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
    }

}
