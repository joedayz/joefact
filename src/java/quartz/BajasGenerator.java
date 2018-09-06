package quartz;

import com.org.factories.ConnectionPool;
import com.org.factories.Util;
import com.org.model.beans.DocumentoBean;
import com.org.model.despatchers.ResumenBajaDespachador;
import com.org.ws.ResumenBaja;
import com.org.ws.ResumenReversion;
import java.sql.Connection;
import java.util.Date;

/**
 * Clase que implementa la tarea final a ejecutar
 *
 * @author gonzalo.delgado
 *
 */
public class BajasGenerator {

    public synchronized static void generator() {
        Connection conn = null;
        try {
            conn = ConnectionPool.obtenerConexionMysql();
            System.out.println("__Ejecución del generador de Bajas " + new Date().toString());
            DocumentoBean item = ResumenBajaDespachador.pendienteDocElectronicoReversionesBaja(conn);
            String iddoc = null;
            String tipodoc = null;
            String result = "x";
            if (item != null) {
                iddoc = item.getResu_codigo();
                tipodoc = item.getResu_tipo();
                ResumenReversion.guardarStatus(iddoc, "B", conn);
                System.out.println("___Preparando el doc. " + Util.equivalenciaTipoDocNombre(tipodoc) + " " + iddoc);
                switch (tipodoc) {
                    case "RA":  //Anulacion
                        result = ResumenBaja.enviarBajaASunat(iddoc, conn);//enviarZipASunat
                        break;
                    case "RR":  //Resumen de Reversiones
                        result = ResumenReversion.enviarReversionASunat(iddoc, conn);//enviarZipASunat
                        break;
//            case "OE":  //Obtener Estado
//                result = EstadoTicket.obtenerEstado(iddoc);//consulta directamente a sunat
//                break;
                    default:
                        result = "0100|Operacion nula";
                        break;

                }
            }
            if(!result.equals("x"))
            System.out.println("Resultado " + result);

        } catch (Exception er) {
            er.printStackTrace();
        } finally {
            ConnectionPool.closeConexion(conn);
        }
    }

}
