/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.beans;

import java.util.List;

/**
 *
 * @author oswaldo
 */
public class DocumentoBean {

    private String docu_codigo;
    private String empr_razonsocial;
    private String empr_ubigeo;
    private String empr_nombrecomercial;
    private String empr_direccion;
    private String empr_provincia;
    private String empr_departamento;
    private String empr_distrito;
    private String empr_pais;
    private String empr_nroruc;
    private String empr_tipodoc;
    private String clie_numero;
    private String clie_tipodoc;
    private String clie_nombre;
    private String docu_fecha;
    private String docu_tipodocumento;
    private String docu_numero;
    private String docu_moneda;
    private String docu_gravada;
    private String docu_inafecta;
    private String docu_exonerada;
    private String docu_gratuita;
    private String docu_descuento;
    private String docu_subtotal;
    private String docu_total;
    private String docu_igv;
    private String tasa_igv;
    private String docu_isc;
    private String tasa_isc;

    private String docu_otrostributos;
    private String docu_otroscargos;
    private String docu_percepcion;
    private String docu_enviaws;
    /**
     * *variable de detalle*****
     */
    private String iddetalle;
    private String item_moneda;
    private String item_orden;
    private String item_unidad;
    private String item_cantidad;
    private String item_codproducto;
    private String item_descripcion;
    private String item_afectacion;
    private String item_pventa;
    private String item_pventa_nohonerosa;
    private String item_ti_subtotal;
    private String item_ti_igv;
    /**
     * *variable de detalle retenciones*****
     */
    private String rete_rela_tipo_docu;
    private String rete_rela_nume_docu;
    private String rete_rela_fech_docu;
    private String rete_rela_tipo_moneda;
    private String rete_rela_total_original;
    private String rete_rela_fecha_pago;
    private String rete_rela_numero_pago;
    private String rete_rela_importe_pagado_original;
    private String rete_rela_tipo_moneda_pago;
    private String rete_importe_retenido_nacional;
    private String rete_importe_neto_nacional;
    private String rete_tipo_moneda_referencia;
    private String rete_tipo_moneda_objetivo;
    private String rete_tipo_moneda_tipo_cambio;
    private String rete_tipo_moneda_fecha;
    // adicionales de detalle 
    private String item_otros;
    /**
     * variable de resumen**
     */
    private String resu_codigo;
    private String resu_fecha;
    private String resu_fila;
    private String resu_tipodoc;
    private String resu_serie;
    private String resu_inicio;
    private String resu_final;
    private String resu_gravada;
    private String resu_exonerada;
    private String resu_inafecta;
    private String resu_otcargos;
    private String resu_isc;
    private String resu_igv;
    private String resu_ottributos;
    private String resu_total;
    private String resu_identificador;
    private String resu_fechagenera;
    private String resu_fec;
    private String resu_numero;
    private String resu_motivo;
    private String resu_tipo;
    private String resu_version;
    private String item_estado;

    //nuevos campos
    private String tasa_otrostributos;
    private String nota_motivo;
    private String nota_sustento;
    private String nota_tipodoc;
    private String nota_documento;
    private String cdesu_nroticket;

    // Retenciones cabecera
    private String rete_regi;
    private String rete_tasa;
    private String rete_total_elec;
    private String rete_total_rete;

    // Adicionales cabecera // facturas
    private String docu_forma_pago;
    private String docu_observacion;
    private String clie_direccion;
    private String docu_vendedor;
    private String docu_pedido;
    private String docu_guia_remision;
    private String clie_orden_compra;
    // Adiocionales en 
    private String clie_correo_cpe1;
    private String clie_correo_cpe2;
    private String clie_correo_cpe0;
    //Leyendas cabecera
    private String docu_leyenda_a;
    private String docu_leyenda_b;
    private String docu_leyenda_c;
    private String docu_leyenda_d;
    private String docu_leyenda_e;
    private String docu_leyenda_f;
    

    // control id de app externo
    private String idExterno;

    // Anticipos
    private String docu_tipo_operacion;
    private String docu_anticipo_prepago;
    private String docu_anticipo_docu_tipo;
    private String docu_anticipo_docu_numero;
    private String docu_anticipo_tipo_docu_emi;
    private String docu_anticipo_numero_docu_emi;
    private String docu_anticipo_total;
    
    // Otros detalle es un arreglo
//    List<OtrosDetalles> otrosDetalles;
    public String getCdesu_nroticket() {
        return cdesu_nroticket;
    }

