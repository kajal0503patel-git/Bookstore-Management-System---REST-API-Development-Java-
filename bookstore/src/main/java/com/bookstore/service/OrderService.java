package com.bookstore.service;

import com.bookstore.dto.OrderRequest;
import com.bookstore.dto.OrderResponse;
import com.bookstore.exception.ApiException;
import com.bookstore.model.*;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<OrderResponse> getOrdersForUser(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return orderRepository.findByUser(user, pageable).map(this::toResponse);
    }

    public OrderResponse getOrderById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public OrderResponse placeOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Book book = bookRepository.findById(itemReq.getBookId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Book not found with id: " + itemReq.getBookId()));

            if (book.getStockQuantity() < itemReq.getQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for: " + book.getTitle()
                        + ". Available: " + book.getStockQuantity());
            }

            // Decrement stock
            book.setStockQuantity(book.getStockQuantity() - itemReq.getQuantity());
            bookRepository.save(book);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(book.getPrice());
            items.add(item);

            total = total.add(book.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }

        order.setItems(items);
        order.setTotalAmount(total);

        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        Order order = findOrThrow(id);
        try {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
        return toResponse(orderRepository.save(order));
    }

    private Order findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setCustomerName(order.getUser().getName());
        res.setCustomerEmail(order.getUser().getEmail());
        res.setStatus(order.getStatus().name());
        res.setPaymentStatus(order.getPaymentStatus().name());
        res.setTotalAmount(order.getTotalAmount());
        res.setCreatedAt(order.getCreatedAt());

        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
            OrderResponse.OrderItemResponse ir = new OrderResponse.OrderItemResponse();
            ir.setBookId(item.getBook().getId());
            ir.setBookTitle(item.getBook().getTitle());
            ir.setQuantity(item.getQuantity());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setSubtotal(item.getSubtotal());
            return ir;
        }).toList();

        res.setItems(itemResponses);
        return res;
    }
}
