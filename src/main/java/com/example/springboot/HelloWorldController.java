package com.example.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {
  @RequestMapping("/")
  public @ResponseBody String helloWorld() {
    return "Hello world";
  }
}
