package com.example.demo.contorller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

  @GetMapping("/test-1")
  public String test1(@RequestParam(value = "test") String test, Model model) {
    model.addAttribute("test", test);
    return "main";
  }

  @GetMapping("/test-2")
  public String test2(@RequestParam(value = "test") String test, Model model) {
    model.addAttribute("test", test);
    return "main";
  }
}
