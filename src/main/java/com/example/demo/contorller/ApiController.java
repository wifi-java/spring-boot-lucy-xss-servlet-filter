package com.example.demo.contorller;

import com.example.demo.model.Data;
import com.nhncorp.lucy.security.xss.XssSaxFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ApiController {

  @GetMapping("/get/test-1")
  public Map<String, Object> test1() {
    String a = "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/yPqtheslXOw?si=4at9wTLaDdvFySWQ\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";

    Map<String, Object> map = new HashMap<>();
    map.put("test", a);

    return map;
  }

  @GetMapping("/get/test-2")
  public Map<String, Object> test2() {
    String a = "<iframe width=\"560\" height=\"315\" src=\"https://www.naver.com\" title=\"Naver video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
    XssSaxFilter filter = XssSaxFilter.getInstance("lucy-xss-sax.xml");
    Map<String, Object> map = new HashMap<>();
    map.put("test", filter.doFilter(a));

    return map;
  }

  @GetMapping("/get/test-3")
  public Map<String, Object> test3() {
    String a = "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/yPqtheslXOw?si=4at9wTLaDdvFySWQ\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
    XssSaxFilter filter = XssSaxFilter.getInstance("lucy-xss-sax.xml");

    Map<String, Object> map = new HashMap<>();
    map.put("test", filter.doFilter(a));

    return map;
  }

  @PostMapping("/post/model")
  public Map<String, Object> model(@ModelAttribute Data data) {
    log.info("{}", data.getData());

    Map<String, Object> resultMap = new HashMap<>();

    resultMap.put("test", data.getData());

    return resultMap;
  }

  @PostMapping("/post/body")
  public Map<String, Object> body(@RequestBody Data data) {
    log.info("{}", data.getData());

    Map<String, Object> resultMap = new HashMap<>();

    resultMap.put("test", data.getData());

    return resultMap;
  }
}
