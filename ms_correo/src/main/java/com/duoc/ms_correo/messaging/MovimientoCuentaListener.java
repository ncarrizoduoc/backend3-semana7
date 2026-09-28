package com.duoc.ms_correo.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

@Component 
public class MovimientoCuentaListener {
    
    @JmsListener(destination = "movimientos_cuentas")
    public void recibir(String json){
        ObjectMapper mapper = new ObjectMapper();
        MovimientoCuentaEvent event = mapper.readValue(json, MovimientoCuentaEvent.class);
        System.out.println("Se ha recibido evento con ID: " + event.getId());
        procesarEvento(event);
    }

    public void procesarEvento(MovimientoCuentaEvent event){
        System.out.println("Procesando evento con ID: " + event.getId());
        System.out.println("Se ha enviado el siguiente comprobante de transacción al correo del titular de la cuenta:");
        System.out.println(event.toString());
    }
}
