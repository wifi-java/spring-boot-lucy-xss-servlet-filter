package com.example.demo.common.lucy.filter;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter;
import jakarta.servlet.*;

import java.io.IOException;

/**
 * lucy-filter 가 javax.servlet 되어 있어 해당 소스를 가져 와서 jakarta.servlet 경로를 맞춰 줌
 */
public class XssEscapeServletFilter implements Filter {
  private XssEscapeFilter xssEscapeFilter = XssEscapeFilter.getInstance();

  public XssEscapeServletFilter() {
  }

  public void init(FilterConfig filterConfig) throws ServletException {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    chain.doFilter(new XssEscapeServletFilterWrapper(request, this.xssEscapeFilter), response);
  }

  public void destroy() {
  }
}