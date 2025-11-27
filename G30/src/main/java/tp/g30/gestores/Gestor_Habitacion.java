package tp.g30.gestores;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp.g30.clases.EstadoHabitacion;
import tp.g30.clases.Habitacion;
import tp.g30.dao.HabitacionDaoDB;
import tp.g30.dto.EstadoHabitacionDTO;
import tp.g30.dto.HabitacionDTO;
import tp.g30.enums.Estado;

@Service
public class Gestor_Habitacion {
    @Autowired
    private HabitacionDaoDB habitacionDao;
    public List<HabitacionDTO> mostrarEstadoHabitaciones(LocalDate fechaDesde, LocalDate fechaHasta) {
        
        List<Habitacion> habitaciones = habitacionDao.listarHabitaciones();
        
        List<HabitacionDTO> resultado = new ArrayList<>();

        for (Habitacion hab : habitaciones) {
            List<EstadoHabitacion> historial = hab.getHistoriaEstados();
            if (historial == null) {
                historial = new ArrayList<>();
            }
            List<EstadoHabitacionDTO> estadosDTO = historial.stream()
                .filter(estado -> esEstadoRelevante(estado, fechaDesde, fechaHasta))
                .map(estado -> new EstadoHabitacionDTO(
                        estado.getEstado().toString(),
                        estado.getFechaInicio(),
                        estado.getFechaFin()
                ))
                .collect(Collectors.toList());

            HabitacionDTO dto = new HabitacionDTO(hab.getNumeroHabitacion(), hab.getTipo(), estadosDTO);
            
            resultado.add(dto);
        }

        return resultado;
    }


    private boolean esEstadoRelevante(EstadoHabitacion estado, LocalDate busquedaInicio, LocalDate busquedaFin) {
        if (estado.getFechaInicio() == null || estado.getFechaFin() == null) {
            return false; 
        }
        return !estado.getFechaFin().isBefore(busquedaInicio) && 
               !estado.getFechaInicio().isAfter(busquedaFin);
    }

    public void crearEstadoHabitacion(Habitacion habitacion, LocalDate fecha_inicio, LocalDate fecha_fin) {
        EstadoHabitacion nuevoEstado = new EstadoHabitacion();
        nuevoEstado.setEstado(tp.g30.enums.Estado.RESERVADA); 
        nuevoEstado.setFechaInicio(fecha_inicio);
        nuevoEstado.setFechaFin(fecha_fin);

        List<EstadoHabitacion> historiaEstados = habitacion.getHistoriaEstados();
        if (historiaEstados == null) {
            historiaEstados = new ArrayList<>();
            habitacion.setHistoriaEstados(historiaEstados);
        }
        historiaEstados.add(nuevoEstado);

        habitacionDao.actualizarHabitacion(habitacion);
    }
        public void crearEstadoHabitacion(Habitacion habitacion, Estado estado,LocalDate fecha_inicio, LocalDate fecha_fin) {
        EstadoHabitacion nuevoEstado = new EstadoHabitacion();
        nuevoEstado.setEstado(estado); 
        nuevoEstado.setFechaInicio(fecha_inicio);
        nuevoEstado.setFechaFin(fecha_fin);

        List<EstadoHabitacion> historiaEstados = habitacion.getHistoriaEstados();
        if (historiaEstados == null) {
            historiaEstados = new ArrayList<>();
            habitacion.setHistoriaEstados(historiaEstados);
        }
        historiaEstados.add(nuevoEstado);

        habitacionDao.actualizarHabitacion(habitacion);
    }


    public Habitacion buscarHabitacionPorNumero(Long numHabitacion) {
        return habitacionDao.buscarPorNumero(numHabitacion);
    }
}
