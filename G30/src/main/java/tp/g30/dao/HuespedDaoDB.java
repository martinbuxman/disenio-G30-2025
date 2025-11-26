package tp.g30.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
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
    @Transactional
    public Huesped saveHuesped(Huesped huesped) {
    em.persist(huesped);
    em.flush(); 
    return huesped;
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
    }
@Override
    public List<Huesped> buscarHuespedes(String nombre, String apellido, String tipoDocumento, String numDocumento) {
        
        StringBuilder jpql = new StringBuilder("SELECT h FROM Huesped h WHERE 1=1"); 

        
        if (StringUtils.hasText(apellido)) {
            jpql.append(" AND LOWER(h.apellido) LIKE LOWER(:apellido)");
        }
        
        if (StringUtils.hasText(nombre)) {
            jpql.append(" AND LOWER(h.nombre) LIKE LOWER(:nombre)");
        }
        
        if (StringUtils.hasText(tipoDocumento)) {
            jpql.append(" AND h.tipo_documento = :tipoDocumento");
        }

        if (StringUtils.hasText(numDocumento)) { 
            jpql.append(" AND h.num_documento = :numDocumento"); 
        } 
        
        TypedQuery<Huesped> query = em.createQuery(jpql.toString(), Huesped.class);
        
        if (StringUtils.hasText(apellido)) {
            query.setParameter("apellido", "%" + apellido + "%");
        }
        
        if (StringUtils.hasText(nombre)) {
            query.setParameter("nombre", "%" + nombre + "%");
        }
        
        if (StringUtils.hasText(tipoDocumento)) {
            
            TipoDocumento tipoDocEnum = TipoDocumento.valueOf(tipoDocumento);
            
            query.setParameter("tipoDocumento", tipoDocEnum);
        }

        if (StringUtils.hasText(numDocumento)) {
            query.setParameter("numDocumento", numDocumento); 
        }

        return query.getResultList();
    }
    public Huesped buscarHuespedPorDatos(String nombre, String apellido, String telefono) {
        String jpql = "SELECT h FROM Huesped h WHERE h.nombre = :nombre AND h.apellido = :apellido AND h.telefono = :telefono";
    
        TypedQuery<Huesped> query = em.createQuery(jpql, Huesped.class);
        

        query.setParameter("nombre", nombre);
        query.setParameter("apellido", apellido);
        query.setParameter("telefono", telefono);

        query.setMaxResults(1); 
        
        List<Huesped> results = query.getResultList();
        
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }

    }

}