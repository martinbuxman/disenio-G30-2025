package tp.g30.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tp.g30.clases.Habitacion;
import tp.g30.interfaces.HabitacionDAO;

@Repository
public class HabitacionDaoDB implements HabitacionDAO{
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Habitacion> listarHabitaciones() {
        String jpql = "SELECT h FROM Habitacion h"; 
        return entityManager.createQuery(jpql, Habitacion.class).getResultList();
    }
}
