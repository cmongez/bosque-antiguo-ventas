package cl.bosqueantiguo.ventas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductoClient {

    private final RestTemplate restTemplate;

    @Value("${microservicio.productos.url:http://localhost:8080}")
    private String productosUrl;

    public ProductoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductoDTO getProducto(Long id) {
        String url = productosUrl + "/api/v1/products/" + id;
        System.out.println("=== LLAMANDO A PRODUCTO CLIENT ===");
        System.out.println("URL: " + url);
        ProductoDTO producto = restTemplate.getForObject(url, ProductoDTO.class);
        System.out.println("Producto recibido: " + (producto != null ? producto.getNombre() : "null"));
        if (producto != null) {
            System.out.println("Nombre completo: [" + producto.getNombre() + "], longitud: " + producto.getNombre().length());
        }
        return producto;
    }

    public void reducirStock(Long productoId, Integer cantidad) {
        String url = productosUrl + "/api/v1/products/" + productoId + "/reducir-stock?cantidad=" + cantidad;
        System.out.println("=== REDUCIENDO STOCK ===");
        System.out.println("URL: " + url);
        try {
            restTemplate.put(url, null);
            System.out.println("Stock reducido exitosamente");
        } catch (Exception e) {
            System.err.println("Error al reducir stock: " + e.getMessage());
            throw e;
        }
    }
}
