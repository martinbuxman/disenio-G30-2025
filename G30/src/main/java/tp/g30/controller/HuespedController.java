package tp.g30.controller;


import org.springframework.web.bind.annotation.*;
import tp.g30.clases.Huesped;
import tp.g30.dto.HuespedDTO;
import tp.g30.gestores.Gestor_Usuario;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import tp.g30.excepciones.DocumentoDuplicadoException;
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
    public ResponseEntity<?> altaHuesped(@Valid @RequestBody HuespedDTO huespedDTO,
        @RequestParam(defaultValue = "false") boolean forzar) { 
        try {
            Huesped nuevo = gestorHuesped.dar_alta_huesped(huespedDTO, forzar);
            return ResponseEntity.ok(nuevo);
            
        } catch (DocumentoDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al registrar el huésped: " + e.getMessage());
        }
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<Huesped>> buscarHuespedesApi(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "apellido", required = false) String apellido,
            @RequestParam(value = "tipo_documento", required = false) String tipoDocumento,
            @RequestParam(value = "num_documento", required = false) String numDocumento) {
        try {
            // Llama al gestor/servicio para obtener la lista de resultados
            List<Huesped> resultados = gestorHuesped.buscarHuespedes(
                nombre, apellido, tipoDocumento, numDocumento); 
            
            // Retorna la lista de huéspedes como JSON con código 200 OK
            return ResponseEntity.ok(resultados);
            
        } catch (Exception e) {
            // Manejo de errores para la API
            // Nota: Podrías devolver un objeto ErrorDTO en lugar de una lista vacía
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
}