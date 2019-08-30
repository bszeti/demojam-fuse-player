package rhte.demojam.battlefield;


import io.restassured.RestAssured;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
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


@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("unittest")
@DirtiesContext
@DisableJmx(true)
public class RouteTest {

    @LocalServerPort
    int port;

    @Autowired
    HealthManager healthManager;

    @Before
    public void before(){
        RestAssured.port = port;
    }


    @Test
    public void syncSegmentsWithParentNoGIS() {
        healthManager.setCurrent(10);

        when()
            .get("/hit/test")
        .then()
            .statusCode(200)
            .body(equalTo("9"))
        ;
    }
}
