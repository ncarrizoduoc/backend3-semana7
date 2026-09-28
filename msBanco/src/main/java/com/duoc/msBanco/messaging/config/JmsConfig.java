package com.duoc.msBanco.messaging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration 
public class JmsConfig {

    @Bean 
    public JacksonJsonMessageConverter messageConverter(){
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTargetType(MessageType.TEXT); // Convertir mensaje a texto
        converter.setTypeIdPropertyName("_type"); // Atributo que indica que el tipo del mensaje es "texto"
        return converter;
    }

}
