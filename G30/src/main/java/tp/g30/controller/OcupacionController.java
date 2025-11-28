package tp.g30.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tp.g30.dto.EstadiaDTO;
import tp.g30.gestores.Gestor_Estadia;

@RestController
@RequestMapping("/api/ocupacion")
public class OcupacionController {
    @Autowired
    Gestor_Estadia gestorEstadia;


    @PostMapping("/checkin")
    public ResponseEntity<String> realizarCheckin(@RequestBody List<EstadiaDTO> estadias) {
        try {
            gestorEstadia.crearEstadia(estadias);
            
            return ResponseEntity.ok("Check-in completado exitosamente para " + estadias.size() + " habitación(es).");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al realizar el check-in: " + e.getMessage());
        }
    }
}
