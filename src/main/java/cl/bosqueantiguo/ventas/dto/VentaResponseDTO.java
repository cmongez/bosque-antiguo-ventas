package cl.bosqueantiguo.ventas.dto;

import cl.bosqueantiguo.ventas.model.Boleta;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// DTO para enviar la respuesta de una Venta/Boleta
// Esto es lo que enviaremos al Frontend (React)
@Data
public class VentaResponseDTO {
    
    private Long id;
    private LocalDateTime fecha;
    private Double total;
    private Long usuarioId;
    private List<DetalleResponseDTO> detalles;

    @Data
    public static class DetalleResponseDTO {
        private Long id;
        private Long productoId;
        private Integer cantidad;
        private Double subtotal;
    }

    // Constructor para mapear fácil desde la Entidad
    public VentaResponseDTO(Boleta boleta) {
        this.id = boleta.getId();
        this.fecha = boleta.getFecha();
        this.total = boleta.getTotal();
        this.usuarioId = boleta.getUsuarioId();
        this.detalles = boleta.getDetalles().stream()
            .map(detalle -> {
                DetalleResponseDTO dto = new DetalleResponseDTO();
                dto.setId(detalle.getId());
                dto.setProductoId(detalle.getProductoId());
                dto.setCantidad(detalle.getCantidad());
                dto.setSubtotal(detalle.getSubtotal());
                return dto;
            })
            .collect(Collectors.toList());
    }
}