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
 * @author vpipa
 */
public class ResumenBajaDespachador {

    public static DocumentoBean cargarCabecera(String trans, Connection conn) {
        DocumentoBean cabecera = null;
//        Connection conn = null;
        try {
            //conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT EMPR_RAZONSOCIAL, ";
            sql += " EMPR_NRORUC, ";
            sql += " RESU_FECHA_DOC, "; //YYYY-MM-DD
            sql += " RESU_IDENTIFICADOR, ";
            sql += " RESU_FECHA_COM "; //YYYY-MM-DD
            sql += " FROM resumendia_baja";
            sql += " ";
            sql += " WHERE CODIGO = ?"; //ID
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
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return cabecera;
    }

    public static List<DocumentoBean> cargarResumen(String trans, Connection conn) throws Exception {
        List<DocumentoBean> res = new ArrayList<DocumentoBean>();
//        Connection conn = null;
        try {
            //conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT  RESU_FECHA,";
            sql += " RESU_FILA, ";
            sql += " RESU_TIPODOC, ";
            sql += " RESU_SERIE,";
            sql += " RESU_NUMERO, ";
            sql += " RESU_MOTIVO";
            sql += " FROM  comunicabaja ";
            sql += " WHERE CODIGO = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, trans);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DocumentoBean resumen = new DocumentoBean();
                resumen.setResu_fecha(rs.getString("resu_fecha"));
                resumen.setResu_fila(rs.getString("resu_fila"));
                resumen.setResu_tipodoc(rs.getString("resu_tipodoc"));
                resumen.setResu_serie(rs.getString("resu_serie"));
                resumen.setResu_numero(rs.getString("resu_numero"));
                resumen.setResu_motivo(rs.getString("RESU_MOTIVO"));
                res.add(resumen);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //ConnectionPool.closeConexion(conn);
        }
        return res;
    }

    public static DocumentoBean pendienteDocElectronicoReversionesBaja(Connection conn) {
        DocumentoBean cabecera = null;

//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT * ";
            sql += " FROM resumendia_baja ";
            sql += " WHERE  resu_proce_status = 'N' ";
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

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return cabecera;
    }

}
