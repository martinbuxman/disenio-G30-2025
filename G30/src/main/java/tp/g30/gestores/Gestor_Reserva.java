package tp.g30.gestores;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp.g30.clases.Huesped;
import tp.g30.clases.Reserva;
import tp.g30.clases.ReservaHabitacion;
import tp.g30.clases.Direccion;
import tp.g30.clases.Habitacion;
import tp.g30.dao.HabitacionDaoDB;
import tp.g30.dao.HuespedDaoDB;
import tp.g30.dao.ReservaDaoDB;
import tp.g30.dao.ReservaHabitacionDaoDB;
import tp.g30.dto.HabitacionDTO;
import tp.g30.dto.HuespedDTO;
import tp.g30.dto.ReservaDTO;
import tp.g30.dto.ReservaHabitacionDTO;
@Service
public class Gestor_Reserva { 
    @Autowired
    private final Gestor_Habitacion gestorHabitacion = new Gestor_Habitacion(); 
    @Autowired
    private final ReservaDaoDB reservaDaoDB = new ReservaDaoDB();
    @Autowired
    private final HuespedDaoDB huespedDaoDB = new HuespedDaoDB();
    @Autowired
    private final HabitacionDaoDB habitacionDaoDB = new HabitacionDaoDB();
    @Autowired
    private final ReservaHabitacionDaoDB reservaHabitacionDaoDB = new ReservaHabitacionDaoDB();

 
    public List<HabitacionDTO> mostrarEstadoHabitaciones(java.time.LocalDate fechaDesde, java.time.LocalDate fechaHasta) {

        return gestorHabitacion.mostrarEstadoHabitaciones(fechaDesde, fechaHasta);
    }

    public Long confirmarReserva(ReservaDTO reservaDTO) throws Exception {

HuespedDTO huespedDTO = reservaDTO.getHuespedPrincipal();
        Huesped huespedPrincipal;
        Huesped huespedExistente = huespedDaoDB.buscarHuespedPorDatos(
            huespedDTO.getNombre(),
            huespedDTO.getApellido(),
            huespedDTO.getTelefono()
        );

        if (huespedExistente != null) {
            huespedPrincipal = huespedExistente;
            System.out.println("DEBUG: Huésped encontrado y reutilizado: ID " + huespedPrincipal.getId());
        } else {
            System.out.println("DEBUG: Huésped no encontrado. Creando nuevo huésped.");
            Direccion direccion = new Direccion();
            Huesped huespedNuevo = new Huesped();

            huespedNuevo.setNombre(huespedDTO.getNombre());
            huespedNuevo.setApellido(huespedDTO.getApellido());
            huespedNuevo.setTelefono(huespedDTO.getTelefono());
            huespedNuevo.setNum_documento(0);
            huespedNuevo.setEmail(huespedDTO.getEmail());
            huespedNuevo.setFecha_nacimiento(huespedDTO.getFecha_nacimiento());
            huespedNuevo.setDireccion(direccion); 
            huespedNuevo.setOcupacion(huespedDTO.getOcupacion());
            huespedNuevo.setNacionalidad(huespedDTO.getNacionalidad());
            huespedNuevo.setCondicionIVA(huespedDTO.getCondicionIVA());
            huespedNuevo.setTipoDocumento(huespedDTO.getTipoDocumento());
            huespedNuevo.setCUIT(0);

            huespedPrincipal = huespedDaoDB.saveHuesped(huespedNuevo);
        }
        Reserva reservaPrincipal = new Reserva();

        reservaPrincipal.setHuespedPrincipal(huespedPrincipal);
        List<ReservaHabitacionDTO> ReservasHabitacionesDTO = reservaDTO.getListaHabitacionesReservadas();
        Reserva reservaPersistida = reservaDaoDB.guardarReserva(reservaPrincipal);

        for (ReservaHabitacionDTO itemDTO : ReservasHabitacionesDTO) {
            ReservaHabitacion item = new ReservaHabitacion(); 
            
            item.setReserva(reservaPersistida);
            item.setFecha_inicio(itemDTO.getFecha_inicio());
            item.setFecha_fin(itemDTO.getFecha_fin());

            Long idHabitacion = itemDTO.getHabitacion().getNumeroHabitacion();
            
            Habitacion habitacion = habitacionDaoDB.buscarId(idHabitacion);
            
            item.setHabitacion(habitacion);
            
            gestorHabitacion.crearEstadoHabitacion(
            habitacion, 
            item.getFecha_inicio(),
            item.getFecha_fin()
            );

            reservaHabitacionDaoDB.save(item);
        }
        return Long.valueOf(reservaPersistida.getId());
    }
}