    public void setCdesu_nroticket(String cdesu_nroticket) {
        this.cdesu_nroticket = cdesu_nroticket;
    }

    public String getDocu_enviaws() {
        return docu_enviaws;
    }

    public void setDocu_enviaws(String docu_enviaws) {
        this.docu_enviaws = docu_enviaws;
    }

    public String getResu_motivo() {
        return resu_motivo;
    }

    public void setResu_motivo(String resu_motivo) {
        this.resu_motivo = resu_motivo;
    }

    public String getResu_numero() {
        return resu_numero;
    }

    public void setResu_numero(String resu_numero) {
        this.resu_numero = resu_numero;
    }

    public String getDocu_codigo() {
        return docu_codigo;
    }

    /**
     * @param docu_codigo the docu_codigo to set
     */
    public void setDocu_codigo(String docu_codigo) {
        this.docu_codigo = docu_codigo;
    }

    /**
     * @return the empr_razonsocial
     */
    public String getEmpr_razonsocial() {
        return empr_razonsocial;
    }

    /**
     * @param empr_razonsocial the empr_razonsocial to set
     */
    public void setEmpr_razonsocial(String empr_razonsocial) {
        this.empr_razonsocial = empr_razonsocial;
    }

    /**
     * @return the empr_ubigeo
     */
    public String getEmpr_ubigeo() {
        return empr_ubigeo;
    }

    /**
     * @param empr_ubigeo the empr_ubigeo to set
     */
    public void setEmpr_ubigeo(String empr_ubigeo) {
        this.empr_ubigeo = empr_ubigeo;
    }

    /**
     * @return the empr_nombrecomercial
     */
    public String getEmpr_nombrecomercial() {
        return empr_nombrecomercial;
    }

    /**
     * @param empr_nombrecomercial the empr_nombrecomercial to set
     */
    public void setEmpr_nombrecomercial(String empr_nombrecomercial) {
        this.empr_nombrecomercial = empr_nombrecomercial;
    }

    /**
     * @return the empr_direccion
     */
    public String getEmpr_direccion() {
        return empr_direccion;
    }

    /**
     * @param empr_direccion the empr_direccion to set
     */
    public void setEmpr_direccion(String empr_direccion) {
        this.empr_direccion = empr_direccion == null ? "" : empr_direccion.trim();
    }

    /**
     * @return the empr_provincia
     */
    public String getEmpr_provincia() {
        return empr_provincia;
    }

    /**
     * @param empr_provincia the empr_provincia to set
     */
    public void setEmpr_provincia(String empr_provincia) {
        this.empr_provincia = empr_provincia;
    }

    /**
     * @return the empr_departamento
     */
    public String getEmpr_departamento() {
        return empr_departamento;
    }

    /**
     * @param empr_departamento the empr_departamento to set
     */
    public void setEmpr_departamento(String empr_departamento) {
        this.empr_departamento = empr_departamento;
    }

    /**
     * @return the empr_distrito
     */
    public String getEmpr_distrito() {
        return empr_distrito;
    }

    /**
     * @param empr_distrito the empr_distrito to set
     */
    public void setEmpr_distrito(String empr_distrito) {
        this.empr_distrito = empr_distrito;
    }

    /**
     * @return the empr_pais
     */
    public String getEmpr_pais() {
        return empr_pais;
    }

    /**
     * @param empr_pais the empr_pais to set
     */
    public void setEmpr_pais(String empr_pais) {
        this.empr_pais = empr_pais;
    }

    /**
     * @return the empr_nroruc
     */
    public String getEmpr_nroruc() {
        return empr_nroruc;
    }

    /**
     * @param empr_nroruc the empr_nroruc to set
     */
    public void setEmpr_nroruc(String empr_nroruc) {
        this.empr_nroruc = empr_nroruc;
    }

    /**
     * @return the empr_tipodoc
     */
    public String getEmpr_tipodoc() {
        return empr_tipodoc;
    }

    /**
     * @param empr_tipodoc the empr_tipodoc to set
     */
    public void setEmpr_tipodoc(String empr_tipodoc) {
        this.empr_tipodoc = empr_tipodoc;
    }

    /**
     * @return the clie_numero
     */
    public String getClie_numero() {
        return clie_numero;
    }

    /**
     * @param clie_numero the clie_numero to set
     */
    public void setClie_numero(String clie_numero) {
        this.clie_numero = clie_numero;
    }

    /**
     * @return the clie_tipodoc
     */
    public String getClie_tipodoc() {
        return clie_tipodoc;
    }

