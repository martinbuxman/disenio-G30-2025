/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tp.g30.clases;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Estadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: Una estadía está asociada a UNA habitación.
    @ManyToOne
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    private Float valor_estadia;

    // Relación: Una estadía puede tener varios huéspedes.
    @OneToMany
    @JoinColumn(name = "estadia_id")
    private List<Huesped> huespedes = new ArrayList<>();

    // Relación: Una estadía puede tener varias facturas.
    @OneToMany
    @JoinColumn(name = "estadia_id")
    private List<Factura> lista_facturas = new ArrayList<>();

    // Relación: Una estadía puede tener muchos consumos.
    @OneToMany
    @JoinColumn(name = "estadia_id")
    private List<Consumo> lista_consumos = new ArrayList<>();

    private LocalDate fecha_check_in;
    private LocalDate fecha_check_out;
}

