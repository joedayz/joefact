/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.despatchers;

import com.org.model.beans.DocumentoBean;
import com.org.model.beans.Leyenda;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author oswaldo
 */
public class DElectronicoDespachador {
    private static Log log = LogFactory.getLog(DElectronicoDespachador.class);

    public static DocumentoBean cargarDocElectronico(String pdocu_codigo, Connection conn) {
        DocumentoBean b = null;

//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT DOCU_CODIGO,";
            sql += " EMPR_RAZONSOCIAL,";
            sql += " EMPR_UBIGEO,";
            sql += " EMPR_NOMBRECOMERCIAL,";
            sql += " EMPR_DIRECCION,";
            sql += " EMPR_PROVINCIA,";
            sql += " EMPR_DEPARTAMENTO,";
            sql += " EMPR_DISTRITO,";
            sql += " EMPR_PAIS,";
            sql += " EMPR_NRORUC,";
            sql += " EMPR_TIPODOC,";
            sql += " CLIE_NUMERO,";
            sql += " CLIE_TIPODOC,";
            sql += " CLIE_NOMBRE,";
            sql += " DOCU_FECHA,";
            sql += " DOCU_TIPODOCUMENTO,";
            sql += " DOCU_NUMERO,";
            sql += " DOCU_MONEDA,";
            sql += " DOCU_GRAVADA  as  DOCU_GRAVADA,";
            sql += " DOCU_INAFECTA  as  DOCU_INAFECTA,";
            sql += " DOCU_EXONERADA  as  DOCU_EXONERADA,";
            sql += " DOCU_GRATUITA  as  DOCU_GRATUITA,";
            sql += " DOCU_DESCUENTO  as  DOCU_DESCUENTO,";
            sql += " DOCU_SUBTOTAL  as  DOCU_SUBTOTAL,";
            sql += " DOCU_TOTAL  as  DOCU_TOTAL,";
            sql += " DOCU_IGV  as  DOCU_IGV,";
            sql += " TASA_IGV,";
            sql += " DOCU_ISC,";
            sql += " TASA_ISC,";
            sql += " DOCU_OTROSTRIBUTOS  as  DOCU_OTROSTRIBUTOS,";
            sql += " TASA_OTROSTRIBUTOS,";

            sql += " RETE_REGI,"; // 01 TASA 3%
            sql += " RETE_TASA,"; // 3%           
            sql += " RETE_TOTAL_ELEC,"; //
            sql += " RETE_TOTAL_RETE,"; //

            sql += " DOCU_OTROSCARGOS  as  DOCU_OTROSCARGOS,";
            sql += " DOCU_PERCEPCION  as  DOCU_PERCEPCION,";
            sql += " NOTA_MOTIVO,";
            sql += " NOTA_SUSTENTO,";
            sql += " NOTA_TIPODOC,";
            sql += " NOTA_DOCUMENTO, ";
            sql += " docu_enviaws, ";
            sql += " idExterno, ";
            sql += " clie_correo_cpe1, ";
            sql += " clie_correo_cpe2, ";
            sql += " clie_correo_cpe0, ";
            // anticipos documenotos / 04 anticipos
            sql += " docu_tipo_operacion,  ";
            
            sql += " docu_anticipo_total ";
            
            sql += " FROM cabecera";
            sql += " WHERE  DOCU_CODIGO = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pdocu_codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                b = new DocumentoBean();
                b.setDocu_codigo(rs.getString("docu_codigo"));
                b.setEmpr_razonsocial(rs.getString("empr_razonsocial"));
                b.setEmpr_ubigeo(rs.getString("empr_ubigeo"));
                b.setEmpr_nombrecomercial(rs.getString("empr_nombrecomercial"));
                b.setEmpr_direccion(rs.getString("empr_direccion"));
                b.setEmpr_provincia(rs.getString("empr_provincia"));
                b.setEmpr_departamento(rs.getString("empr_departamento"));
                b.setEmpr_distrito(rs.getString("empr_distrito"));
                b.setEmpr_pais(rs.getString("empr_pais"));
                b.setEmpr_nroruc(rs.getString("empr_nroruc"));
                b.setEmpr_tipodoc(rs.getString("empr_tipodoc"));
                b.setClie_numero(rs.getString("clie_numero"));
                b.setClie_tipodoc(rs.getString("clie_tipodoc"));
                b.setClie_nombre(rs.getString("clie_nombre"));
                b.setDocu_fecha(rs.getString("docu_fecha"));
                b.setDocu_tipodocumento(rs.getString("docu_tipodocumento"));
                b.setDocu_numero(rs.getString("docu_numero"));
                b.setDocu_moneda(rs.getString("docu_moneda"));
                b.setDocu_gravada(rs.getString("docu_gravada"));
                b.setDocu_inafecta(rs.getString("docu_inafecta"));
                b.setDocu_exonerada(rs.getString("docu_exonerada"));
                b.setDocu_gratuita(rs.getString("docu_gratuita"));
                b.setDocu_descuento(rs.getString("docu_descuento"));
                b.setDocu_subtotal(rs.getString("docu_subtotal"));
                b.setDocu_total(rs.getString("docu_total"));
                b.setDocu_igv(rs.getString("docu_igv"));
                b.setTasa_igv(rs.getString("tasa_igv"));
                b.setDocu_isc(rs.getString("docu_isc"));
                b.setTasa_isc(rs.getString("tasa_isc"));
                b.setDocu_otrostributos(rs.getString("docu_otrostributos"));
                b.setTasa_otrostributos(rs.getString("tasa_otrostributos"));
                b.setDocu_otroscargos(rs.getString("docu_otroscargos"));
                b.setDocu_percepcion(rs.getString("docu_percepcion"));
                b.setNota_motivo(rs.getString("nota_motivo"));
                b.setNota_sustento(rs.getString("nota_sustento"));
                b.setNota_tipodoc(rs.getString("nota_tipodoc"));
                b.setNota_documento(rs.getString("nota_documento"));

                b.setRete_regi(rs.getString("rete_regi"));
                b.setRete_tasa(rs.getString("rete_tasa"));
                b.setRete_total_elec(rs.getString("rete_total_elec"));
                b.setRete_total_rete(rs.getString("rete_total_rete"));
                b.setDocu_enviaws(rs.getString("docu_enviaws"));
                b.setIdExterno(rs.getString("idExterno"));
                b.setClie_correo_cpe1(rs.getString("clie_correo_cpe1"));
                b.setClie_correo_cpe2(rs.getString("clie_correo_cpe2"));
                b.setClie_correo_cpe0(rs.getString("clie_correo_cpe0"));

                // anticipos
                b.setDocu_tipo_operacion(rs.getString("docu_tipo_operacion"));
                b.setDocu_anticipo_total(rs.getString("docu_anticipo_total"));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return b;
    }