    /**
     * @param clie_tipodoc the clie_tipodoc to set
     */
    public void setClie_tipodoc(String clie_tipodoc) {
        this.clie_tipodoc = clie_tipodoc;
    }

    /**
     * @return the clie_nombre
     */
    public String getClie_nombre() {
        return clie_nombre;
    }

    /**
     * @param clie_nombre the clie_nombre to set
     */
    public void setClie_nombre(String clie_nombre) {
        this.clie_nombre = clie_nombre;
    }

    /**
     * @return the docu_fecha
     */
    public String getDocu_fecha() {
        return docu_fecha;
    }

    /**
     * @param docu_fecha the docu_fecha to set
     */
    public void setDocu_fecha(String docu_fecha) {
        this.docu_fecha = docu_fecha;
    }

    /**
     * @return the docu_tipodocumento
     */
    public String getDocu_tipodocumento() {
        return docu_tipodocumento;
    }

    /**
     * @param docu_tipodocumento the docu_tipodocumento to set
     */
    public void setDocu_tipodocumento(String docu_tipodocumento) {
        this.docu_tipodocumento = docu_tipodocumento;
    }

    /**
     * @return the docu_numero
     */
    public String getDocu_numero() {
        return docu_numero;
    }

    /**
     * @param docu_numero the docu_numero to set
     */
    public void setDocu_numero(String docu_numero) {
        this.docu_numero = docu_numero;
    }

    /**
     * @return the docu_moneda
     */
    public String getDocu_moneda() {
        return docu_moneda;
    }

    /**
     * @param docu_moneda the docu_moneda to set
     */
    public void setDocu_moneda(String docu_moneda) {
        this.docu_moneda = docu_moneda == null ? "" : docu_moneda.trim();
    }

    /**
     * @return the docu_gravada
     */
    public String getDocu_gravada() {
        return docu_gravada;
    }

    /**
     * @param docu_gravada the docu_gravada to set
     */
    public void setDocu_gravada(String docu_gravada) {
        this.docu_gravada = docu_gravada;
    }

    /**
     * @return the docu_inafecta
     */
    public String getDocu_inafecta() {
        return docu_inafecta;
    }

    /**
     * @param docu_inafecta the docu_inafecta to set
     */
    public void setDocu_inafecta(String docu_inafecta) {
        this.docu_inafecta = docu_inafecta;
    }

    /**
     * @return the docu_exonerada
     */
    public String getDocu_exonerada() {
        return docu_exonerada;
    }

    /**
     * @param docu_exonerada the docu_exonerada to set
     */
    public void setDocu_exonerada(String docu_exonerada) {
        this.docu_exonerada = docu_exonerada;
    }

    /**
     * @return the docu_gratuita
     */
    public String getDocu_gratuita() {
        return docu_gratuita;
    }

    /**
     * @param docu_gratuita the docu_gratuita to set
     */
    public void setDocu_gratuita(String docu_gratuita) {
        this.docu_gratuita = docu_gratuita;
    }

    /**
     * @return the docu_descuento
     */
    public String getDocu_descuento() {
        return docu_descuento;
    }

    /**
     * @param docu_descuento the docu_descuento to set
     */
    public void setDocu_descuento(String docu_descuento) {
        this.docu_descuento = docu_descuento;
    }

    /**
     * @return the docu_subtotal
     */
    public String getDocu_subtotal() {
        return docu_subtotal;
    }

    /**
     * @param docu_subtotal the docu_subtotal to set
     */
    public void setDocu_subtotal(String docu_subtotal) {
        this.docu_subtotal = docu_subtotal;
    }

    /**
     * @return the docu_total
     */
    public String getDocu_total() {
        return docu_total;
    }

    /**
     * @param docu_total the docu_total to set
     */
    public void setDocu_total(String docu_total) {
        this.docu_total = docu_total;
    }

    /**
     * @return the docu_igv
     */
    public String getDocu_igv() {
        return docu_igv;
    }

    /**
     * @param docu_igv the docu_igv to set
     */
    public void setDocu_igv(String docu_igv) {
        this.docu_igv = docu_igv;
    }

    /**
     * @return the tasa_igv
     */
    public String getTasa_igv() {
        return tasa_igv;
    }

    /**
     * @param tasa_igv the tasa_igv to set
     */
    public void setTasa_igv(String tasa_igv) {
        this.tasa_igv = tasa_igv;
    }

    /**
     * @return the docu_isc
     */
    public String getDocu_isc() {
        return docu_isc;
    }

