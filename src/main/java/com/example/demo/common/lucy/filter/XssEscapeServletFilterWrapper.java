package com.example.demo.common.lucy.filter;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * lucy-filter 가 javax.servlet 되어 있어 해당 소스를 가져 와서 jakarta.servlet 경로를 맞춰 줌
 */
public class XssEscapeServletFilterWrapper extends HttpServletRequestWrapper {
  private XssEscapeFilter xssEscapeFilter;
  private String path = null;

  public XssEscapeServletFilterWrapper(ServletRequest request, XssEscapeFilter xssEscapeFilter) {
    super((HttpServletRequest) request);
    this.xssEscapeFilter = xssEscapeFilter;
    String contextPath = ((HttpServletRequest) request).getContextPath();
    this.path = ((HttpServletRequest) request).getRequestURI()
                                              .substring(contextPath.length());
  }

  public String getParameter(String paramName) {
    String value = super.getParameter(paramName);
    return this.doFilter(paramName, value);
  }

  public String[] getParameterValues(String paramName) {
    String[] values = super.getParameterValues(paramName);
    if (values == null) {
      return values;
    } else {
      for (int index = 0; index < values.length; ++index) {
        values[index] = this.doFilter(paramName, values[index]);
      }

      return values;
    }
  }

  public Map<String, String[]> getParameterMap() {
    Map<String, String[]> paramMap = super.getParameterMap();
    Map<String, String[]> newFilteredParamMap = new HashMap();
    Set<Map.Entry<String, String[]>> entries = paramMap.entrySet();
    Iterator var4 = entries.iterator();

    while (var4.hasNext()) {
      Map.Entry<String, Object> entry = (Map.Entry) var4.next();
      String paramName = (String) entry.getKey();
      Object[] valueObj = (Object[]) ((Object[]) entry.getValue());
      String[] filteredValue = new String[valueObj.length];

      for (int index = 0; index < valueObj.length; ++index) {
        filteredValue[index] = this.doFilter(paramName, String.valueOf(valueObj[index]));
      }

      newFilteredParamMap.put(entry.getKey(), filteredValue);
    }

    return newFilteredParamMap;
  }

  private String doFilter(String paramName, String value) {
    return this.xssEscapeFilter.doFilter(this.path, paramName, value);
  }
}