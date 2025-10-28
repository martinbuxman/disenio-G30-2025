package tp.g30.controller;


import org.springframework.web.bind.annotation.*;
import tp.g30.clases.Huesped;
import tp.g30.gestores.Gestor_Usuario;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
public class HuespedController {

    private final Gestor_Usuario gestorHuesped;

    public HuespedController(Gestor_Usuario gestorHuesped) {
        this.gestorHuesped = gestorHuesped;
    }

    @PostMapping("/alta")
    public void altaHuesped(@RequestBody Huesped huesped) {
        gestorHuesped.dar_alta_huesped(huesped);
    }
}