    /**
     * @param docu_isc the docu_isc to set
     */
    public void setDocu_isc(String docu_isc) {
        this.docu_isc = docu_isc;
    }

    /**
     * @return the tasa_isc
     */
    public String getTasa_isc() {
        return tasa_isc;
    }

    /**
     * @param tasa_isc the tasa_isc to set
     */
    public void setTasa_isc(String tasa_isc) {
        this.tasa_isc = tasa_isc;
    }

    /**
     * @return the docu_otrostributos
     */
    public String getDocu_otrostributos() {
        return docu_otrostributos;
    }

    /**
     * @param docu_otrostributos the docu_otrostributos to set
     */
    public void setDocu_otrostributos(String docu_otrostributos) {
        this.docu_otrostributos = docu_otrostributos;
    }

    /**
     * @return the docu_otroscargos
     */
    public String getDocu_otroscargos() {
        return docu_otroscargos;
    }

    /**
     * @param docu_otroscargos the docu_otroscargos to set
     */
    public void setDocu_otroscargos(String docu_otroscargos) {
        this.docu_otroscargos = docu_otroscargos;
    }

    /**
     * @return the docu_percepcion
     */
    public String getDocu_percepcion() {
        return docu_percepcion;
    }

    /**
     * @param docu_percepcion the docu_percepcion to set
     */
    public void setDocu_percepcion(String docu_percepcion) {
        this.docu_percepcion = docu_percepcion;
    }

    /**
     * @return the iddetalle
     */
    public String getIddetalle() {
        return iddetalle;
    }

    public void setIddetalle(String iddetalle) {
        this.iddetalle = iddetalle;
    }

    /**
     * @return the item_moneda
     */
    public String getItem_moneda() {
        return item_moneda;
    }

    /**
     * @param item_moneda the item_moneda to set
     */
    public void setItem_moneda(String item_moneda) {
        this.item_moneda = item_moneda;
    }

    /**
     * @return the item_orden
     */
    public String getItem_orden() {
        return item_orden;
    }

    /**
     * @param item_orden the item_orden to set
     */
    public void setItem_orden(String item_orden) {
        this.item_orden = item_orden;
    }

    /**
     * @return the item_unidad
     */
    public String getItem_unidad() {
        return item_unidad;
    }

    /**
     * @param item_unidad the item_unidad to set
     */
    public void setItem_unidad(String item_unidad) {
        this.item_unidad = item_unidad;
    }

    /**
     * @return the item_cantidad
     */
    public String getItem_cantidad() {
        return item_cantidad;
    }

    /**
     * @param item_cantidad the item_cantidad to set
     */
    public void setItem_cantidad(String item_cantidad) {
        this.item_cantidad = item_cantidad;
    }

    /**
     * @return the item_codproducto
     */
    public String getItem_codproducto() {
        return item_codproducto;
    }

    /**
     * @param item_codproducto the item_codproducto to set
     */
    public void setItem_codproducto(String item_codproducto) {
        this.item_codproducto = item_codproducto;
    }

    /**
     * @return the item_descripcion
     */
    public String getItem_descripcion() {
        return item_descripcion;
    }

    /**
     * @param item_descripcion the item_descripcion to set
     */
    public void setItem_descripcion(String item_descripcion) {
        this.item_descripcion = item_descripcion;
    }

    /**
     * @return the item_afectacion
     */
    public String getItem_afectacion() {
        return item_afectacion;
    }

    /**
     * @param item_afectacion the item_afectacion to set
     */
    public void setItem_afectacion(String item_afectacion) {
        this.item_afectacion = item_afectacion;
    }

    /**
     * @return the item_pventa
     */
    public String getItem_pventa() {
        return item_pventa;
    }

    /**
     * @param item_pventa the item_pventa to set
     */
    public void setItem_pventa(String item_pventa) {
        this.item_pventa = item_pventa;
    }

    public String getItem_pventa_nohonerosa() {
        return item_pventa_nohonerosa;
    }

    public void setItem_pventa_nohonerosa(String item_pventa_nohonerosa) {
        this.item_pventa_nohonerosa = item_pventa_nohonerosa;
    }
    
    /**
     * @return the item_ti_subtotal
     */
    public String getItem_ti_subtotal() {
        return item_ti_subtotal;
    }

    /**
     * @param item_ti_subtotal the item_ti_subtotal to set
     */
    public void setItem_ti_subtotal(String item_ti_subtotal) {
        this.item_ti_subtotal = item_ti_subtotal;
    }

