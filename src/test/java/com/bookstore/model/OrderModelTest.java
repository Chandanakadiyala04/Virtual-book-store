package com.bookstore.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Test
public class OrderModelTest {

    private Order order;

    @BeforeMethod
    public void setUp() {
        order = new Order();
        order.setId("o1");
        order.setUserId("u1");
        order.setTotalAmount(59.98);
        order.setStatus("PENDING");
        order.setShippingAddress("123 Main St");
        order.setOrderDate(new Date());
    }

    @Test
    public void testOrderFields() {
        Assert.assertEquals(order.getId(), "o1");
        Assert.assertEquals(order.getUserId(), "u1");
        Assert.assertEquals(order.getTotalAmount(), 59.98);
        Assert.assertEquals(order.getStatus(), "PENDING");
        Assert.assertEquals(order.getShippingAddress(), "123 Main St");
        Assert.assertNotNull(order.getOrderDate());
    }

    @Test
    public void testStatusUpdate() {
        order.setStatus("DELIVERED");
        Assert.assertEquals(order.getStatus(), "DELIVERED");
    }

    @Test
    public void testOrderItems() {
        Order.OrderItem item = new Order.OrderItem("i1", "b1", "Clean Code", 2, 29.99);
        List<Order.OrderItem> items = new ArrayList<>();
        items.add(item);
        order.setItems(items);
        Assert.assertEquals(order.getItems().size(), 1);
        Assert.assertEquals(order.getItems().get(0).getTitle(), "Clean Code");
    }

    @Test
    public void testOrderItemFields() {
        Order.OrderItem item = new Order.OrderItem("i1", "b1", "Clean Code", 2, 29.99);
        Assert.assertEquals(item.getId(), "i1");
        Assert.assertEquals(item.getBookId(), "b1");
        Assert.assertEquals(item.getTitle(), "Clean Code");
        Assert.assertEquals(item.getQuantity(), 2);
        Assert.assertEquals(item.getPrice(), 29.99);
    }

    @Test
    public void testTotalAmountCalculation() {
        order.setTotalAmount(2 * 29.99);
        Assert.assertEquals(order.getTotalAmount(), 59.98, 0.001);
    }
}