    public static List<DocumentoBean> cargarDetDocElectronico(String pdocu_codigo, Connection conn) throws SQLException {
        List<DocumentoBean> detalle = new ArrayList<DocumentoBean>();
//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT  DOCU_CODIGO,";
            sql += " DOCU_MONEDA,";
            sql += " ITEM_MONEDA,";
            sql += " ITEM_ORDEN,";
            sql += " ITEM_UNIDAD,";
            sql += " ITEM_CANTIDAD,";
            sql += " ITEM_CODPRODUCTO,";
            sql += " ITEM_DESCRIPCION,";
            sql += " ITEM_AFECTACION,";
            sql += " ITEM_PVENTA, ";
            sql += " item_pventa_nohonerosa,";
            sql += " ITEM_TI_SUBTOTAL,";
            sql += " ITEM_TI_IGV, ";
            // retenciones
            sql += " rete_rela_tipo_docu, ";
            sql += " rete_rela_nume_docu, ";
            sql += " rete_rela_fech_docu, ";
            sql += " rete_rela_tipo_moneda, ";
            sql += " rete_rela_total_original, ";
            sql += " rete_rela_fecha_pago, ";
            sql += " rete_rela_numero_pago, ";
            sql += " rete_rela_importe_pagado_original, ";
            sql += " rete_rela_tipo_moneda_pago, ";
            sql += " rete_importe_retenido_nacional, ";
            sql += " rete_importe_neto_nacional, ";
            sql += " rete_tipo_moneda_referencia, ";
            sql += " rete_tipo_moneda_objetivo, ";
            sql += " rete_tipo_moneda_tipo_cambio, ";
            sql += " rete_tipo_moneda_fecha ";
            // Retenciones
            sql += " FROM detalle";
            sql += " WHERE DOCU_CODIGO = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pdocu_codigo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DocumentoBean cdetalle = new DocumentoBean();
                cdetalle.setDocu_codigo(rs.getString("docu_codigo"));
                cdetalle.setDocu_moneda(rs.getString("docu_moneda"));
                cdetalle.setItem_moneda(rs.getString("item_moneda"));
                cdetalle.setItem_orden(rs.getString("item_orden"));
                cdetalle.setItem_unidad(rs.getString("item_unidad"));
                cdetalle.setItem_cantidad(rs.getString("item_cantidad"));
                cdetalle.setItem_codproducto(rs.getString("item_codproducto"));
                cdetalle.setItem_descripcion(rs.getString("item_descripcion"));
                cdetalle.setItem_afectacion(rs.getString("item_afectacion"));
                cdetalle.setItem_pventa(rs.getString("item_pventa"));
                cdetalle.setItem_pventa_nohonerosa(rs.getString("item_pventa_nohonerosa"));
                cdetalle.setItem_ti_subtotal(rs.getString("item_ti_subtotal"));
                cdetalle.setItem_ti_igv(rs.getString("item_ti_igv"));

                // retenciones
                cdetalle.setRete_rela_tipo_docu(rs.getString("rete_rela_tipo_docu"));
                cdetalle.setRete_rela_nume_docu(rs.getString("rete_rela_nume_docu"));
                cdetalle.setRete_rela_fech_docu(rs.getString("rete_rela_fech_docu"));
                cdetalle.setRete_rela_tipo_moneda(rs.getString("rete_rela_tipo_moneda"));
                cdetalle.setRete_rela_total_original(rs.getString("rete_rela_total_original"));
                cdetalle.setRete_rela_fecha_pago(rs.getString("rete_rela_fecha_pago"));
                cdetalle.setRete_rela_numero_pago(rs.getString("rete_rela_numero_pago"));
                cdetalle.setRete_rela_importe_pagado_original(rs.getString("rete_rela_importe_pagado_original"));
                cdetalle.setRete_rela_tipo_moneda_pago(rs.getString("rete_rela_tipo_moneda_pago"));
                cdetalle.setRete_importe_retenido_nacional(rs.getString("rete_importe_retenido_nacional"));
                cdetalle.setRete_importe_neto_nacional(rs.getString("rete_importe_neto_nacional"));
                cdetalle.setRete_tipo_moneda_referencia(rs.getString("rete_tipo_moneda_referencia"));
                cdetalle.setRete_tipo_moneda_objetivo(rs.getString("rete_tipo_moneda_objetivo"));
                cdetalle.setRete_tipo_moneda_tipo_cambio(rs.getString("rete_tipo_moneda_tipo_cambio"));
                cdetalle.setRete_tipo_moneda_fecha(rs.getString("rete_tipo_moneda_fecha"));
                // Retenciones

                detalle.add(cdetalle);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return detalle;
    }