    /**
     * @return the item_ti_igv
     */
    public String getItem_ti_igv() {
        return item_ti_igv;
    }

    /**
     * @param item_ti_igv the item_ti_igv to set
     */
    public void setItem_ti_igv(String item_ti_igv) {
        this.item_ti_igv = item_ti_igv;
    }

    public String getRete_rela_tipo_docu() {
        return rete_rela_tipo_docu;
    }

    public void setRete_rela_tipo_docu(String rete_rela_tipo_docu) {
        this.rete_rela_tipo_docu = rete_rela_tipo_docu;
    }

    public String getRete_rela_nume_docu() {
        return rete_rela_nume_docu;
    }

    public void setRete_rela_nume_docu(String rete_rela_nume_docu) {
        this.rete_rela_nume_docu = rete_rela_nume_docu;
    }

    public String getRete_rela_fech_docu() {
        return rete_rela_fech_docu;
    }

    public void setRete_rela_fech_docu(String rete_rela_fech_docu) {
        this.rete_rela_fech_docu = rete_rela_fech_docu;
    }

    public String getRete_rela_tipo_moneda() {
        return rete_rela_tipo_moneda;
    }

    public void setRete_rela_tipo_moneda(String rete_rela_tipo_moneda) {
        this.rete_rela_tipo_moneda = rete_rela_tipo_moneda;
    }

    public String getRete_rela_total_original() {
        return rete_rela_total_original;
    }

    public void setRete_rela_total_original(String rete_rela_total_original) {
        this.rete_rela_total_original = rete_rela_total_original;
    }

    public String getRete_rela_fecha_pago() {
        return rete_rela_fecha_pago;
    }

    public void setRete_rela_fecha_pago(String rete_rela_fecha_pago) {
        this.rete_rela_fecha_pago = rete_rela_fecha_pago;
    }

    public String getRete_rela_numero_pago() {
        return rete_rela_numero_pago;
    }

    public void setRete_rela_numero_pago(String rete_rela_numero_pago) {
        this.rete_rela_numero_pago = rete_rela_numero_pago;
    }

    public String getRete_rela_importe_pagado_original() {
        return rete_rela_importe_pagado_original;
    }

    public void setRete_rela_importe_pagado_original(String rete_rela_importe_pagado_original) {
        this.rete_rela_importe_pagado_original = rete_rela_importe_pagado_original;
    }

    public String getRete_rela_tipo_moneda_pago() {
        return rete_rela_tipo_moneda_pago;
    }

    public void setRete_rela_tipo_moneda_pago(String rete_rela_tipo_moneda_pago) {
        this.rete_rela_tipo_moneda_pago = rete_rela_tipo_moneda_pago;
    }

    public String getRete_importe_retenido_nacional() {
        return rete_importe_retenido_nacional;
    }

    public void setRete_importe_retenido_nacional(String rete_importe_retenido_nacional) {
        this.rete_importe_retenido_nacional = rete_importe_retenido_nacional;
    }

    public String getRete_importe_neto_nacional() {
        return rete_importe_neto_nacional;
    }

    public void setRete_importe_neto_nacional(String rete_importe_neto_nacional) {
        this.rete_importe_neto_nacional = rete_importe_neto_nacional;
    }

    public String getRete_tipo_moneda_referencia() {
        return rete_tipo_moneda_referencia;
    }

    public void setRete_tipo_moneda_referencia(String rete_tipo_moneda_referencia) {
        this.rete_tipo_moneda_referencia = rete_tipo_moneda_referencia;
    }

    public String getResu_tipo() {
        return resu_tipo;
    }

    public void setResu_tipo(String resu_tipo) {
        this.resu_tipo = resu_tipo;
    }

    public String getResu_version() {
        return resu_version;
    }

    public void setResu_version(String resu_version) {
        this.resu_version = resu_version;
    }

    public String getItem_estado() {
        return item_estado;
    }

    public void setItem_estado(String item_estado) {
        this.item_estado = item_estado;
    }

    public String getResu_codigo() {
        return resu_codigo;
    }

    public void setResu_codigo(String resu_codigo) {
        this.resu_codigo = resu_codigo;
    }

    /**
     * @return the resu_fecha
     */
    public String getResu_fecha() {
        return resu_fecha;
    }

    /**
     * @param resu_fecha the resu_fecha to set
     */
    public void setResu_fecha(String resu_fecha) {
        this.resu_fecha = resu_fecha;
    }

    /**
     * @return the resu_fila
     */
    public String getResu_fila() {
        return resu_fila;
    }

