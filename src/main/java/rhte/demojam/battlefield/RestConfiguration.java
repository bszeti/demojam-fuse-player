package rhte.demojam.battlefield;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RestConfiguration extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        restConfiguration("servlet")
            .bindingMode(RestBindingMode.off)
            .contextPath("/")
        ;

    }
}
