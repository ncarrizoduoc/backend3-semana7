package com.duoc.msBanco.messaging;

import java.io.Serializable;
import java.time.LocalDate;

import com.duoc.msBanco.model.MovimientoCuenta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoCuentaEvent implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDate fecha;
    private String transaccion;
    private Long cuentaId;
    private Integer monto;
    private String descripcion;

    public MovimientoCuentaEvent(MovimientoCuenta mov){
        id = mov.getId();
        fecha = mov.getFecha();
        transaccion = mov.getTransaccion();
        cuentaId = mov.getCuentaId();
        monto = mov.getMonto();
        if(mov.getDescripcion() != null){
            descripcion = mov.getDescripcion();
        } else {
            descripcion = "SIN DESCRIPCION";
        }
    }

}
