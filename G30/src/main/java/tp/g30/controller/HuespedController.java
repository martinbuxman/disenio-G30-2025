package tp.g30.controller;


import org.springframework.web.bind.annotation.*;
import tp.g30.clases.Huesped;
import tp.g30.dto.HuespedDTO;
import tp.g30.gestores.Gestor_Usuario;
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
}