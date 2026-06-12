package com.bookstore.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.LocalDateTime;

@Test
public class PasswordResetTokenModelTest {

    private PasswordResetToken token;

    @BeforeMethod
    public void setUp() {
        token = new PasswordResetToken("abc123", "user@example.com", LocalDateTime.now().plusHours(1));
    }

    @Test
    public void testConstructorFields() {
        Assert.assertEquals(token.getToken(), "abc123");
        Assert.assertEquals(token.getEmail(), "user@example.com");
        Assert.assertNotNull(token.getExpiryDate());
    }

    @Test
    public void testExpiryDateInFuture() {
        Assert.assertTrue(token.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testSetUserId() {
        token.setUserId("u1");
        Assert.assertEquals(token.getUserId(), "u1");
    }
}