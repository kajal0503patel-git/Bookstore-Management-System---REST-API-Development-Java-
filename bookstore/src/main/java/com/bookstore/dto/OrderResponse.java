package com.bookstore.dto;

import com.bookstore.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String customerName;
    private String customerEmail;
    private List<OrderItemResponse> items;
    private String status;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    @Data
    public static class OrderItemResponse {
        private Long bookId;
        private String bookTitle;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}
