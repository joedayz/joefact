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

public class Interno {

    private static Log log = LogFactory.getLog(Interno.class);

    public static String buscarEmpresa(String emprRuc, Connection conn) {
        String respuesta = null;

        try {
            // buscar si existe empresa
            String sql = "select empr_link from empresa where empr_ruc =? and empr_tipo = 'INTERNO' ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emprRuc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = rs.getString(1);
            }

        } catch (Exception ex) {
            log.error("LecturaXML.getRespuestaSunat - Error : " + ex.toString());
            Logger.getLogger(Interno.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//            ConnectionPool.closeConexion(conn);
        }
        return respuesta;
    }

}
