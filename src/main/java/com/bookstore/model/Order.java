package com.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;

    private double totalAmount;
    private String status;

    @Column(length = 500)
    private String shippingAddress;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "order_items")
    public static class OrderItem {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;

        private String bookId;
        private String title;
        private int quantity;
        private double price;
    }
}
