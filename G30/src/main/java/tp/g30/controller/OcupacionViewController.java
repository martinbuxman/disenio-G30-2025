package tp.g30.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ocupacion")
public class OcupacionViewController {

    @GetMapping("/checkin")
    public String mostrarPantallaCheckIn() {
        return "ocupar-habitacion"; 
    }
}