    public static List<DocumentoBean> cargarDetDocElectronicoAnticipo(String pdocu_codigo, Connection conn) throws SQLException {
        List<DocumentoBean> detalle = new ArrayList<DocumentoBean>();

        try {

            String sql = "SELECT  docu_codigo, "
                    + "docu_anticipo_prepago, "
                    + "docu_anticipo_docu_tipo, "
                    + "docu_anticipo_docu_numero, "
                    + "docu_anticipo_tipo_docu_emi, "
                    + "docu_anticipo_numero_docu_emi ";
            // Anticipos
            sql += " FROM anticipos ";
            sql += " WHERE DOCU_CODIGO = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pdocu_codigo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DocumentoBean cdetalle = new DocumentoBean();
                cdetalle.setDocu_codigo(rs.getString("docu_codigo"));
                cdetalle.setDocu_anticipo_prepago(rs.getString("docu_anticipo_prepago"));
                cdetalle.setDocu_anticipo_docu_tipo(rs.getString("docu_anticipo_docu_tipo"));
                cdetalle.setDocu_anticipo_docu_numero(rs.getString("docu_anticipo_docu_numero"));
                cdetalle.setDocu_anticipo_tipo_docu_emi(rs.getString("docu_anticipo_tipo_docu_emi"));
                cdetalle.setDocu_anticipo_numero_docu_emi(rs.getString("docu_anticipo_numero_docu_emi"));
                // Anticipos

                detalle.add(cdetalle);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        } finally {
        }
        return detalle;
    }

