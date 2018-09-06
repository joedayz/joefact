package com.org.util;

import com.org.ws.FacElectronica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EjecutorReporte {

    private static Log log = LogFactory.getLog(EjecutorReporte.class);

    private String rutaReporte;
    private Map<String, Object> parametros;
    private String nombreReporteGenerado;

    private boolean reporteGenerado;

    public EjecutorReporte(String rutaReporte, Map<String, Object> parametros, String nombreReporteGenerado, String logoPdf) {
        super();
        this.rutaReporte = rutaReporte;
        this.parametros = parametros;
        this.nombreReporteGenerado = nombreReporteGenerado;
        this.parametros.put(JRParameter.REPORT_LOCALE, new Locale("es", "PE"));
        this.parametros.put("P_logo", logoPdf);

    }

    /**
     *
     * @param connection
     */
    public void execute(Connection conn) throws SQLException {
//        Connection conn = null;

        try {
//            System.out.println(" cargamos el archivo impresion");
//            System.out.println("Parametros " + this.parametros);
//            System.out.println(" abrimos el print");

//            conn = ConnectionPool.obtenerConexionMysql();
            log.info("execute - Inicializamos el ambiente");
            JasperReport reporte;
            log.info("execute - Leemos ruta del reporte");
            reporte = (JasperReport) JRLoader.loadObjectFromFile(this.rutaReporte);
            JasperPrint jasperPrint;
            jasperPrint = JasperFillManager.fillReport(reporte, parametros, conn);
            this.reporteGenerado = jasperPrint.getPages().size() > 0;
            //System.out.println(" contamos cuantas paginas son?" + jasperPrint.getPages().size());
            if (this.reporteGenerado) {
                //System.out.println(" a convertoit a pdf");
                JasperExportManager.exportReportToPdfFile(jasperPrint, this.nombreReporteGenerado);
                log.info("execute - se genero reporte");
            }
            //=== Guardar el link
            log.info("execute - Prepsarando Para grabr ruta");
            String sql = "update cabecera set docu_link_pdf=? where docu_codigo=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            //===insertar link
            String trans = "0";
            //== busco el P_docu
            for (Map.Entry e : parametros.entrySet()) {
                //System.out.println(e.getKey() + " " + e.getValue());
                if (e.getKey().equals("P_docu")) {
                    trans = e.getValue().toString();
                }
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, this.nombreReporteGenerado);
            ps.setString(2, trans);

            int reg = ps.executeUpdate();
            log.info("execute - ruta grabada");
            if (reg > 0) {
                System.out.println("==>BarCode grabado");
            } else {
                System.out.println("==>Error en generación de BarCode");
            }

        } catch (JRException e) {
            log.error("execute - Error " + e.getStackTrace()[0].toString() );
            throw new SQLException("Error al generar el reporte " + this.rutaReporte, e.getStackTrace()[0].toString());
            //System.out.println("Error al generar el reporte " + this.rutaReporte + " -- " + e);
        }
    }

    public boolean isReporteGenerado() {
        return reporteGenerado;
    }

}
