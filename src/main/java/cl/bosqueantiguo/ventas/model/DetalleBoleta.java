package cl.bosqueantiguo.ventas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "detalle_boletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleBoleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del producto (del microservicio de Productos)
    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "subtotal")
    private Double subtotal;

    // Muchos detalles pertenecen a una boleta.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boleta_id")
    @JsonBackReference // Evita loops infinitos
    private Boleta boleta;
}