    public static List<Leyenda> cargarDetDocElectronicoLeyenda(String pdocu_codigo, Connection conn) throws SQLException {
        List<Leyenda> detalle = new ArrayList<Leyenda>();

        try {

            String sql = "SELECT  leyenda_codigo, "
                    + "leyenda_texto ";
            // Anticipos
            sql += " FROM leyenda ";
            sql += " WHERE DOCU_CODIGO = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pdocu_codigo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Leyenda leyenda = new Leyenda();
                leyenda.setLeyendaCodigo(rs.getString("leyenda_codigo"));
                leyenda.setLeyendaTexto(rs.getString("leyenda_texto"));
                // Anticipos

                detalle.add(leyenda);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        } finally {
        }
        return detalle;
    }

    public static DocumentoBean pendienteDocElectronico(Connection conn) {
        DocumentoBean b = null;

//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT * ";
            sql += " FROM facturaelectronica.cabecera";
            sql += " WHERE  docu_proce_status = 'N' ";
            sql += " order by docu_codigo LIMIT 1 ";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                b = new DocumentoBean();
                b.setDocu_codigo(rs.getString("docu_codigo"));
                b.setEmpr_razonsocial(rs.getString("empr_razonsocial"));
                b.setEmpr_ubigeo(rs.getString("empr_ubigeo"));
                b.setEmpr_nombrecomercial(rs.getString("empr_nombrecomercial"));
                b.setEmpr_direccion(rs.getString("empr_direccion"));
                b.setEmpr_provincia(rs.getString("empr_provincia"));
                b.setEmpr_departamento(rs.getString("empr_departamento"));
                b.setEmpr_distrito(rs.getString("empr_distrito"));
                b.setEmpr_pais(rs.getString("empr_pais"));
                b.setEmpr_nroruc(rs.getString("empr_nroruc"));
                b.setEmpr_tipodoc(rs.getString("empr_tipodoc"));
                b.setClie_numero(rs.getString("clie_numero"));
                b.setClie_tipodoc(rs.getString("clie_tipodoc"));
                b.setClie_nombre(rs.getString("clie_nombre"));
                b.setDocu_fecha(rs.getString("docu_fecha"));
                b.setDocu_tipodocumento(rs.getString("docu_tipodocumento"));
                b.setDocu_numero(rs.getString("docu_numero"));
                b.setDocu_moneda(rs.getString("docu_moneda"));
                b.setDocu_gravada(rs.getString("docu_gravada"));
                b.setDocu_inafecta(rs.getString("docu_inafecta"));
                b.setDocu_exonerada(rs.getString("docu_exonerada"));
                b.setDocu_gratuita(rs.getString("docu_gratuita"));
                b.setDocu_descuento(rs.getString("docu_descuento"));
                b.setDocu_subtotal(rs.getString("docu_subtotal"));
                b.setDocu_total(rs.getString("docu_total"));
                b.setDocu_igv(rs.getString("docu_igv"));
                b.setTasa_igv(rs.getString("tasa_igv"));
                b.setDocu_isc(rs.getString("docu_isc"));
                b.setTasa_isc(rs.getString("tasa_isc"));
                b.setDocu_otrostributos(rs.getString("docu_otrostributos"));
                b.setTasa_otrostributos(rs.getString("tasa_otrostributos"));
                b.setDocu_otroscargos(rs.getString("docu_otroscargos"));
                b.setDocu_percepcion(rs.getString("docu_percepcion"));
                b.setNota_motivo(rs.getString("nota_motivo"));
                b.setNota_sustento(rs.getString("nota_sustento"));
                b.setNota_tipodoc(rs.getString("nota_tipodoc"));
                b.setNota_documento(rs.getString("nota_documento"));

                b.setRete_regi(rs.getString("rete_regi"));
                b.setRete_tasa(rs.getString("rete_tasa"));
                b.setRete_total_elec(rs.getString("rete_total_elec"));
                b.setRete_total_rete(rs.getString("rete_total_rete"));
                b.setDocu_enviaws(rs.getString("docu_enviaws"));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return b;
    }

