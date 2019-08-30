package rhte.demojam.battlefield;


import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder implements ApplicationContextAware {

    @Autowired
    HealthManager healthManager;

    private ApplicationContext applicationContext;

    @Override
    public void configure() throws Exception {

        rest()
            .get("hit/{byplayer}").route().routeId("hiy-byplayer")
                .log("Hit by ${header.byplayer}.")
                .setBody().method(healthManager,"decreaseHealth(${header.byplayer})")
                .log("Health after hit by ${header.byplayer}: ${body}")
                .wireTap("direct:checkAlive")

        ;

        from("direct:checkAlive").routeId("checkAlive")
            .choice()
                .when(simple("${body} == 0"))
                    .log("Shutting down...")
                    .setBody().method(healthManager,"getLasthitby")
                    .to("file:{{terminationlog.dir}}?fileName={{terminationlog.file}}")
                    .script().method(this,"shutdownApp")
        ;



    }

    public void shutdownApp(){
        new Thread( () ->
            ((ConfigurableApplicationContext) applicationContext).close()
        ).start();
//        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
//        System.exit(exitCode);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
