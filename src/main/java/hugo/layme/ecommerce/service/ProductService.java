package hugo.layme.ecommerce.service;

import hugo.layme.ecommerce.dto.product.ProductRequest;
import hugo.layme.ecommerce.dto.product.ProductResponse;
import hugo.layme.ecommerce.entity.Product;
import hugo.layme.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest request){
        String name = request.name().trim();

        productRepository.findByNameIgnoreCase(name).ifPresent(p -> {
            throw new RuntimeException("The product's name already exists in database.");
        });

        Product savedProduct = new Product(name, request.description(),
                request.price(), request.stock());

        productRepository.save(savedProduct);

        return productToResponse(savedProduct);
    }

    public void deactivateProduct(Long id){

        productRepository.findByIdAndActiveTrue(id)
                .ifPresentOrElse(p -> {
                    p.deactivate();
                    productRepository.save(p);
                }, () -> {
                    throw new RuntimeException("Product doesn't exist.");
                });
    }

    public List<ProductResponse> getActiveProducts(){

        return productRepository.findByActiveTrue().stream()
                .map(this::productToResponse)
                .toList();
    }

    public ProductResponse getActiveProductById(Long id){

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Product doesn't exist!"));

        return productToResponse(product);
    }

    private ProductResponse productToResponse(Product product){
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getStock(), product.isActive());
    }
}
