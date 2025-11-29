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

    @Autowired
    private cl.bosqueantiguo.ventas.client.ProductoClient productoClient;

    @Override
    @Transactional // Asegura que toda la operación sea atómica y haga rollback si hay errores
    public VentaResponseDTO registrarVenta(VentaRequestDTO ventaRequest) {
        
        Boleta boleta = new Boleta();
        boleta.setUsuarioId(ventaRequest.getUsuarioId());
        
        double totalVenta = 0.0;
        List<DetalleBoleta> detalles = new ArrayList<>();

        // Validar todos los productos ANTES de realizar descuentos de stock
        for (VentaRequestDTO.DetalleRequestDTO detalleRequest : ventaRequest.getDetalles()) {
            cl.bosqueantiguo.ventas.client.ProductoDTO producto = productoClient.getProducto(detalleRequest.getProductoId());
            
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado: " + detalleRequest.getProductoId());
            }
            
            if (!producto.getDisponible()) {
                throw new RuntimeException("Producto no disponible: " + producto.getNombre());
            }
            
            if (producto.getStock() < detalleRequest.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + 
                    ". Stock disponible: " + producto.getStock() + ", solicitado: " + detalleRequest.getCantidad());
            }
        }

        // Si todas las validaciones pasan, proceder con la venta y descuento de stock
        for (VentaRequestDTO.DetalleRequestDTO detalleRequest : ventaRequest.getDetalles()) {
            
            // 1. Obtener producto (ya validado anteriormente)
            cl.bosqueantiguo.ventas.client.ProductoDTO producto = productoClient.getProducto(detalleRequest.getProductoId());
            
            // 2. Calcular subtotal con precio real
            double subtotal = producto.getPrecio() * detalleRequest.getCantidad();
            
            // 3. Crear el detalle
            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setProductoId(detalleRequest.getProductoId());
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setSubtotal(subtotal);
            detalle.setBoleta(boleta);
            
            detalles.add(detalle);
            totalVenta += subtotal;

            // 4. Descontar stock del producto - Si falla, @Transactional hace rollback automático
            try {
                productoClient.reducirStock(detalleRequest.getProductoId(), detalleRequest.getCantidad());
            } catch (Exception e) {
                // El @Transactional hace rollback de la base de datos automáticamente
                throw new RuntimeException("Error al procesar la venta. Stock no pudo ser actualizado para: " + producto.getNombre(), e);
            }
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
        
        // --- LÓGICA DE NEGOCIO ADICIONAL ---
        // Aquí deberíamos llamar al microservicio de Productos para reponer el stock
        // de los productos en boleta.getDetalles()

        boletaRepository.delete(boleta);
    }
}
