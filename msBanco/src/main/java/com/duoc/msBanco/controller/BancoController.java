package com.duoc.msBanco.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.msBanco.exception.ErrorResponse;
import com.duoc.msBanco.model.MovimientoCuenta;
import com.duoc.msBanco.model.TransferenciaRequest;
import com.duoc.msBanco.service.BancoService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController 
@RequestMapping("/api/banco")
public class BancoController {

    private final BancoService bancoService;

    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @GetMapping("/estado-cuenta/{id}")
    @CircuitBreaker(name = "bancoBreaker", fallbackMethod ="findByIdFallback")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bancoService.findById(id));
    }

    // Método fallback para búsqueda de estdo de cuenta por ID
    // Retorna un objeto ErrorResponse informando la no disponibilidad del servicio de banco
    public ResponseEntity<ErrorResponse> findByIdFallback(Long id, Throwable cause) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "Banco no disponible",
            "No fue posible consultar el estado de cuenta en este momento",
            "/api/banco/estado-cuenta/" + id
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @PostMapping("/transferencias")
    @CircuitBreaker(name = "bancoBreaker", fallbackMethod = "realizarTransferenciaFallback")
    public ResponseEntity<?> realizarTransferencia(@Valid @RequestBody TransferenciaRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(bancoService.realizarTransferencia(request));
    }

    // Método fallback para transferencia fallida entre cuentas
    // Retorna un ErrorResponse informando la no disponibilidad del servicio de banco
    public ResponseEntity<ErrorResponse> realizarTransferenciaFallback(TransferenciaRequest request, Throwable cause){
        Long idOrigen = request.getCuentaOrigenId();
        Long idDestino = request.getCuentaDestinoId();
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "Banco no disponible",
            "No es posible realizar transferencia desde cuenta con ID " + idOrigen
            + " a cuenta con ID " + idDestino + " en este momento",
            "/api/banco/transferencias"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @PostMapping("/movimientos")
    @CircuitBreaker(name = "bancoBreaker", fallbackMethod = "realizarMovimientoFallback")
    public ResponseEntity<?> realizarMovimiento(@Valid @RequestBody MovimientoCuenta movimiento) {
        movimiento.setId(null); // ID null para que se genere automáticamente
        return ResponseEntity.status(HttpStatus.CREATED).body(bancoService.realizarMovimiento(movimiento));
    }

    // Método fallback para regsitro de movimiento bancario fallido
    // Retorna un mensaje informando la no disponibilidad del servicio de banco
    public ResponseEntity<ErrorResponse> realizarMovimientoFallback(MovimientoCuenta movimiento, Throwable cause){
        Long idCuenta = movimiento.getCuentaId();

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "Banco no disponible",
            "No es posible realizar registrar un movimiento para la cuenta con ID " + idCuenta
            + " en este momento",
            "/api/banco/movimientos"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);

    }

    @GetMapping("/movimientos")
    @CircuitBreaker(name = "bancoBreaker", fallbackMethod = "verMovimientosFallback")
    public ResponseEntity<?> verMovimientos(
        @RequestParam(required = false) String tipoMovimiento,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha
    ) {
        if (tipoMovimiento != null && fecha != null) {
            return ResponseEntity.ok(bancoService.findMovimientoByTipoAndFecha(tipoMovimiento, fecha));
        } else if (tipoMovimiento != null) {
            return ResponseEntity.ok(bancoService.findMovimientoByTipo(tipoMovimiento));
        } else if (fecha != null) {
            return ResponseEntity.ok(bancoService.findMovimientoByFecha(fecha));
        } else {
            return ResponseEntity.ok(bancoService.findAllMovimientos());
        }
    }

    // Método fallback para consulta de movimientos bancarios
    // Retorna un ErrorResponse informando la no disponibilidad del banco
    public ResponseEntity<ErrorResponse> verMovimientosFallback(
        String tipoMovimiento,
        LocalDate fecha,
        Throwable cause
    ){
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "Banco no disponible",
            "No es posible consultar movimientos en este momento",
            "/api/banco/movimientos"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

}
