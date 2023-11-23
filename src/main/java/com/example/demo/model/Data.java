package com.example.demo.model;

import com.example.demo.common.lucy.filter.XssEscapeStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@lombok.Data
public class Data {

//  @JsonDeserialize(using = XssEscapeStringDeserializer.class)
//  @IgnoreXssEscape
  private String data;
}
