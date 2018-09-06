/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.despatchers;

//import com.urp.xml.beans.DocumentoBean;
//import com.urp.factories.ConnectionPool;
import com.org.factories.ConnectionPool;
import com.org.model.beans.DocumentoBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author rmoscoso
 */
public class ResumenDespachador {

    public static DocumentoBean cargarCabecera(String trans, Connection conn) {
        //Connection conn = null;
        DocumentoBean cabecera = null;
        try {
            conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT ";
            sql += " EMPR_RAZONSOCIAL,";
            sql += " EMPR_NRORUC,";
            sql += " RESU_FECHA_DOC,";
            sql += " RESU_IDENTIFICADOR,";
            sql += " RESU_FECHA_COM,";
            sql += " NROTICKET";
            sql += " FROM resumendia_cab ";//estos serian los campos ok
            sql += " WHERE CODIGO  = ? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, trans);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cabecera = new DocumentoBean();
                cabecera.setEmpr_razonsocial(rs.getString("empr_razonsocial"));
                cabecera.setEmpr_nroruc(rs.getString("empr_nroruc"));
                cabecera.setResu_fecha(rs.getString("resu_fecha_doc"));
                cabecera.setResu_identificador(rs.getString("resu_identificador"));
                if (rs.getString("resu_fecha_com") == null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cabecera.setResu_fec(sdf.format(new Date()));
                } else {
                    cabecera.setResu_fec(rs.getString("resu_fecha_com"));
                }
                cabecera.setCdesu_nroticket(rs.getString("NROTICKET"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //ConnectionPool.closeConexion(conn);
        }
        return cabecera;
    }

    public static List<DocumentoBean> cargarResumen(String trans, Connection conn) throws Exception {
        List<DocumentoBean> res = new ArrayList<DocumentoBean>();
        //Connection conn = null;
        try {
            conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT RESU_FECHA,";
            sql += " RESU_FILA,";
            sql += " RESU_TIPODOC,";
            sql += " RESU_SERIE,";
            sql += " RESU_INICIO, ";
            sql += " RESU_FINAL,";
            sql += " RESU_GRAVADA,";
            sql += " RESU_EXONERADA,";
            sql += " RESU_INAFECTA,";
            sql += " RESU_OTCARGOS,";
            sql += " RESU_ISC,";
            sql += " RESU_IGV,";
            sql += " RESU_OTTRIBUTOS,";
            sql += " RESU_TOTAL";
            sql += " FROM resumenboleta  WHERE CODIGO = ?";
// ya estaue 
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, trans);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DocumentoBean resumen = new DocumentoBean();
                resumen.setResu_fecha(rs.getString("resu_fecha"));
                resumen.setResu_fila(rs.getString("resu_fila"));
                resumen.setResu_tipodoc(rs.getString("resu_tipodoc"));
                resumen.setResu_serie(rs.getString("resu_serie"));
                resumen.setResu_inicio(rs.getString("resu_inicio"));
                resumen.setResu_final(rs.getString("resu_final"));
                resumen.setResu_gravada(rs.getString("resu_gravada"));
                resumen.setResu_exonerada(rs.getString("resu_exonerada"));
                resumen.setResu_inafecta(rs.getString("resu_inafecta"));
                resumen.setResu_otcargos(rs.getString("resu_otcargos"));
                resumen.setResu_isc(rs.getString("resu_isc"));
                resumen.setResu_igv(rs.getString("resu_igv"));
                resumen.setResu_ottributos(rs.getString("resu_ottributos"));
                resumen.setResu_total(rs.getString("resu_total"));
                res.add(resumen);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //ConnectionPool.closeConexion(conn);
        }
        return res;
    }

    public static List<DocumentoBean> cargarResumenDia(String trans, Connection conn) throws Exception {
        List<DocumentoBean> res = new ArrayList<DocumentoBean>();
        //Connection conn = null;
        try {
            //conn = ConnectionPool.obtenerConexionMysql();
            String sql = " ";
            sql += " SELECT                      ";
            sql += " resu_fecha_doc,             ";
            sql += " empr_razonsocial,           ";
            sql += " empr_nroruc,                ";
            sql += " resu_fila,                  ";
            sql += " resu_tipodoc,               ";
            sql += " resu_serie_correlativo,     ";
            sql += " resu_clie_numero,           ";
            sql += " resu_clie_tipodoc,          ";
            sql += " resu_boleta_mod,            ";
            sql += " resu_tipodoc_mod,           ";
            sql += " resu_estado,                ";
            sql += " resu_total,                 ";
            sql += " resu_gravada,               ";
            sql += " resu_exonerada,             ";
            sql += " resu_inafecta,              ";
            sql += " resu_gratuito,              ";
            sql += " resu_otcargos,              ";
            sql += " resu_isc,                   ";
            sql += " resu_igv,                   ";
            sql += " resu_ottributos,            ";
            sql += " resu_moneda                 ";
            sql += " FROM resumenboleta a, resumendia_cab b";
            sql += " WHERE a.codigo = b.codigo and    ";
            sql += " resu_identificador = ?      ";
            sql += " ";

            // ya estaue 
            System.out.println("Preparndose " + trans );
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, trans);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("cargarResumenDia(): " + rs.getString(6));
                
                DocumentoBean resumen = new DocumentoBean();
                resumen.setDocu_fecha(rs.getString(1));
                resumen.setEmpr_razonsocial(rs.getString("empr_razonsocial"));
                resumen.setEmpr_nroruc(rs.getString("empr_nroruc"));
                resumen.setResu_fila(rs.getString("resu_fila"));
                resumen.setDocu_tipodocumento(rs.getString(5));
                resumen.setDocu_numero(rs.getString(6));
                resumen.setClie_numero(rs.getString(7));
                resumen.setClie_tipodoc(rs.getString(8));
                resumen.setNota_documento(rs.getString(9));
                resumen.setNota_tipodoc(rs.getString(10));
                resumen.setItem_estado(rs.getString(11));
                resumen.setDocu_total(rs.getString(12));
                resumen.setDocu_gravada(rs.getString(13));
                resumen.setDocu_exonerada(rs.getString(14));
                resumen.setDocu_inafecta(rs.getString(15));
                resumen.setDocu_gratuita(rs.getString(16));
                resumen.setDocu_otroscargos(rs.getString(17));
                resumen.setDocu_isc(rs.getString(18));
                resumen.setDocu_igv(rs.getString(18));
                resumen.setDocu_otrostributos(rs.getString(20));
                resumen.setDocu_moneda(rs.getString(21));
                res.add(resumen);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //ConnectionPool.closeConexion(conn);
        }
        return res;
    }

    public static DocumentoBean pendienteResElectronicoDiario(Connection conn) {
        DocumentoBean cabecera = null;

//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT * ";
            sql += " FROM resumendia_baja ";
            sql += " WHERE  resu_proce_status = 'N' ";
            sql += " union  ";
            sql += " SELECT *  ";
            sql += " FROM resumendia_cab ";
            sql += " WHERE  resu_proce_status  = 'N'  ";
            sql += " order by codigo LIMIT 1 ";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cabecera = new DocumentoBean();
                cabecera.setResu_codigo(rs.getString("codigo"));
                cabecera.setEmpr_razonsocial(rs.getString("empr_razonsocial"));
                cabecera.setEmpr_nroruc(rs.getString("empr_nroruc"));
                cabecera.setResu_fecha(rs.getString("resu_fecha_doc"));
                cabecera.setResu_identificador(rs.getString("resu_identificador"));
                cabecera.setResu_fec(rs.getString("resu_fecha_com"));
                cabecera.setResu_tipo(rs.getString("resu_tipo"));
                cabecera.setResu_version(rs.getString("version"));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return cabecera;
    }

}
