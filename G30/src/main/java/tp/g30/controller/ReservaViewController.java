package tp.g30.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservaViewController {
    @GetMapping("/reservar-habitacion")
    public String mostrarFormularioReserva() {
        return "reservar-habitacion"; 
    }
}
