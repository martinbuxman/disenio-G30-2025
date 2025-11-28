package tp.g30.gestores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tp.g30.clases.Estadia;
import tp.g30.clases.Habitacion;
import tp.g30.clases.Huesped;
import tp.g30.dao.EstadiaDaoDB;
import tp.g30.dto.EstadiaDTO;
import tp.g30.dto.HuespedDTO;

@Service   
public class Gestor_Estadia {
    @Autowired
    Gestor_Huesped gestorHuesped;
    @Autowired
    Gestor_Habitacion gestorHabitacion;
    @Autowired
    EstadiaDaoDB estadiaDao;

    public Gestor_Estadia() {
    }


    @Transactional
    public void crearEstadia(List<EstadiaDTO> estadiasDTO) {
        if (estadiasDTO == null || estadiasDTO.isEmpty()) {
            return;
        }

        for (EstadiaDTO estadiaDTO : estadiasDTO) {
            List<Huesped> huespedesFinales = estadiaDTO.getHuespedes().stream()
                .map(this::resolverHuesped)
                .collect(Collectors.toList());

            Long numHabitacion = estadiaDTO.getHabitacion().getNumeroHabitacion();
            Habitacion habitacion = gestorHabitacion.buscarHabitacionPorNumero(numHabitacion); 
            
            if (habitacion == null) {
                throw new RuntimeException("Habitación no encontrada: " + numHabitacion);
            }

            if (estadiaDTO.getHabitacion().getHistoriaEstados() != null && 
                !estadiaDTO.getHabitacion().getHistoriaEstados().isEmpty()) {
                
                gestorHabitacion.crearEstadoHabitacion(
                    habitacion, 
                    tp.g30.enums.Estado.OCUPADA,
                    estadiaDTO.getHabitacion().getHistoriaEstados().get(0).getFechaInicio(),
                    estadiaDTO.getHabitacion().getHistoriaEstados().get(0).getFechaFin()
                );
            }

            Estadia nuevaEstadia = new Estadia();
            nuevaEstadia.setHabitacion(habitacion);
            nuevaEstadia.setHuespedes(huespedesFinales);
            nuevaEstadia.setFecha_check_in(estadiaDTO.getFecha_check_in());

            estadiaDao.guardarEstadia(nuevaEstadia);
        }
    }
    private Huesped resolverHuesped(HuespedDTO dto) {
        if (dto.getId() != 0) {
            return gestorHuesped.buscarPorId(dto.getId()); 
        } else {
            return gestorHuesped.dar_alta_huesped(dto,true); 
        }
    }
}

