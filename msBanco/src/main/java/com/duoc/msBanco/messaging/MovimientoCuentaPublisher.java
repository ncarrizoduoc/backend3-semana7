package com.duoc.msBanco.messaging;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.duoc.msBanco.model.MovimientoCuenta;

@Component 
public class MovimientoCuentaPublisher {
    private static final String TOPIC = "movimientos_cuentas";
    private final JmsTemplate jmsTemplate;

    public MovimientoCuentaPublisher(JmsTemplate jmsTemplate){
        this.jmsTemplate = jmsTemplate;
    }

    // Publicar evento con información de movimiento de cuenta
    public void publicarMovimientoCuenta(MovimientoCuenta mov){
        MovimientoCuentaEvent event = new MovimientoCuentaEvent(mov);
        jmsTemplate.convertAndSend(TOPIC, event);
    }

}
