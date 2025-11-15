package cl.bosqueantiguo.ventas.controller;

import cl.bosqueantiguo.ventas.dto.VentaRequestDTO;
import cl.bosqueantiguo.ventas.dto.VentaResponseDTO;
import cl.bosqueantiguo.ventas.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales") 
@CrossOrigin(origins = "*") // Permite CORS 
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // POST /api/v1/sales
    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrarVenta(@RequestBody VentaRequestDTO ventaRequest) {
        VentaResponseDTO ventaRegistrada = ventaService.registrarVenta(ventaRequest);
        return new ResponseEntity<>(ventaRegistrada, HttpStatus.CREATED);
    }

    // GET /api/v1/sales
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        // TODO: Proteger este endpoint (solo Admin)
        List<VentaResponseDTO> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }

    // GET /api/v1/sales/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Long id) {
        VentaResponseDTO venta = ventaService.obtenerVentaPorId(id);
        return ResponseEntity.ok(venta);
    }

    // GET /api/v1/sales/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VentaResponseDTO>> listarVentasPorUsuario(@PathVariable Long userId) {
        List<VentaResponseDTO> ventas = ventaService.listarVentasPorUsuario(userId);
        return ResponseEntity.ok(ventas);
    }

    // DELETE /api/v1/sales/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> anularBoleta(@PathVariable Long id) {
        // TODO: Proteger este endpoint (solo Admin)
        ventaService.anularVenta(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}