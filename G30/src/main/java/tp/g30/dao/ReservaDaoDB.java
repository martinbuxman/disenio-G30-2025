package tp.g30.dao;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

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

    
}
