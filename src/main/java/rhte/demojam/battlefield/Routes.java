package rhte.demojam.battlefield;


import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(Routes.class);

    @Autowired
    HealthManager healthManager;

    private ApplicationContext applicationContext;

    @Autowired
    Battlefield battleField;

    @Override
    public void configure() throws Exception {


        log.debug("Player URLs: {}",battleField.urls);

        restConfiguration("servlet")
            .bindingMode(RestBindingMode.off)
            //.contextPath("/hit")
        ;

        rest("hit")
            .get("{byplayer}").route().routeId("rhte.demojam.battlefield.hitby")
                .log("Hit by ${header.byplayer}.")
                .setBody().method(healthManager,"decreaseHealth(${header.byplayer})")
                .log("Health: ${body}")
                .wireTap("direct:checkAlive")

        ;

        //Check if health was zero and we should terminate
        from("direct:checkAlive").routeId("rhte.demojam.battlefield.checkZeroHealth")
            .choice()
                .when(simple("${body} == 0"))
                    .log("Shutting down...")
                    .setBody().method(healthManager,"getLasthitby")
                    .to("file:{{terminationlog.dir}}?fileName={{terminationlog.file}}")
                    .script().method(this,"shutdownApp")
        ;

        //Hit another player - random pick
        from("timer:hitPlayer?period={{BATTLEFIELD_HIT_PERIOD}}").routeId("rhte.demojam.battlefield.hitplayer")
            .onException(Exception.class)
                .maximumRedeliveries(0)
                .handled(true)
                .log("Failed to hit player: ${exchangeProperty.playerUrl}. ${exception} - HTTP${header."+Exchange.HTTP_RESPONSE_CODE+"}")
            .end()

            .setProperty("battlefield",simple("${ref:battlefield}"))
            .setProperty("playerIndex",simple("${random(${exchangeProperty.battlefield.urls.size})}"))
            .log(LoggingLevel.DEBUG,"Player index: ${exchangeProperty.playerIndex}; total: ${exchangeProperty.battlefield.urls.size}")
            .setProperty("playerUrl",simple("${exchangeProperty.battlefield.urls[${exchangeProperty.playerIndex}]}"))
            .log("Hit player: ${exchangeProperty.playerUrl}")
            .toD("http://${exchangeProperty.playerUrl}/api/hit/{{BATTLEFIELD_PLAYER_NAME}}?synchronous=true")
        ;



    }

    //Shutdown by another thread so we let Camel complete the Exchange
    public void shutdownApp(){
        new Thread( () ->
            ((ConfigurableApplicationContext) applicationContext).close()
        ).start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