    /**
     * @param resu_fila the resu_fila to set
     */
    public void setResu_fila(String resu_fila) {
        this.resu_fila = resu_fila;
    }

    /**
     * @return the resu_tipodoc
     */
    public String getResu_tipodoc() {
        return resu_tipodoc;
    }

    /**
     * @param resu_tipodoc the resu_tipodoc to set
     */
    public void setResu_tipodoc(String resu_tipodoc) {
        this.resu_tipodoc = resu_tipodoc;
    }

    /**
     * @return the resu_serie
     */
    public String getResu_serie() {
        return resu_serie;
    }

    /**
     * @param resu_serie the resu_serie to set
     */
    public void setResu_serie(String resu_serie) {
        this.resu_serie = resu_serie;
    }

    /**
     * @return the resu_inicio
     */
    public String getResu_inicio() {
        return resu_inicio;
    }

    /**
     * @param resu_inicio the resu_inicio to set
     */
    public void setResu_inicio(String resu_inicio) {
        this.resu_inicio = resu_inicio;
    }

    /**
     * @return the resu_final
     */
    public String getResu_final() {
        return resu_final;
    }

    /**
     * @param resu_final the resu_final to set
     */
    public void setResu_final(String resu_final) {
        this.resu_final = resu_final;
    }

    /**
     * @return the resu_gravada
     */
    public String getResu_gravada() {
        return resu_gravada;
    }

    /**
     * @param resu_gravada the resu_gravada to set
     */
    public void setResu_gravada(String resu_gravada) {
        this.resu_gravada = resu_gravada;
    }

    /**
     * @return the resu_exonerada
     */
    public String getResu_exonerada() {
        return resu_exonerada;
    }

    /**
     * @param resu_exonerada the resu_exonerada to set
     */
    public void setResu_exonerada(String resu_exonerada) {
        this.resu_exonerada = resu_exonerada;
    }

    /**
     * @return the resu_inafecta
     */
    public String getResu_inafecta() {
        return resu_inafecta;
    }

    /**
     * @param resu_inafecta the resu_inafecta to set
     */
    public void setResu_inafecta(String resu_inafecta) {
        this.resu_inafecta = resu_inafecta;
    }

    /**
     * @return the resu_otcargos
     */
    public String getResu_otcargos() {
        return resu_otcargos;
    }

    /**
     * @param resu_otcargos the resu_otcargos to set
     */
    public void setResu_otcargos(String resu_otcargos) {
        this.resu_otcargos = resu_otcargos;
    }

    /**
     * @return the resu_isc
     */
    public String getResu_isc() {
        return resu_isc;
    }

    /**
     * @param resu_isc the resu_isc to set
     */
    public void setResu_isc(String resu_isc) {
        this.resu_isc = resu_isc;
    }

    /**
     * @return the resu_igv
     */
    public String getResu_igv() {
        return resu_igv;
    }

    /**
     * @param resu_igv the resu_igv to set
     */
    public void setResu_igv(String resu_igv) {
        this.resu_igv = resu_igv;
    }

    /**
     * @return the resu_ottributos
     */
    public String getResu_ottributos() {
        return resu_ottributos;
    }

    /**
     * @param resu_ottributos the resu_ottributos to set
     */
    public void setResu_ottributos(String resu_ottributos) {
        this.resu_ottributos = resu_ottributos;
    }

    /**
     * @return the resu_total
     */
    public String getResu_total() {
        return resu_total;
    }

    /**
     * @param resu_total the resu_total to set
     */
    public void setResu_total(String resu_total) {
        this.resu_total = resu_total;
    }

    /**
     * @return the resu_identificador
     */
    public String getResu_identificador() {
        return resu_identificador;
    }

    /**
     * @param resu_identificador the resu_identificador to set
     */
    public void setResu_identificador(String resu_identificador) {
        this.resu_identificador = resu_identificador;
    }

    /**
     * @return the resu_fechagenera
     */
    public String getResu_fechagenera() {
        return resu_fechagenera;
    }

    /**
     * @param resu_fechagenera the resu_fechagenera to set
     */
    public void setResu_fechagenera(String resu_fechagenera) {
        this.resu_fechagenera = resu_fechagenera;
    }

    /**
     * @return the resu_fec
     */
    public String getResu_fec() {
        return resu_fec;
    }

    /**
     * @param resu_fec the resu_fec to set
     */
    public void setResu_fec(String resu_fec) {
        this.resu_fec = resu_fec;
    }

