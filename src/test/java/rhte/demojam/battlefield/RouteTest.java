package rhte.demojam.battlefield;


import io.restassured.RestAssured;
import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

@RunWith(CamelSpringBootRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("unittest")
@DirtiesContext
@DisableJmx(true)
@UseAdviceWith
public class RouteTest {

    @LocalServerPort
    int port;

    @Autowired
    HealthManager healthManager;


    @Autowired
    CamelContext context;

    @Produce(uri = "direct:hitplayer")
    private ProducerTemplate producer;

    @Before
    public void before() throws Exception{
        RestAssured.port = port;

        if (context.getStatus()== ServiceStatus.Stopped) {
            //Execute adviseWith only once
            context.getRouteDefinition("rhte.demojam.battlefield.hitplayer").adviceWith(context, new AdviceWithRouteBuilder() {
                @Override
                public void configure() throws Exception {
                    replaceFromWith("direct:hitplayer");
                }
            });
            context.start();
        }

        healthManager.setCurrent(10);
    }


    @Test
    public void hitByPlayer() {

        when()
            .get("/api/hit/test")
        .then()
            .statusCode(200)
            .body(equalTo("9"))
        ;
    }

    @Test
    public void hitPlayer() {
        //Hit someone. In the players list we only have "localhost", so we will call our own endpoint;
        producer.sendBody(null);

        assertEquals( 9,healthManager.getHealth() );
    }
}
