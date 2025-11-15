package cl.bosqueantiguo.ventas.service;

import cl.bosqueantiguo.ventas.dto.VentaRequestDTO;
import cl.bosqueantiguo.ventas.dto.VentaResponseDTO;
import cl.bosqueantiguo.ventas.exception.ResourceNotFoundException;
import cl.bosqueantiguo.ventas.model.Boleta;
import cl.bosqueantiguo.ventas.model.DetalleBoleta;
import cl.bosqueantiguo.ventas.repository.BoletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private BoletaRepository boletaRepository;

    @Override
    @Transactional // Asegura que toda la operación (guardar boleta y detalles) sea atómica
    public VentaResponseDTO registrarVenta(VentaRequestDTO ventaRequest) {
        
        Boleta boleta = new Boleta();
        boleta.setUsuarioId(ventaRequest.getUsuarioId());
        
        double totalVenta = 0.0;
        List<DetalleBoleta> detalles = new ArrayList<>();

        for (VentaRequestDTO.DetalleRequestDTO detalleRequest : ventaRequest.getDetalles()) {

            double precioProducto = 50.0; 
            
            // 2. Calcular subtotal
            double subtotal = precioProducto * detalleRequest.getCantidad();
            
            // 3. Crear el detalle
            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setProductoId(detalleRequest.getProductoId());
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setSubtotal(subtotal);
            detalle.setBoleta(boleta); // Asociar detalle con la boleta
            
            detalles.add(detalle);
            totalVenta += subtotal;

        }
        
        boleta.setTotal(totalVenta);
        boleta.setDetalles(detalles);
        
        // Guardamos la boleta. Gracias a CascadeType.ALL, los detalles se guardan automáticamente.
        Boleta boletaGuardada = boletaRepository.save(boleta);
        
        return new VentaResponseDTO(boletaGuardada);
    }

    @Override
    public List<VentaResponseDTO> listarVentas() {
        return boletaRepository.findAll().stream()
                .map(VentaResponseDTO::new) // Mapea cada Boleta a VentaResponseDTO
                .collect(Collectors.toList());
    }

    @Override
    public VentaResponseDTO obtenerVentaPorId(Long id) {
        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con id: " + id));
        return new VentaResponseDTO(boleta);
    }

    @Override
    public List<VentaResponseDTO> listarVentasPorUsuario(Long userId) {
        return boletaRepository.findByUsuarioId(userId).stream()
                .map(VentaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void anularVenta(Long id) {
        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con id: " + id));
        

        boletaRepository.delete(boleta);
    }
}