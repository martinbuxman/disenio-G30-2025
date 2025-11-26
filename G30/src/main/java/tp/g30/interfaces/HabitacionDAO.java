package tp.g30.interfaces;

import java.util.List;

import tp.g30.clases.Habitacion;

public interface HabitacionDAO {
    List<Habitacion> listarHabitaciones();

    Habitacion buscarId(long id);
}
