package cl.bosqueantiguo.ventas.dto;

import lombok.Data;
import java.util.List;

// DTO para recibir la solicitud de crear una venta
// Esto es lo que el Frontend enviará en el body del POST
@Data
public class VentaRequestDTO {
    
    private Long usuarioId;
    private List<DetalleRequestDTO> detalles;

    @Data
    public static class DetalleRequestDTO {
        private Long productoId;
        private Integer cantidad;
        // Nota: El precio y subtotal no se reciben del cliente,
        // se calculan en el backend para seguridad.
    }
}
