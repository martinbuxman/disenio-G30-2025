/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package tp.g30.interfaces;

import tp.g30.dto.HuespedDTO;
import tp.g30.enums.TipoDocumento;
import tp.g30.clases.Huesped;
import java.util.List;


/**
 *
 * @author juanc
 */
public interface HuespedDAO {
    void modificar_huesped(HuespedDTO huespedOriginal, HuespedDTO huespedModificado);
    List<Huesped> buscar_huespedes(HuespedDTO huesped);
    boolean existe_documento(TipoDocumento tipoDocumento, int numeroDocumento);
}
