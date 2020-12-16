package com.github.cnida.basicwebapp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @RequestMapping("/")
    public String index() {
        return "Hello world!";
    }

    @RequestMapping("/somePage")
    public String somePage() {
        return "You are on some page";
    }

}
