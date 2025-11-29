package cl.bosqueantiguo.ventas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "boletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "fecha", updatable = false)
    private LocalDateTime fecha;

    @Column(name = "total")
    private Double total;

    // Guardamos el ID del usuario. La info completa del usuario
    // se obtendría del microservicio de Usuarios si es necesario.
    @Column(name = "usuario_id")
    private Long usuarioId;

    // Una boleta tiene muchos detalles.
    // CascadeType.ALL: Si guardo/borro una boleta, guarda/borra sus detalles.
    // orphanRemoval = true: Si quito un detalle de la lista, se borra de la DB.
    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference // Evita loops infinitos al serializar a JSON
    private List<DetalleBoleta> detalles;
}
