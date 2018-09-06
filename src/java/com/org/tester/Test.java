/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.tester;

import com.org.factories.ConnectionPool;
import com.org.ws.FacElectronica;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author oswaldo
 */
public class Test {

    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionPool.obtenerConexionMysql();
            System.out.println("__" + FacElectronica.generarXMLZipiadoFactura("1",conn));
        } catch (Exception er) {
            er.printStackTrace();
        } finally {
            ConnectionPool.closeConexion(conn);
        }
    }
}
