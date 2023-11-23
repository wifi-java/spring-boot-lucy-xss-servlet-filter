# spring-boot-lucy-xss-servlet-filter

lucy-xss-servlet-filter 메이븐 추가
```
<dependency>
  <groupId>com.navercorp.lucy</groupId>
  <artifactId>lucy-xss-servlet</artifactId>
  <version>2.0.1</version>
</dependency>
```

[lucy-xss-servlet-filter-rule.xml](https://github.com/naver/lucy-xss-servlet-filter/blob/master/src/test/resources/lucy-xss-servlet-filter-rule.xml) 파일을 /resource 경로에 추가


XssEscapeServletFilter를 Bean에 등록
```
@Bean
public FilterRegistrationBean<XssEscapeServletFilter> filterRegistrationBean() {
  FilterRegistrationBean<XssEscapeServletFilter> filterRegistration = new FilterRegistrationBean<>();
  filterRegistration.setFilter(new XssEscapeServletFilter());
  filterRegistration.setOrder(1);
  filterRegistration.addUrlPatterns("/*");
  return filterRegistration;
}
```

[lucy-xss-filter 깃 주소](https://github.com/naver/lucy-xss-servlet-filter)

---

XssEscapeServletFilter를 Bean에 등록할때 spring-boot 3.0 이상에서 servlet에 패키지 경로가 javax에서 jakarta로 변경되어 현재 lucy-xss-servlet-filter 라이브러리에서 XssEscapeServletFilter, XssEscapeServletFilterWrapper를 따로 파일을 생성하여
패키지 경로를 jakarta로 변경해주어야 사용 가능하다.

XssEscapeServletFilter.java
```
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter;
import jakarta.servlet.*;

import java.io.IOException;

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
```

XssEscapeServletFilterWrapper.java
```
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
```

---
lucy-xss-servlet-filter가 POST 방식에서 폼 파라미터가 아닌 raw(JSON)데이터로 전달받을때 필터가 동작하지 않는 문제가 있었고, 검색을 통하여 아래 블로그를 찾아 해결할 수 있었다.

https://circlee7.medium.com/jackson-contextualdeserializer-2f0d20f08ce0

블로그에 내용은 jackson에 StringDeserializer를 상속받아 XssEscapeStringDeserializer구현하여 json 에서 모델로 전환될때 lucy-filter를 적용시키는 방법인듯 하다.
다만, 사용 방법은 필터가 적용될 변수에 어노테이션으로 JsonDeserialize를 추가하는 방식으로 사용할 수 있다.
```
public class Data {
  @JsonDeserialize(using = XssEscapeStringDeserializer.class)
  private String data;
}
```

또는 XssEscapeStringDeserializer파일에 어노테이션으로 JsonComponent를 추가하면 공통 적용이 가능하다.
```
@JsonComponent
public class XssEscapeStringDeserializer extends StringDeserializer implements ContextualDeserializer {
}
```

공통으로 적용시킬 경우 특정 변수에서는 필터를 제외시켜야 하는 경우가 생기는데 이럴때는 필터가 적용되지 말아야할 변수에 IgnoreXssEscape 어노테이션을 추가하거나 lucy-xss-servlet-filter-rule.xml 파일에 제외시킬 url를 관리하면 된다.
```
public class Data {
  @IgnoreXssEscape
  private String data;
}
```
```
<url-rule-set>
  <url-rule>
    <url disable="true">/disableUrl1.do</url>
  </url-rule>
</url-rule-set>
```
