package tp.g30.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tp.g30.clases.Huesped;
import tp.g30.gestores.Gestor_Usuario;

@Controller
@RequestMapping("/huespedes")
public class HuespedViewController {
    private final Gestor_Usuario gestorHuesped;
    
    public HuespedViewController(Gestor_Usuario gestorHuesped) {
        this.gestorHuesped = gestorHuesped;
    }
    // Mapea la ruta completa: http://localhost:8080/huespedes/alta
    @GetMapping("/alta") 
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("titulo", "Formulario de Alta de Huésped");
        return "alta"; 
    }
    @GetMapping("/buscar")
    public String mostrarFormularioBusqueda(Model model) {
        model.addAttribute("titulo", "Formulario de Búsqueda de Huésped"); 
        return "buscar-huesped"; 
    }
    @GetMapping("/resultados")
    public String buscarHuespedes(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "apellido", required = false) String apellido,
            @RequestParam(value = "tipo_documento", required = false) String tipoDocumento,
            @RequestParam(value = "num_documento", required = false) String numDocumento,
            Model model) {

        List<Huesped> huespedes = Collections.emptyList();
        String titulo = "Selección de Huésped para Reserva";
        
        boolean isExplicitSearch = 
            (nombre != null && !nombre.isEmpty()) || 
            (apellido != null && !apellido.isEmpty()) ||
            (tipoDocumento != null && !tipoDocumento.isEmpty()) ||
            (numDocumento != null && !numDocumento.isEmpty());
        
        huespedes = gestorHuesped.buscarHuespedes(nombre, apellido, tipoDocumento, numDocumento);
        
        boolean mostrarModalAlta = isExplicitSearch && huespedes.isEmpty();

        model.addAttribute("nombre", nombre);
        model.addAttribute("apellido", apellido);
        model.addAttribute("tipo_documento", tipoDocumento);
        model.addAttribute("num_documento", numDocumento);
        model.addAttribute("huespedes", huespedes);
        model.addAttribute("titulo", titulo);
        model.addAttribute("mostrarModalAlta", mostrarModalAlta);

        return "resultados-busqueda";
    }
}