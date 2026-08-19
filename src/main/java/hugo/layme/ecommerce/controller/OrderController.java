package hugo.layme.ecommerce.controller;

import hugo.layme.ecommerce.dto.order.OrderRequest;
import hugo.layme.ecommerce.dto.order.OrderResponse;
import hugo.layme.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(){

        return ResponseEntity.ok(orderService.getOrders());
    }
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<Void> payOrder(@PathVariable Long id){

        orderService.payOrder(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id){

        orderService.cancelOrder(id);

        return ResponseEntity.noContent().build();
    }

}
