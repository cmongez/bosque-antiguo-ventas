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
        return restTemplate.getForObject(url, ProductoDTO.class);
    }
    
    public void reducirStock(Long productoId, Integer cantidad) {
        String url = productosUrl + "/api/v1/products/" + productoId + "/reducir-stock?cantidad=" + cantidad;
        restTemplate.put(url, null);
    }
}