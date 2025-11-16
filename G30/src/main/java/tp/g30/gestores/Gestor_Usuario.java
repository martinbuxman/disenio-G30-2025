/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.gestores;

import tp.g30.clases.Direccion;
import tp.g30.clases.Huesped;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp.g30.excepciones.DocumentoDuplicadoException;

import tp.g30.dao.HuespedDaoDB;
import tp.g30.dto.HuespedDTO;


/**
 *
 * @author juanc
 */

@Service

public class Gestor_Usuario{
    @Autowired
    private HuespedDaoDB huespedDaoDB;


    @Autowired
    public Gestor_Usuario(HuespedDaoDB huespedDaoDB) {
        this.huespedDaoDB = huespedDaoDB;
    }
 
    public Huesped dar_alta_huesped(HuespedDTO huesped, boolean forzarGuardado) { 
        
        if (!forzarGuardado && huespedDaoDB.existe_documento(huesped.getTipoDocumento(), huesped.getNumDocumento())) {
            throw new DocumentoDuplicadoException(
                "¡CUIDADO! El tipo y numero de documento ya existen en el sistema: " + huesped.getTipoDocumento() + 
                ", " + huesped.getNumDocumento() + "."
            );
        }
        Direccion nuevaDireccion = new Direccion(huesped.getDIRECCION().getCalle(),
                                               huesped.getDIRECCION().getNumero(),
                                               huesped.getDIRECCION().getDepartamento(),
                                               huesped.getDIRECCION().getPiso(),
                                               huesped.getDIRECCION().getCodigoPostal(),
                                               huesped.getDIRECCION().getLocalidad(),
                                               huesped.getDIRECCION().getProvincia(),
                                               huesped.getDIRECCION().getPais());

        Huesped nuevoHuesped = new Huesped(huesped.getTelefono(), huesped.getEmail(), huesped.getOcupacion(), huesped.getCondicionIVA(),
                                         huesped.getApellido(), huesped.getNombre(), huesped.getTipoDocumento(),
                                         (int) huesped.getNum_documento(), (int) huesped.getCuit(), huesped.getFechaNacimiento(),
                                         nuevaDireccion, huesped.getNacionalidad());
        huespedDaoDB.guardarHuesped(nuevoHuesped);
        
        return nuevoHuesped;
    }
    public List<Huesped> buscarHuespedes(String nombre, String apellido, String tipoDocumento, String numDocumento) {
        return huespedDaoDB.buscarHuespedes(nombre, apellido, tipoDocumento, numDocumento);
    }
}