    public static DocumentoBean noPendienteDocElectronico(Connection conn) {
        DocumentoBean b = null;

//        Connection conn = null;
        try {
//            conn = ConnectionPool.obtenerConexionMysql();
            String sql = "SELECT * ";
            sql += " FROM cabecera";
            sql += " WHERE  docu_proce_status in ('B','P','E','X') and docu_proce_fecha <=  DATE_SUB(NOW(), INTERVAL 10 MINUTE)";
            sql += " order by docu_codigo LIMIT 1 ";

            PreparedStatement ps = conn.prepareStatement(sql);
            //ps.setString(1, proceso);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                b = new DocumentoBean();
                b.setDocu_codigo(rs.getString("docu_codigo"));
                b.setEmpr_razonsocial(rs.getString("empr_razonsocial"));
                b.setEmpr_ubigeo(rs.getString("empr_ubigeo"));
                b.setEmpr_nombrecomercial(rs.getString("empr_nombrecomercial"));
                b.setEmpr_direccion(rs.getString("empr_direccion"));
                b.setEmpr_provincia(rs.getString("empr_provincia"));
                b.setEmpr_departamento(rs.getString("empr_departamento"));
                b.setEmpr_distrito(rs.getString("empr_distrito"));
                b.setEmpr_pais(rs.getString("empr_pais"));
                b.setEmpr_nroruc(rs.getString("empr_nroruc"));
                b.setEmpr_tipodoc(rs.getString("empr_tipodoc"));
                b.setClie_numero(rs.getString("clie_numero"));
                b.setClie_tipodoc(rs.getString("clie_tipodoc"));
                b.setClie_nombre(rs.getString("clie_nombre"));
                b.setDocu_fecha(rs.getString("docu_fecha"));
                b.setDocu_tipodocumento(rs.getString("docu_tipodocumento"));
                b.setDocu_numero(rs.getString("docu_numero"));
                b.setDocu_moneda(rs.getString("docu_moneda"));
                b.setDocu_gravada(rs.getString("docu_gravada"));
                b.setDocu_inafecta(rs.getString("docu_inafecta"));
                b.setDocu_exonerada(rs.getString("docu_exonerada"));
                b.setDocu_gratuita(rs.getString("docu_gratuita"));
                b.setDocu_descuento(rs.getString("docu_descuento"));
                b.setDocu_subtotal(rs.getString("docu_subtotal"));
                b.setDocu_total(rs.getString("docu_total"));
                b.setDocu_igv(rs.getString("docu_igv"));
                b.setTasa_igv(rs.getString("tasa_igv"));
                b.setDocu_isc(rs.getString("docu_isc"));
                b.setTasa_isc(rs.getString("tasa_isc"));
                b.setDocu_otrostributos(rs.getString("docu_otrostributos"));
                b.setTasa_otrostributos(rs.getString("tasa_otrostributos"));
                b.setDocu_otroscargos(rs.getString("docu_otroscargos"));
                b.setDocu_percepcion(rs.getString("docu_percepcion"));
                b.setNota_motivo(rs.getString("nota_motivo"));
                b.setNota_sustento(rs.getString("nota_sustento"));
                b.setNota_tipodoc(rs.getString("nota_tipodoc"));
                b.setNota_documento(rs.getString("nota_documento"));

                b.setRete_regi(rs.getString("rete_regi"));
                b.setRete_tasa(rs.getString("rete_tasa"));
                b.setRete_total_elec(rs.getString("rete_total_elec"));
                b.setRete_total_rete(rs.getString("rete_total_rete"));
                b.setDocu_enviaws(rs.getString("docu_enviaws"));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return b;
    }
}
