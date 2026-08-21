package hugo.layme.ecommerce;

import hugo.layme.ecommerce.dto.order.OrderItemRequest;
import hugo.layme.ecommerce.dto.order.OrderRequest;
import hugo.layme.ecommerce.dto.order.OrderResponse;
import hugo.layme.ecommerce.entity.*;
import hugo.layme.ecommerce.exception.BusinessRuleException;
import hugo.layme.ecommerce.exception.ResourceNotFoundException;
import hugo.layme.ecommerce.repository.OrderRepository;
import hugo.layme.ecommerce.repository.ProductRepository;
import hugo.layme.ecommerce.security.UserDetailsImpl;
import hugo.layme.ecommerce.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderService orderService;

    private User user;

    @BeforeEach
    void setUp(){

        orderService = new OrderService(orderRepository, productRepository);

        user = new User("Test", "email@gmail.com", "password");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void afterTests(){

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return a list of order responses")
    void shouldReturnOrderResponseList(){

        Order order = new Order(user);
        List<Order> orders = List.of(order);

        when(orderRepository.findByUser(user)).thenReturn(orders);

        List<OrderResponse> result = orderService.getOrders();

        assertEquals(1, result.size());
        assertEquals(OrderStatus.PENDING, result.getFirst().status());
        assertEquals(BigDecimal.ZERO, result.getFirst().total());
        verify(orderRepository).findByUser(user);
    }

    @Test
    @DisplayName("Should create an order successfully")
    void shouldCreateAnOrderSuccessfully(){

       Product product = getTestProduct();
       ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

       when(productRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(product));

       OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(1L, 10)));

       OrderResponse response = orderService.createOrder(request);

       verify(orderRepository).save(orderCaptor.capture());
       Order savedOrder = orderCaptor.getValue();

       assertEquals(OrderStatus.PENDING, response.status());
       assertEquals(BigDecimal.valueOf(100), response.total());
       assertEquals(user, savedOrder.getUser());
       assertEquals(100, product.getStock());
       assertEquals(1, savedOrder.getOrderItems().size());
       assertEquals(10, savedOrder.getOrderItems().getFirst().getQuantity());
       assertEquals(product.getPrice(), savedOrder.getOrderItems().getFirst().getUnitPrice());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product does not exist")
    void shouldThrowResourceNotFoundExceptionWhenProductDoesNotExist(){

        when(productRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(99L, 10)));

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should pay an order successfully")
    void shouldPayAnOrderSuccessfully(){

        Long orderId = 1L;
        Order order = new Order(user);
        Product product = getTestProduct();
        OrderItem orderItem = new OrderItem(product, order, BigDecimal.TEN, 10);
        order.addItem(orderItem);
        
        when(orderRepository.findByIdAndUser(orderId, user )).thenReturn(Optional.of(order));

        orderService.payOrder(orderId);

        verify(orderRepository).findByIdAndUser(orderId, user);
        assertEquals(90, product.getStock());
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when try to pay an order with invalid id")
    void shouldThrowResourceNotFoundExceptionWhenTryToPayAnOrderWithInvalidId(){

        Long orderId = 99L;

        when(orderRepository.findByIdAndUser(orderId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.payOrder(orderId));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when try to pay a cancelled order")
    void shouldThrowBusinessRuleExceptionWhenTryToPayACancelledOrder(){

        Order order = new Order(user);
        order.cancel();

        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(BusinessRuleException.class,
                () -> orderService.payOrder(1L));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when try to pay an already paid order")
    void shouldThrowBusinessRuleExceptionWhenTryToPayAnAlreadyPaidOrder(){

        Order order = new Order(user);
        order.pay();

        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertThrows(BusinessRuleException.class,
                () -> orderService.payOrder(1L));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when product is inactive")
    void shouldThrowBusinessRuleExceptionWhenProductIsInactive(){

        Long orderId = 1L;
        Order order = new Order(user);
        Product product = getTestProduct();
        product.deactivate();
        OrderItem item = new OrderItem(product, order, product.getPrice(), 10);
        order.addItem(item);

        when(orderRepository.findByIdAndUser(orderId, user)).thenReturn(Optional.of(order));

        assertEquals(100, product.getStock());
        assertFalse(product.isActive());
        assertThrows(BusinessRuleException.class,
                () -> orderService.payOrder(orderId));
        assertEquals(100, product.getStock());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when item's quantity exceeds the product stock")
    void shouldThrowBusinessRuleExceptionWhenItemsQuantityExceedsTheProductStock(){

        Order order = new Order(user);
        Product product = getTestProduct();
        OrderItem item = new OrderItem(product, order, product.getPrice(), 999);
        order.addItem(item);

        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        assertThrows(BusinessRuleException.class,
                () -> orderService.payOrder(1L));
        assertEquals(100, product.getStock());
    }

    @Test
    @DisplayName("Should cancel an order successfully")
    void shouldCancelAnOrderSuccessfully(){

        Order order = new Order(user);

        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when try to cancel an order with invalid id")
    void shouldThrowResourceNotFoundExceptionWhenTryToCancelAnOrderWithInvalidId(){

        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.cancelOrder(1L));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when try to cancel a paid order")
    void shouldThrowBusinessExceptionWhenTryToCancelAPaidOrder(){

        Order order = new Order(user);
        order.pay();

        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        assertThrows(BusinessRuleException.class,
                () -> orderService.cancelOrder(1L));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when try to cancel an already cancelled order")
    void shouldThrowBusinessExceptionWhenTryToCancelAnAlreadyCancelledOrder(){

        Order order = new Order(user);
        order.cancel();

        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        assertThrows(BusinessRuleException.class,
                () -> orderService.cancelOrder(1L));
    }

    private Product getTestProduct(){
        return new Product("Test product", null, BigDecimal.TEN, 100);
    }

}
