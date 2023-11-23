package com.example.demo.common.lucy;

import com.example.demo.common.lucy.filter.XssEscapeServletFilter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class XssEscapeFilterConfig implements WebMvcConfigurer {

  private final ObjectMapper objectMapper;

  @Bean
  public FilterRegistrationBean<XssEscapeServletFilter> filterRegistrationBean() {
    FilterRegistrationBean<XssEscapeServletFilter> filterRegistration = new FilterRegistrationBean<>();
    filterRegistration.setFilter(new XssEscapeServletFilter());
    filterRegistration.setOrder(1);
    filterRegistration.addUrlPatterns("/test-1");
    filterRegistration.addUrlPatterns("/api/*");
    return filterRegistration;
  }

  // 모든 응답에 대해서 <>& 등 HTMLCharacterEscapes 정의된 문자가 필터링 된다.
//  @Bean
//  public MappingJackson2HttpMessageConverter jacksonEscapeConverter() {
//    ObjectMapper copy = objectMapper.copy();
//    copy.getFactory()
//        .setCharacterEscapes(new HTMLCharacterEscapes());
//    return new MappingJackson2HttpMessageConverter(copy);
//  }

  private static class HTMLCharacterEscapes extends CharacterEscapes {

    private final int[] asciiEscapes;

    public HTMLCharacterEscapes() {
      asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
      asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
      asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
      asciiEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;
      asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
      asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
      asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
      asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
      asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
      return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
      SerializedString serializedString = null;
      char charAt = (char) ch;
      //emoji jackson parse 오류에 따른 예외 처리
      if (Character.isHighSurrogate(charAt) || Character.isLowSurrogate(charAt)) {
        StringBuilder sb = new StringBuilder();
        sb.append("\\u");
        sb.append(String.format("%04x", ch));
        serializedString = new SerializedString(sb.toString());
      } else {
        serializedString = new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString(charAt)));
      }
      return serializedString;
    }
  }
}
