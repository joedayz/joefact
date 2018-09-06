/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.ws;

import com.org.factories.ConnectionPool;
import com.org.factories.Util;
import java.sql.Connection;
import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author oswaldo
 */
@WebService(serviceName = "WSEnvioSunat")
public class WSEnvioSunat {

    /**
     * This is a sample web service operation
     */
    @PostConstruct
    private void init() {
        /*Este metodo se ejecuta automaticamente al inicializar(Deploy) de los servicios*/
        System.out.println(" ______________________________________");
        System.out.println("|                                      |");
        System.out.println("|  SISTEMA DE FACTURACION ELECTRONICA  |");
        System.out.println("|  WEB-SERVICE - Envio a SUNAT         |");
        System.out.println("|______________________________________|");
    }

//    @WebMethod(operationName = "enviarASunat")
//    public String enviarASunat(@WebParam(name = "fileName") String fileName, @WebParam(name = "tipo") String tipo) {
//        String result = "";
//        String unidadEnvio = Util.getPathZipFilesEnvio();
//        if (tipo.equals("RS")) {
//            result = WSDispatcher.enviarResumenASunat(unidadEnvio, fileName);//enviarZipASunat
//        } else if (tipo.equals("AN")) {
//            result = WSDispatcher.enviarBajaASunat(unidadEnvio, fileName);
//        } else {
//            result = WSDispatcher.enviarASunat(unidadEnvio, fileName);
//        }
//
//        return result;
//    }
    @WebMethod(operationName = "obtenerEstado")
    public String obtenerEstado(@WebParam(name = "iddoc") String iddoc) {
        return EstadoTicket_ocp.obtenerEstado(iddoc);//consulta directamente a sunat;
    }

    @WebMethod(operationName = "generarXML")
    public String generarXML(@WebParam(name = "iddoc") String iddoc, @WebParam(name = "tipodoc") String tipodoc) {
        String result = "";
        System.out.println("___Preparando el doc. " + Util.equivalenciaTipoDocNombre(tipodoc) + " " + iddoc);
        Connection conn = null;
        try {
            conn = ConnectionPool.obtenerConexionMysql();
            switch (tipodoc) {
                case "1":
                    result = FacElectronica.generarXMLZipiadoFactura(iddoc, conn);
                    break;
                case "3":
                    result = BolElectronica.generarXMLZipiadoBoleta(iddoc, conn);
                    break;
                case "7":   //Nota de Credito
                    result = NCElectronica.generarXMLZipiadoNC(iddoc, conn);
                    break;
                case "8":   //Nota de Debito
                    result = NDElectronica.generarXMLZipiadoND(iddoc, conn);
                    break;
                case "20":   //Retenciones
                    result = RetElectronica.generarXMLZipiadoRetencion(iddoc, conn);
                    break;
                case "RS":  //resumen
                    result = ResElectronica.enviarResumenASunat(iddoc,conn);//enviarZipASunat
                    break;
                case "RA":  //Anulacion
                    result = ResumenBaja.enviarBajaASunat(iddoc, conn);//enviarZipASunat
                    break;
                case "RR":  //Resumen de Reversiones
                    result = ResumenReversion.enviarReversionASunat(iddoc, conn);//enviarZipASunat
                    break;
                case "OE":  //Obtener Estado
                    result = EstadoTicket_ocp.obtenerEstado(iddoc);//consulta directamente a sunat
                    break;
                default:
                    result = "0100|Operacion nula";
                    break;

            }
        } catch (Exception er) {
            er.printStackTrace();
        } finally {
            ConnectionPool.closeConexion(conn);
        }
        return result;
    }

}
