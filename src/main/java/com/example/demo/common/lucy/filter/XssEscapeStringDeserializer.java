package com.example.demo.common.lucy.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Optional;

@JsonComponent
public class XssEscapeStringDeserializer extends StringDeserializer implements ContextualDeserializer {

	private static final long serialVersionUID = -5674806753698655941L;
	
  // lucy xss escape filter
	private final transient XssEscapeFilter xssEscapeFilter = XssEscapeFilter.getInstance();
	
	private StringDeserializer stringSerializer = new StringDeserializer();
	
	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
			throws JsonMappingException {

		boolean propertyXssEscapeIngored = Optional.ofNullable(property.getAnnotation(IgnoreXssEscape.class)).isPresent();
		
    // 해당 프로퍼티에 IgnoreXssEscape annotation이 정의 되어있다면 xssEscape 대상이 아니다.
		if(propertyXssEscapeIngored) {
			return stringSerializer;
		}
		
		boolean contextXssEscapeIngored = Optional.ofNullable(property.getContextAnnotation(IgnoreXssEscape.class)).isPresent();
    // 해당 프로퍼티에 IgnoreXssEscape annotation이 정의 되어있다면 xssEscape 대상이 아니다.
		if(contextXssEscapeIngored) {
			return stringSerializer;
		}
		
		return this;
	}
	
	@Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String value = super.deserialize(p, ctxt);
		return xssEscapeFilter.doFilter(getRequestPath(), p.currentName(), value);
	}
	
	private String getRequestPath() {
    	RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    	if(requestAttributes instanceof ServletRequestAttributes) {
    		HttpServletRequest req = ((ServletRequestAttributes)requestAttributes).getRequest();
    		return req.getRequestURI();
    	}
    	
    	return null;
    }
    
}