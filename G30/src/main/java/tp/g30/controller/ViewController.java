package tp.g30.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Controlador MVC simple
public class ViewController {

    @GetMapping("/")
    public String mostrarPaginaInicio(Model model) {
        model.addAttribute("titulo", "Hotel Premier - G30");
        return "index"; // Devuelve index.html
    }
}