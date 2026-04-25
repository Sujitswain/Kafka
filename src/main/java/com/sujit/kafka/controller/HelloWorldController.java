package com.sujit.kafka.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/api")
@RestController
public class HelloWorldController {

    @GetMapping("/helloworld")
    public String sayHelloWorld() {
        return "Hello World";
    }

}
