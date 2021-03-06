/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.factories;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;

/**
 *
 * @author ecavero
 */
public class ConnectionPool {
    public static void closeConexion(Connection conexion) {
        try {
            if (conexion != null) {
                conexion.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static Connection obtenerConexion(String fuente) throws SQLException{
        DataSource ds=null;
        try{
            Context contexto=new InitialContext();
            Context ctx = (Context) contexto.lookup("java:comp/env");
            ds=(DataSource)ctx.lookup(fuente);
            
        }catch(Exception ex){
            throw new SQLException(ex);
        }
        return ds.getConnection();
    }
    public static Connection obtenerConexionMysql() throws SQLException{
        return obtenerConexion("jdbc/genxml");
    }
    
    
}