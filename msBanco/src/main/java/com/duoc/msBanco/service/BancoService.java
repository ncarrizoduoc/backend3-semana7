package com.duoc.msBanco.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.msBanco.exception.CuentaNotFoundException;
import com.duoc.msBanco.exception.SaldoInsuficienteException;
import com.duoc.msBanco.messaging.MovimientoCuentaPublisher;
import com.duoc.msBanco.model.EstadoCuenta;
import com.duoc.msBanco.model.MovimientoCuenta;
import com.duoc.msBanco.model.TransferenciaRequest;
import com.duoc.msBanco.repository.EstadoCuentaRepository;
import com.duoc.msBanco.repository.MovimientoCuentaRepository;

@Service 
public class BancoService {
    
    private final MovimientoCuentaRepository movRepo;
    private final EstadoCuentaRepository estadoRepo;
    private final MovimientoCuentaPublisher movPublisher;

    public BancoService(MovimientoCuentaRepository movRepo, EstadoCuentaRepository estadoRepo, MovimientoCuentaPublisher movPublisher) {
        this.movRepo = movRepo;
        this.estadoRepo = estadoRepo;
        this.movPublisher = movPublisher;
    }

    // Buscar estado de cuenta por ID
    public EstadoCuenta findById(Long id){
        Optional<EstadoCuenta> encontrado = estadoRepo.findById(id);
        if (encontrado.isEmpty()){
            return null;
        } else {
            EstadoCuenta estado = encontrado.get();
            List<MovimientoCuenta> movimientos = movRepo.findByCuentaId(id);
            
            // Si la cuenta no registra movimientos, retorna una lista vacia
            if (movimientos == null || movimientos.isEmpty()) {
                estado.setMovimientos(new ArrayList<>());
            } else {
                estado.setMovimientos(movimientos);
            }
            return estado;
        }
    }

    @Transactional 
    // Método para realizar una transferencia entre cuentas
    public MovimientoCuenta realizarTransferencia(TransferenciaRequest request){
        // Validar que las cuentas de origen y destino existan
        EstadoCuenta cuentaOrigen = findById(request.getCuentaOrigenId());
        EstadoCuenta cuentaDestino = findById(request.getCuentaDestinoId());
        
        if (cuentaOrigen == null) {
            throw new CuentaNotFoundException("No se encontro la cuenta de origen con ID: " + request.getCuentaOrigenId());
        }
        if (cuentaDestino == null) {
            throw new CuentaNotFoundException("No se encontro la cuenta de destino con ID: " + request.getCuentaDestinoId());
        }
        // Validar que el monto sea positivo
        if (request.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser un valor positivo");
        }
        // Validar que la cuenta de origen tenga suficiente saldo
        if (cuentaOrigen.getSaldo() < request.getMonto()) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta de origen");
        }
        // Realizar la transferencia
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - request.getMonto());
        cuentaOrigen.setGastos(cuentaOrigen.getGastos() + request.getMonto());
        cuentaDestino.setSaldo(cuentaDestino.getSaldo() + request.getMonto());
        cuentaDestino.setIngresos(cuentaDestino.getIngresos() + request.getMonto());

        // Registrar movimientos en la cuenta de origen y destino
        MovimientoCuenta movimientoOrigen = MovimientoCuenta.builder()
                .id(null)
                .cuentaId(cuentaOrigen.getCuentaId())
                .monto(request.getMonto() * -1) // Monto negativo para la cuenta de origen
                .transaccion("transferencia")
                .fecha(request.getFecha())
                .descripcion("Transferencia a cuenta con ID: " + request.getCuentaDestinoId())
                .build();
        cuentaOrigen.getMovimientos().add(movimientoOrigen);

        MovimientoCuenta movimientoDestino = MovimientoCuenta.builder()
                .id(null)
                .cuentaId(cuentaDestino.getCuentaId())
                .monto(request.getMonto())
                .transaccion("transferencia")
                .fecha(request.getFecha())
                .descripcion("Transferencia desde cuenta con ID: " + request.getCuentaOrigenId())
                .build();
        cuentaDestino.getMovimientos().add(movimientoDestino);
        
        // Guardar los cambios
        MovimientoCuenta movOrigen = movRepo.save(movimientoOrigen);
        movRepo.save(movimientoDestino);
        estadoRepo.save(cuentaOrigen);
        estadoRepo.save(cuentaDestino);
        
        // Publicar evento con información de transferencia y retornarla
        movPublisher.publicarMovimientoCuenta(movOrigen);
        return movimientoOrigen;
    }

    @Transactional
    public MovimientoCuenta realizarMovimiento(MovimientoCuenta movimiento) {
        // Validar que la cuenta exista
        EstadoCuenta cuenta = findById(movimiento.getCuentaId());
        if (cuenta == null) {
            throw new CuentaNotFoundException("No se encontro la cuenta con ID: " + movimiento.getCuentaId());
        }

        // Validar que la cuenta tenga suficiente saldo si el monto es negativo
        if (movimiento.getMonto() < 0 && cuenta.getSaldo() < Math.abs(movimiento.getMonto())) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta con ID: " + movimiento.getCuentaId());
        }
        
        // Actualizar el saldo de la cuenta y guardarla en base de datos
        cuenta.setSaldo(cuenta.getSaldo() + movimiento.getMonto());
        if(movimiento.getMonto() > 0){
            cuenta.setIngresos(cuenta.getIngresos() + movimiento.getMonto());
        } else {
            cuenta.setGastos(cuenta.getGastos() + Math.abs(movimiento.getMonto())); 
        }
        estadoRepo.save(cuenta);
        
        // Registrar el movimiento, publicarlo y retornarlo
        MovimientoCuenta movimientoRegistrado = movRepo.save(movimiento);
        movPublisher.publicarMovimientoCuenta(movimientoRegistrado);
        return movimientoRegistrado;
    }

    public List<MovimientoCuenta> findMovimientoByTipo(String tipoMovimiento) {
        return movRepo.findByTransaccion(tipoMovimiento);
    }

    public List<MovimientoCuenta> findMovimientoByFecha(LocalDate fecha) {
        return movRepo.findByFecha(fecha);
    }

    public List<MovimientoCuenta> findMovimientoByTipoAndFecha(String tipoMovimiento, LocalDate fecha) {
        return movRepo.findByTransaccionAndFecha(tipoMovimiento, fecha);
    }
    
    public List<MovimientoCuenta> findAllMovimientos() {
        return movRepo.findAll();
    }

}