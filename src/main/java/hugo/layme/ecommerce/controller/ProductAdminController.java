package hugo.layme.ecommerce.controller;

import hugo.layme.ecommerce.dto.product.ProductRequest;
import hugo.layme.ecommerce.dto.product.ProductResponse;
import hugo.layme.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponse createProduct(@RequestBody @Valid ProductRequest request){
        return productService.createProduct(request);
    }

    @PatchMapping("/{id}/deactivate")
    public void deactivateProduct(@PathVariable Long id){
        productService.deactivateProduct(id);
    }

    @PatchMapping("/{id}/activate")
    public void activateProduct(@PathVariable Long id){
        productService.activateProduct(id);
    }

}
