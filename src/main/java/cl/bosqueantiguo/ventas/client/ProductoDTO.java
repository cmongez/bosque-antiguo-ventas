package cl.bosqueantiguo.ventas.client;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    private Integer stock;
    private Integer stockCritico;
    private Boolean disponible;
    private CategoriaDTO categoria;
    
    @Data
    public static class CategoriaDTO {
        private Long id;
        private String nombre;
    }
}