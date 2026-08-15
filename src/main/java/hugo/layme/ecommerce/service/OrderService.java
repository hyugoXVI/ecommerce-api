package hugo.layme.ecommerce.service;

import hugo.layme.ecommerce.dto.order.OrderItemRequest;
import hugo.layme.ecommerce.dto.order.OrderItemResponse;
import hugo.layme.ecommerce.dto.order.OrderRequest;
import hugo.layme.ecommerce.dto.order.OrderResponse;
import hugo.layme.ecommerce.entity.Order;
import hugo.layme.ecommerce.entity.OrderItem;
import hugo.layme.ecommerce.entity.Product;
import hugo.layme.ecommerce.entity.User;
import hugo.layme.ecommerce.repository.OrderRepository;
import hugo.layme.ecommerce.repository.ProductRepository;
import hugo.layme.ecommerce.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<OrderResponse> getOrders(){
        User user = getAuthenticatedUser();

        return orderRepository.findByUser(user).stream()
                .map(this::orderToResponse)
                .toList();
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request){

        User user = getAuthenticatedUser();
        Order order = new Order(user);

        List<OrderItemRequest> itemsList = request.items();

        if (itemsList == null || itemsList.isEmpty()){
            throw new RuntimeException("Items cannot be null/empty!");
        }

        itemsList.forEach(item -> {
            Product product = productRepository.findByIdAndActiveTrue(item.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found."));
                    product.decreaseStock(item.quantity());

                    order.addItem(new OrderItem(product, order, product.getPrice(),item.quantity()));
        });

        orderRepository.save(order);
        return orderToResponse(order);
    }

    private User getAuthenticatedUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetailsImpl userDetails){

            return userDetails.getUser();
        }

        throw new RuntimeException("User not authenticated.");
    }

    private OrderResponse orderToResponse(Order order){

        return new OrderResponse(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getTotal(), order.getStatus(), order.getCreatedAt(),
                order.getOrderItems().stream().map(this::orderItemToResponse).toList());
    }

    private OrderItemResponse orderItemToResponse(OrderItem orderItem){

        return new OrderItemResponse(orderItem.getId(), orderItem.getProduct().getName(), orderItem.getUnitPrice(),
                orderItem.getQuantity(), orderItem.getSubtotal());
    }
}
