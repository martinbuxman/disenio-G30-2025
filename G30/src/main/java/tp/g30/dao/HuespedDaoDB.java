package tp.g30.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import tp.g30.interfaces.HuespedDAO;
import tp.g30.dto.HuespedDTO;
import tp.g30.clases.Huesped;
import tp.g30.enums.TipoDocumento;

import java.util.List;

@Repository
public class HuespedDaoDB implements HuespedDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void guardarHuesped(Huesped huesped) {
        em.persist(huesped);
        em.flush();
    }
    
    @Override
    public boolean existe_documento(TipoDocumento tipoDocumento, long numeroDocumento) {
        String query = "SELECT COUNT(h) FROM Huesped h WHERE h.tipo_documento = :tipo AND h.num_documento = :num";
        Long count = em.createQuery(query, Long.class)
                       .setParameter("tipo", tipoDocumento)
                       .setParameter("num", numeroDocumento)
                       .getSingleResult();
        return count > 0;
    }

    @Override
    public void modificar_huesped(HuespedDTO huespedOriginal, HuespedDTO huespedModificado) {
        // Implementación pendiente
    }
    @Override
    public List<Huesped> buscar_huespedes(HuespedDTO huesped) {
        // Implementación pendiente
        return null;
    }

}