package cl.bosqueantiguo.ventas.service;

import cl.bosqueantiguo.ventas.dto.VentaRequestDTO;
import cl.bosqueantiguo.ventas.dto.VentaResponseDTO;
import java.util.List;

public interface VentaService {

    VentaResponseDTO registrarVenta(VentaRequestDTO ventaRequest);
    
    List<VentaResponseDTO> listarVentas();
    
    VentaResponseDTO obtenerVentaPorId(Long id);
    
    List<VentaResponseDTO> listarVentasPorUsuario(Long userId);
    
    void anularVenta(Long id);
}
