package tp.g30.dao;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tp.g30.clases.Estadia;
import tp.g30.interfaces.EstadiaDAO;

@Repository
public class EstadiaDaoDB implements EstadiaDAO{
    @PersistenceContext
    private EntityManager em;
    public void guardarEstadia(Estadia nuevaEstadia) {
        em.persist(nuevaEstadia);
        em.flush();
    }

    
}
