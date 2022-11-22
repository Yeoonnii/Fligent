package com.example.restcontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/login")
public class ApiLoginRestController {
    @GetMapping(value = "/naver.json")
    public String naverLoginGET(){
        return null;

    }
}
