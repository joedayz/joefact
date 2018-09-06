package quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Clase que define el trabajo a realizar. Cuando Quartz "alcance" el momento de
 * ejecuci�n buscar� este Job y lanzar� el execute.
 *
 * @author gonzalo.delgado
 *
 */
public class ResumenDiarioJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            //llamada a la clase que implementa la tarea a ejecutar.
            ResumenDiarioGenerator.generator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
