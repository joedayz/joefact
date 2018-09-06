package quartz;

import com.org.factories.ConnectionPool;
import com.org.factories.Util;
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

/**
 * Clase que implementa la tarea final a ejecutar
 *
 * @author gonzalo.delgado
 *
 */
public class RezagadoGenerator {

    public static void generator() {
        Connection conn = null;
        try {
            conn = ConnectionPool.obtenerConexionMysql();
            DocumentoBean item = DElectronicoDespachador.noPendienteDocElectronico(conn);
            String iddoc = null;
            String tipodoc = null;
            String result = "";
            System.out.println("__Ejecución rezagados " + new Date().toString() + "     ____ " + item);

            if (item != null) {
                iddoc = item.getDocu_codigo();
                tipodoc = Integer.valueOf(item.getDocu_tipodocumento()).toString().trim();
                LecturaXML.guardarProcesoEstado(iddoc, "B", " | ".split("\\|", 0), "", conn);
                System.out.println("___Preparando el doc. " + Util.equivalenciaTipoDocNombre(tipodoc) + " " + iddoc);
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
//            case "OE":  //Obtener Estado
//                result = EstadoTicket.obtenerEstado(iddoc);//consulta directamente a sunat
//                break;
                    default:
                        result = "0100|Operacion nula";
                        break;

                }
            }
            System.out.println("Resultado Rezagado: " + result);

        } catch (Exception er) {
            er.printStackTrace();
        } finally {
            ConnectionPool.closeConexion(conn);

        }
    }

}
