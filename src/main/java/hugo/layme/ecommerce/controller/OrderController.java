package hugo.layme.ecommerce.controller;

import hugo.layme.ecommerce.dto.order.OrderRequest;
import hugo.layme.ecommerce.dto.order.OrderResponse;
import hugo.layme.ecommerce.service.OrderService;
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
    public List<OrderResponse> getOrders(){
        return orderService.getOrders();
    }
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request){
        return orderService.createOrder(request);
    }

}
