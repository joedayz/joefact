package quartz;

import com.org.factories.ConnectionPool;
import com.org.factories.Util;
import com.org.model.beans.DocumentoBean;
import com.org.model.despatchers.ResumenDespachador;
import com.org.ws.ResElectronica;
import com.org.ws.ResumenDiarioElectronica;
import com.org.ws.ResumenReversion;
import java.sql.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Clase que implementa la tarea final a ejecutar
 *
 * @author gonzalo.delgado
 *
 */
public class ResumenDiarioGenerator {

    private static Log log = LogFactory.getLog(ResumenDiarioGenerator.class);

    public synchronized static void generator() {
        Connection conn = null;
        try {
            log.info("ResumenDiarioGenerator.generator - Obtener conexion a MysQL");
            conn = ConnectionPool.obtenerConexionMysql();
            log.info("ResumenDiarioGenerator.generator - Buscar Pendientes ");
            DocumentoBean item = ResumenDespachador.pendienteResElectronicoDiario(conn);
            String iddoc = null;
            String tipodoc = null;
            String result = "x";
            if (item != null) {
                iddoc = item.getResu_codigo();
                tipodoc = item.getResu_tipo();
                ResElectronica.guardarStatus(iddoc, "B", conn);
                log.info("ResumenDiarioGenerator.generator - Existen Pendientes a Procesar " + Util.equivalenciaTipoDocNombre(tipodoc) + " " + iddoc);
                switch (tipodoc) {
                    case "RC":  //Diario de Boletas
                        if (!"1.1".equals(item.getResu_version())) {
                            result = ResElectronica.enviarResumenASunat(iddoc, conn);//enviarZipASunat
                        } else {
                            result = ResumenDiarioElectronica.enviarResumenASunat(iddoc, conn);//enviarZipASunat
                        }
                        break;
                    case "RR":  //Resumen de Reversiones
                        result = ResumenReversion.enviarReversionASunat(iddoc, conn);//enviarZipASunat
                        break;
//            case "OE":  //Obtener Estado
//                result = EstadoTicket.obtenerEstado(iddoc);//consulta directamente a sunat
//                break;
                    default:
                        result = "0100|Operacion nula";
                        log.info("ResumenDiarioGenerator.generator - No Existen Tipo a Procesar " + Util.equivalenciaTipoDocNombre(tipodoc) + " " + iddoc);
                        break;

                }
            }
            if (!result.equals("x")) {
                System.out.println("Resultado " + result);
            }

        } catch (Exception er) {
            er.printStackTrace();
            log.error("ResumenDiarioGenerator.generator - Error" + er.toString());
        } finally {
            ConnectionPool.closeConexion(conn);
        }
    }

}
