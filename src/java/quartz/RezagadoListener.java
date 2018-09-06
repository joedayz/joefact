package quartz;

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * Clase donde se implementa el escuchador definido en web.xml El listener se
 * ejecutar� al arrancar la aplicaci�n. Su funcionamiento ser� definir una tarea
 * en Quartz y lanzar el trigger con los par�metros que interesen (en este caso
 * un retardo de 60 segundos).
 *
 * @author gonzalo.delgado
 *
 */
public class RezagadoListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            //Instanciamos el gestor de tareas, a traves de la clase SchedulerFactory
            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();

            sched.getListenerManager().addSchedulerListener(new MySchedulerListener(sched));

            //Arrancamos el gestor de tareas.
            sched.start();

            //Definimos un trabajo
            /*QuartzJob - clase donde se define el trabajo a realizar (en este*
			 * caso, es la misma que la definida en quartz_jobs.xml pues queremos
			 * ejecutar lo mismo) */
            JobBuilder jobBuilder = JobBuilder.newJob(RezagadoJob.class);
            JobDataMap data = new JobDataMap();
            data.put("latch", this);

            JobDetail job = jobBuilder.usingJobData("example", "quartz.RezagadoListener")
                    .usingJobData(data)
                    .withIdentity("RezagadoJob", "group1")
                    .build();

            System.out.println("Definido el Job en el rezagado " + new Date().toString());

            //Fecha Ejecución - 1 minuto de retardo tras el arranque del server
            Date fecha_ejec = new Date(System.currentTimeMillis() + 60000);

            //Definimos un trigger que ocurrirá una sola vez y no se repetirá
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("rezagadoTrigger", "inicioTriggers")
                    .startAt(fecha_ejec)
                    .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ? *"))
                    .build();

            //Registramos en el  gestor el trabajo y su trigger asociado.
            //El gestor ser� responsable de ejecutar el trabajo cuando se dispare el trigger 
            sched.scheduleJob(job, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}
