package javaobservabilitypoc;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@ComponentScan({"javaobservabilitypoc", "config"})
public class JavaObservabilityPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaObservabilityPocApplication.class, args);
    }
}

@RestController
class HelloController {

    private final AtomicInteger counter = new AtomicInteger(0);

    private final Counter requestsCounter;

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);


    @Autowired
    public HelloController(MeterRegistry meterRegistry) {
        this.requestsCounter = Counter.builder("requests_to_hello_endpoint")
                .description("Number of requests to /hello endpoint")
                .register(meterRegistry);
        Gauge.builder("requests_to_hello_endpoint_gauge", counter, AtomicInteger::get)
                .description("Number of requests to /hello endpoint")
                .register(meterRegistry);
    }

    @GetMapping("/hello")
    public String hello() {
        requestsCounter.increment();
        return
                "Hello, Spring Boot!";
    }


    @Scheduled(fixedRate = 500)
    public void incrementCounter() {
        logger.info("Incrementing counter");
        requestsCounter.increment();
    }
}
