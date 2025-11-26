package tp.g30.dao;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ReservaHabitacionDaoDB  {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(tp.g30.clases.ReservaHabitacion reservaHabitacion) {
        em.persist(reservaHabitacion);
        em.flush();
    }
}
