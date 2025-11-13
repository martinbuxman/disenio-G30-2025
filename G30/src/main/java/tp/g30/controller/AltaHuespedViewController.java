package tp.g30.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/huespedes")
public class AltaHuespedViewController {

    // Mapea la ruta completa: http://localhost:8080/huespedes/alta
    @GetMapping("/alta") 
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("titulo", "Formulario de Alta de Huésped");
        // Devuelve el nombre de la plantilla: index.html
        return "alta"; 
    }
}