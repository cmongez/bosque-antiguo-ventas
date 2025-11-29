package cl.bosqueantiguo.ventas.controller;

import cl.bosqueantiguo.ventas.dto.VentaRequestDTO;
import cl.bosqueantiguo.ventas.dto.VentaResponseDTO;
import cl.bosqueantiguo.ventas.service.VentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales") 
@CrossOrigin(origins = "*") // Permite CORS
@Tag(name = "Ventas", description = "Gestión de órdenes y ventas")
@SecurityRequirement(name = "bearerAuth") 
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Operation(summary = "Registrar nueva venta", description = "Crea una nueva orden de venta con sus detalles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Venta registrada exitosamente",
            content = @Content(schema = @Schema(implementation = VentaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de venta inválidos")
    })
    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrarVenta(
            @RequestBody VentaRequestDTO ventaRequest,
            Authentication authentication) {
        
        // Extraer userId del JWT
        Long userId = Long.parseLong(authentication.getDetails().toString());
        ventaRequest.setUsuarioId(userId);
        
        VentaResponseDTO ventaRegistrada = ventaService.registrarVenta(ventaRequest);
        return new ResponseEntity<>(ventaRegistrada, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas las ventas", description = "Obtiene todas las ventas registradas - Solo ADMIN y VENDEDOR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = VentaResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo ADMIN y VENDEDOR")
    })
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas(Authentication authentication) {
        // Solo ADMIN y VENDEDOR pueden ver todas las ventas
        boolean isAdminOrVendedor = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_VENDEDOR"));
        
        if (!isAdminOrVendedor) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<VentaResponseDTO> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }

    @Operation(summary = "Obtener venta por ID", description = "Obtiene una venta específica - ADMIN/VENDEDOR ven todo, CLIENTE solo sus ventas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta encontrada",
            content = @Content(schema = @Schema(implementation = VentaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Long id, Authentication authentication) {
        VentaResponseDTO venta = ventaService.obtenerVentaPorId(id);
        
        // ADMIN y VENDEDOR pueden ver cualquier venta
        boolean isAdminOrVendedor = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_VENDEDOR"));
        
        if (!isAdminOrVendedor) {
            // CLIENTE solo puede ver sus propias ventas
            Long currentUserId = Long.parseLong(authentication.getDetails().toString());
            if (!currentUserId.equals(venta.getUsuarioId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        return ResponseEntity.ok(venta);
    }

    @Operation(summary = "Listar ventas por usuario", description = "Obtiene todas las ventas de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de ventas del usuario",
            content = @Content(schema = @Schema(implementation = VentaResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VentaResponseDTO>> listarVentasPorUsuario(
            @PathVariable Long userId,
            Authentication authentication) {
        
        // Verificar que el usuario solo pueda ver sus propias ventas (excepto ADMIN)
        Long currentUserId = Long.parseLong(authentication.getDetails().toString());
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !currentUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<VentaResponseDTO> ventas = ventaService.listarVentasPorUsuario(userId);
        return ResponseEntity.ok(ventas);
    }
    
    @Operation(summary = "Listar mis compras", description = "Obtiene el historial de compras del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial de compras obtenido",
            content = @Content(schema = @Schema(implementation = VentaResponseDTO.class)))
    })
    @GetMapping("/mis-compras")
    public ResponseEntity<List<VentaResponseDTO>> listarMisCompras(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getDetails().toString());
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
