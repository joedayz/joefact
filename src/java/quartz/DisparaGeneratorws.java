package quartz;

import com.org.factories.ConnectionPool;
import com.org.model.beans.DocumentoBean;
import com.org.model.despatchers.DElectronicoDespachador;
import com.org.util.LecturaXML;
import com.org.ws.BolElectronica;
import com.org.ws.FacElectronica;
import com.org.ws.NCElectronica;
import com.org.ws.NDElectronica;
import com.org.ws.ResElectronica;
import com.org.ws.ResumenBaja;
import com.org.ws.RetElectronica;
import java.sql.Connection;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Clase que implementa la tarea final a ejecutar
 *
 * @author gonzalo.delgado
 *
 */
public class DisparaGeneratorws {

    private static Log log = LogFactory.getLog(DisparaGeneratorws.class);

    public synchronized static void generator() {
        //log.info("generator");
        Connection conn = null;
        try {
            //log.info("generator - conectar a MySQl");
            conn = ConnectionPool.obtenerConexionMysql();
            System.out.println("__Ejecución disparar " + new Date().toString());
            //log.info("generator - buscar pendientes");
            DocumentoBean item = DElectronicoDespachador.pendienteDocElectronico(conn);
            String iddoc = null;
            String tipodoc = null;
            String result = "x";
            if (item != null && item.getDocu_tipodocumento().trim() != null) {
                log.info("generator - Existe pendiente");
                iddoc = item.getDocu_codigo();
                //tipodoc = Integer.valueOf(item.getDocu_tipodocumento()).toString().trim();
                tipodoc = item.getDocu_tipodocumento().trim();
                LecturaXML.guardarProcesoEstado(iddoc, "B", " | ".split("\\|", 0), "", conn);
                //System.out.println("___Preparando el doc. " + Util.equivalenciaTipoDocNombre(tipodoc) + " " + iddoc);
                System.out.println("___Preparando el doc. " + tipodoc + " " + iddoc);
                log.info("generator - extrayendo datos");
                switch (tipodoc) {
                    case "01":
                        result = FacElectronica.generarXMLZipiadoFactura(iddoc, conn);
                        break;
                    case "03":
                        result = BolElectronica.generarXMLZipiadoBoleta(iddoc, conn);
                        break;
                    case "07":   //Nota de Credito
                        result = NCElectronica.generarXMLZipiadoNC(iddoc, conn);
                        break;
                    case "08":   //Nota de Debito
                        result = NDElectronica.generarXMLZipiadoND(iddoc, conn);
                        break;
                    case "20":   //Retenciones
                        result = RetElectronica.generarXMLZipiadoRetencion(iddoc, conn);
                        break;
                    case "RS":  //resumen
                        result = ResElectronica.enviarResumenASunat(iddoc, conn);//enviarZipASunat
                        break;
                    case "RA":  //Anulacion
                        result = ResumenBaja.enviarBajaASunat(iddoc, conn);//enviarZipASunat
                        break;
//            case "OE":  //Obtener Estado
//                result = EstadoTicket.obtenerEstado(iddoc);//consulta directamente a sunat
//                break;
                    default:
                        result = "0100|Operacion nula";
                        break;

                }
            }
            if (!result.equals("x")) {
                System.out.println("Resultado " + result);
            }

        } catch (Exception er) {
            er.printStackTrace();
                log.error("generator - error " + er.toString());
            
        } finally {
            ConnectionPool.closeConexion(conn);
        }
    }

}