    /**
     * @return the tasa_otrostributos
     */
    public String getTasa_otrostributos() {
        return tasa_otrostributos;
    }

    /**
     * @param tasa_otrostributos the tasa_otrostributos to set
     */
    public void setTasa_otrostributos(String tasa_otrostributos) {
        this.tasa_otrostributos = tasa_otrostributos;
    }

    /**
     * @return the nota_motivo
     */
    public String getNota_motivo() {
        return nota_motivo;
    }

    /**
     * @param nota_motivo the nota_motivo to set
     */
    public void setNota_motivo(String nota_motivo) {
        this.nota_motivo = nota_motivo;
    }

    /**
     * @return the nota_sustento
     */
    public String getNota_sustento() {
        return nota_sustento;
    }

    /**
     * @param nota_sustento the nota_sustento to set
     */
    public void setNota_sustento(String nota_sustento) {
        this.nota_sustento = nota_sustento;
    }

    /**
     * @return the nota_tipodoc
     */
    public String getNota_tipodoc() {
        return nota_tipodoc;
    }

    /**
     * @param nota_tipodoc the nota_tipodoc to set
     */
    public void setNota_tipodoc(String nota_tipodoc) {
        this.nota_tipodoc = nota_tipodoc;
    }

    /**
     * @return the nota_documento
     */
    public String getNota_documento() {
        return nota_documento;
    }

    /**
     * @param nota_documento the nota_documento to set
     */
    public void setNota_documento(String nota_documento) {
        this.nota_documento = nota_documento;
    }

    public String getRete_regi() {
        return rete_regi;
    }

    public void setRete_regi(String rete_regi) {
        this.rete_regi = rete_regi;
    }

    public String getRete_tasa() {
        return rete_tasa;
    }

    public void setRete_tasa(String rete_tasa) {
        this.rete_tasa = rete_tasa;
    }

    public String getRete_total_elec() {
        return rete_total_elec;
    }

    public void setRete_total_elec(String rete_total_elec) {
        this.rete_total_elec = rete_total_elec;
    }

    public String getRete_total_rete() {
        return rete_total_rete;
    }

    public void setRete_total_rete(String rete_total_rete) {
        this.rete_total_rete = rete_total_rete;
    }

    public String getRete_tipo_moneda_objetivo() {
        return rete_tipo_moneda_objetivo;
    }

    public void setRete_tipo_moneda_objetivo(String rete_tipo_moneda_objetivo) {
        this.rete_tipo_moneda_objetivo = rete_tipo_moneda_objetivo;
    }

    public String getRete_tipo_moneda_tipo_cambio() {
        return rete_tipo_moneda_tipo_cambio;
    }

    public void setRete_tipo_moneda_tipo_cambio(String rete_tipo_moneda_tipo_cambio) {
        this.rete_tipo_moneda_tipo_cambio = rete_tipo_moneda_tipo_cambio;
    }

    public String getRete_tipo_moneda_fecha() {
        return rete_tipo_moneda_fecha;
    }

    public void setRete_tipo_moneda_fecha(String rete_tipo_moneda_fecha) {
        this.rete_tipo_moneda_fecha = rete_tipo_moneda_fecha;
    }

    public String getItem_otros() {
        return item_otros;
    }

    public void setItem_otros(String item_otros) {
        this.item_otros = item_otros;
    }

    public String getDocu_forma_pago() {
        return docu_forma_pago;
    }

    public void setDocu_forma_pago(String docu_forma_pago) {
        this.docu_forma_pago = docu_forma_pago;
    }

    public String getDocu_observacion() {
        return docu_observacion;
    }

    public void setDocu_observacion(String docu_observacion) {
        this.docu_observacion = docu_observacion;
    }

    public String getClie_direccion() {
        return clie_direccion;
    }

    public void setClie_direccion(String clie_direccion) {
        this.clie_direccion = clie_direccion;
    }

    public String getDocu_vendedor() {
        return docu_vendedor;
    }

    public void setDocu_vendedor(String docu_vendedor) {
        this.docu_vendedor = docu_vendedor;
    }

    public String getDocu_pedido() {
        return docu_pedido;
    }

    public void setDocu_pedido(String docu_pedido) {
        this.docu_pedido = docu_pedido;
    }

    public String getDocu_guia_remision() {
        return docu_guia_remision;
    }

    public void setDocu_guia_remision(String docu_guia_remision) {
        this.docu_guia_remision = docu_guia_remision;
    }

    public String getClie_orden_compra() {
        return clie_orden_compra;
    }

