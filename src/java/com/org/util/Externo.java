package com.org.util;

/**
 *
 * @author oswaldo
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Externo {

    private static Log log = LogFactory.getLog(Externo.class);

    public static String creaEmpresa(String clieRuc, String clieNombre, String clieEmail, Connection conn) {
        String respuesta = null;

        try {
            // buscar si existe empresa
            String sql = "select '1' from empresa where empr_ruc =? and empr_tipo = 'EXTERNO' ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, clieRuc);
            ResultSet rs = ps.executeQuery();
            String existeEmpresa = "0";
            String existeUsuario = "0";
            String codigoEmpresa = "0";
            String passwordUsuario = "0";
            if (rs.next()) {
                existeEmpresa = rs.getString(1);
            }
            // No Existe
            if ("0".equals(existeEmpresa)) {
                sql = "insert empresa (empr_ruc,empr_razon_social,empr_tipo) values (?,?,'EXTERNO')";
                ps = conn.prepareStatement(sql);
                ps.setString(1, clieRuc);
                ps.setString(2, clieNombre);
                ps.executeUpdate();
            }
            //Busca codigo de empresa existente o creade
            sql = "select ide_empresa from empresa where empr_ruc =? and empr_tipo = 'EXTERNO' ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, clieRuc);
            rs = ps.executeQuery();
            if (rs.next()) {
                codigoEmpresa = rs.getString(1);
            }

            Random rnd = new Random();
            rnd.setSeed(10000000);
            codigoEmpresa = String.valueOf(1 + rnd.nextLong()).substring(1, 8);
            // buscar si existe usuario por ruc
            sql = "select '1' from pvm_usuario where ruc =? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, clieRuc);
            rs = ps.executeQuery();
            if (rs.next()) {
                existeUsuario = rs.getString(1);
            }
            // buscar si existe usuario por correo
            sql = "select '1' from pvm_usuario where email =? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, clieEmail);
            rs = ps.executeQuery();
            if (rs.next()) {
                existeUsuario = rs.getString(1);
            }
            // buscar si existe usuario por dni
            sql = "select '1', password from pvm_usuario where dni =? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, clieEmail);
            rs = ps.executeQuery();
            if (rs.next()) {
                existeUsuario = rs.getString(1);
                passwordUsuario = rs.getString(2);
            }
            // vaklidamos que tenga correo

            if (clieEmail != null && clieEmail.trim().length() > 10) {
                // No Existe
                if ("0".equals(existeUsuario)) {
                    sql = "insert pvm_usuario(activo, email, nombre_usuario, ruc, dni,password,sexo) values (b'1',?,?,?,?, '****' ,'FEMENINO')";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, clieEmail);
                    ps.setString(2, clieNombre);
                    ps.setString(3, clieRuc);
                    ps.setString(4, codigoEmpresa);
                    ps.executeUpdate();
                    passwordUsuario = "****";
                }
            }
            //Si no se ha logueado enviar/reeniar correo
            if ("****".equals(passwordUsuario)) {

            }

        } catch (Exception ex) {
            log.error("LecturaXML.getRespuestaSunat - Error : " + ex.toString());
            Logger.getLogger(Externo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return respuesta;
    }

}
