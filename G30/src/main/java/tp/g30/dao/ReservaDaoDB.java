package tp.g30.dao;

import java.time.LocalDate;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.NoResultException;

import tp.g30.clases.Reserva;

@Repository
public class ReservaDaoDB {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Reserva guardarReserva(Reserva reserva) {
        em.persist(reserva);
        em.flush();
        return reserva;
    }

    public String obtenerNombreResponsableReserva(Long numeroHabitacion, LocalDate fecha) {
        String queryStr = "SELECT CONCAT(r.huespedPrincipal.nombre, ' ', r.huespedPrincipal.apellido) " +
                  "FROM Reserva r " +
                  "JOIN r.listaHabitacionesRerservadas rh " +
                          "WHERE rh.habitacion.numeroHabitacion = :numeroHabitacion " +
                          "AND :fecha BETWEEN rh.fecha_inicio AND rh.fecha_fin";

        try {
                System.out.println("DEBUG: obtenerNombreResponsableReserva - numeroHabitacion=" + numeroHabitacion + " fecha=" + fecha);
            String nombreCompleto = em.createQuery(queryStr, String.class)
                .setParameter("numeroHabitacion", numeroHabitacion)
                .setParameter("fecha", fecha)
                .getSingleResult();
                System.out.println("DEBUG: obtenerNombreResponsableReserva - nombre=" + nombreCompleto);
            return nombreCompleto;
        } catch (NoResultException nre) {
            return "Desconocido (No encontrado)";
        } catch (Exception e) {
            e.printStackTrace();
            return "Desconocido (Error interno)";
        }
    }


    
}
