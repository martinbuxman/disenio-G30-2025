package tp.g30.controller;


import org.springframework.web.bind.annotation.*;
import tp.g30.clases.Huesped;
import tp.g30.gestores.Gestor_Usuario;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/huespedes")
public class HuespedController {

    private final Gestor_Usuario gestorHuesped;
    public HuespedController(Gestor_Usuario gestorHuesped) {
        this.gestorHuesped = gestorHuesped;
    }

@PostMapping("/alta")
public ResponseEntity<?> altaHuesped(@Valid @RequestBody Huesped huesped) {
    try {
        Huesped nuevo = gestorHuesped.dar_alta_huesped(huesped);
        return ResponseEntity.ok(nuevo);
    } catch (IllegalStateException e) {
        // Duplicado u otro error de negocio
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        // Cualquier otro error inesperado
        return ResponseEntity.internalServerError().body("Error al registrar el huésped: " + e.getMessage());
    }
}
}