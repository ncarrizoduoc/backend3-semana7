package com.duoc.ms_correo.messaging;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class MovimientoCuentaEvent {
    
    private Long id;
    private LocalDate fecha;
    private String transaccion;
    private Long cuentaId;
    private Integer monto;
    private String descripcion;

    @Override 
    public String toString(){
        String texto = "";
        texto += "ID de transacción: " + id;
        texto += "\nID de cuenta: " + cuentaId;
        texto += "\nFecha de la transacción: " + fecha;
        texto += "\nTipo de transacción: " + transaccion;
        texto += "\nMonto: $" + monto;
        texto += "\nDescripción: " + descripcion;

        return texto;
    }

}
