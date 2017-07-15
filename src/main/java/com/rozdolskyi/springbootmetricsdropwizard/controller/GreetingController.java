package com.rozdolskyi.springbootmetricsdropwizard.controller;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private Meter requests;

    public GreetingController(MetricRegistry metricRegistry) {
        this.requests = metricRegistry.meter("hello-requests");
    }

    @GetMapping("/hello")
    public String helloWorld() {
        requests.mark();
        return "Hello World!";
    }
}