    public void setClie_orden_compra(String clie_orden_compra) {
        this.clie_orden_compra = clie_orden_compra;
    }

//    public List<OtrosDetalles> getOtrosDetalles() {
//        return otrosDetalles;
//    }
//
//    public void setOtrosDetalles(List<OtrosDetalles> otrosDetalles) {
//        this.otrosDetalles = otrosDetalles;
//    }

    public String getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(String idExterno) {
        this.idExterno = idExterno;
    }

    public String getClie_correo_cpe1() {
        return clie_correo_cpe1;
    }

    public void setClie_correo_cpe1(String clie_correo_cpe1) {
        this.clie_correo_cpe1 = clie_correo_cpe1;
    }

    public String getClie_correo_cpe2() {
        return clie_correo_cpe2;
    }

    public void setClie_correo_cpe2(String clie_correo_cpe2) {
        this.clie_correo_cpe2 = clie_correo_cpe2;
    }

    public String getClie_correo_cpe0() {
        return clie_correo_cpe0;
    }

    public void setClie_correo_cpe0(String clie_correo_cpe0) {
        this.clie_correo_cpe0 = clie_correo_cpe0;
    }

    public String getDocu_leyenda_a() {
        return docu_leyenda_a;
    }

    public void setDocu_leyenda_a(String docu_leyenda_a) {
        this.docu_leyenda_a = docu_leyenda_a;
    }

    public String getDocu_leyenda_b() {
        return docu_leyenda_b;
    }

    public void setDocu_leyenda_b(String docu_leyenda_b) {
        this.docu_leyenda_b = docu_leyenda_b;
    }

    public String getDocu_leyenda_c() {
        return docu_leyenda_c;
    }

    public void setDocu_leyenda_c(String docu_leyenda_c) {
        this.docu_leyenda_c = docu_leyenda_c;
    }

    public String getDocu_leyenda_d() {
        return docu_leyenda_d;
    }

    public void setDocu_leyenda_d(String docu_leyenda_d) {
        this.docu_leyenda_d = docu_leyenda_d;
    }

    public String getDocu_leyenda_e() {
        return docu_leyenda_e;
    }

    public void setDocu_leyenda_e(String docu_leyenda_e) {
        this.docu_leyenda_e = docu_leyenda_e;
    }

    public String getDocu_leyenda_f() {
        return docu_leyenda_f;
    }

    public void setDocu_leyenda_f(String docu_leyenda_f) {
        this.docu_leyenda_f = docu_leyenda_f;
    }

    public String getDocu_tipo_operacion() {
        return docu_tipo_operacion;
    }

    public void setDocu_tipo_operacion(String docu_tipo_operacion) {
        this.docu_tipo_operacion = docu_tipo_operacion;
    }

    public String getDocu_anticipo_prepago() {
        return docu_anticipo_prepago;
    }

    public void setDocu_anticipo_prepago(String docu_anticipo_prepago) {
        this.docu_anticipo_prepago = docu_anticipo_prepago;
    }

    public String getDocu_anticipo_docu_tipo() {
        return docu_anticipo_docu_tipo;
    }

    public void setDocu_anticipo_docu_tipo(String docu_anticipo_docu_tipo) {
        this.docu_anticipo_docu_tipo = docu_anticipo_docu_tipo;
    }

    public String getDocu_anticipo_docu_numero() {
        return docu_anticipo_docu_numero;
    }

    public void setDocu_anticipo_docu_numero(String docu_anticipo_docu_numero) {
        this.docu_anticipo_docu_numero = docu_anticipo_docu_numero;
    }

    public String getDocu_anticipo_tipo_docu_emi() {
        return docu_anticipo_tipo_docu_emi;
    }

    public void setDocu_anticipo_tipo_docu_emi(String docu_anticipo_tipo_docu_emi) {
        this.docu_anticipo_tipo_docu_emi = docu_anticipo_tipo_docu_emi;
    }

    public String getDocu_anticipo_numero_docu_emi() {
        return docu_anticipo_numero_docu_emi;
    }

    public void setDocu_anticipo_numero_docu_emi(String docu_anticipo_numero_docu_emi) {
        this.docu_anticipo_numero_docu_emi = docu_anticipo_numero_docu_emi;
    }

    public String getDocu_anticipo_total() {
        return docu_anticipo_total;
    }

    public void setDocu_anticipo_total(String docu_anticipo_total) {
        this.docu_anticipo_total = docu_anticipo_total;
    }

    
    
}
