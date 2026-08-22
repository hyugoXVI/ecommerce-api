package hugo.layme.ecommerce;

import hugo.layme.ecommerce.dto.product.ProductRequest;
import hugo.layme.ecommerce.dto.product.ProductResponse;
import hugo.layme.ecommerce.entity.Product;
import hugo.layme.ecommerce.exception.BusinessRuleException;
import hugo.layme.ecommerce.exception.ResourceNotFoundException;
import hugo.layme.ecommerce.repository.ProductRepository;
import hugo.layme.ecommerce.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should create a product successfully")
    void shouldCreateAProductSuccessfully(){

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        String name = "Test";
        ProductRequest request = new ProductRequest(name, null, BigDecimal.TEN, 100);

        when(productRepository.findByNameIgnoreCase(name)).thenReturn(Optional.empty());

        ProductResponse response = productService.createProduct(request);

        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertEquals(name, savedProduct.getName());
        assertEquals(BigDecimal.TEN, response.price());
        assertTrue(savedProduct.isActive());
        assertEquals(100, response.stock());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when try to create a product with an existing name")
    void shouldThrowBusinessRuleExceptionWhenTryToCreateAProductWithAnExistingName(){

        String name = "Test";
        Product product = new Product(name, null, BigDecimal.TEN, 100);
        ProductRequest request = new ProductRequest(name, "something", BigDecimal.TWO, 10);

        when(productRepository.findByNameIgnoreCase(name)).thenReturn(Optional.of(product));

        assertThrows(BusinessRuleException.class,
                () -> productService.createProduct(request));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should activate a product successfully")
    void shouldActivateAProductSuccessfully(){

        Product product = getTestProduct();
        product.deactivate();
        when(productRepository.findByIdAndActiveFalse(1L)).thenReturn(Optional.of(product));

        productService.activateProduct(1L);


        assertTrue(product.isActive());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when try to activate with an invalid product's id")
    void shouldThrowResourceNotFoundExceptionWhenTryToActivateWithAnInvalidProductsId(){

        when(productRepository.findByIdAndActiveFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.activateProduct(1L));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should deactivate a product successfully")
    void shouldDeactivateAProductSuccessfully(){

        Product product = getTestProduct();
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        when(productRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(product));

        productService.deactivateProduct(1L);

        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertFalse(savedProduct.isActive());
        assertEquals(product.getName(), savedProduct.getName());
        assertEquals(product.getStock(), savedProduct.getStock());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when try to deactivate with an invalid product's id")
    void shouldThrowResourceNotFoundExceptionWhenTryToDeactivateWithAnInvalidProductsId(){

        when(productRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.deactivateProduct(1L));
        verify(productRepository, never()).save(any(Product.class));

    }

    @Test
    @DisplayName("Should return a list of active products")
    void shouldReturnAListOfActiveProducts(){

        when(productRepository.findByActiveTrue()).thenReturn(List.of(getTestProduct()));

        List<ProductResponse> result = productService.getActiveProducts();

        assertEquals(1, result.size());
        assertTrue(result.getFirst().active());
        verify(productRepository).findByActiveTrue();
        }

    @Test
    @DisplayName("Should return an active product")
    void shouldReturnAnActiveProductById(){

        Product product = getTestProduct();

        when(productRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getActiveProductById(1L);

        assertEquals(product.getName(), response.name());
        assertEquals(product.getPrice(), response.price());
        assertTrue(response.active());
        verify(productRepository).findByIdAndActiveTrue(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when try to return a product with invalid id")
    void shouldThrowResourceNotFoundExceptionWhenTryToReturnAProductWithInvalidId(){

        when(productRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getActiveProductById(99L));
    }

    private Product getTestProduct(){

        return new Product("Test", null, BigDecimal.TEN, 100);
    }
}
