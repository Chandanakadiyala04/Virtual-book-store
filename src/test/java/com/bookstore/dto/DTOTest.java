package com.bookstore.dto;

import java.util.Arrays;
import java.util.HashSet;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DTOTest {

    @Test
    public void testLoginRequest() {
        LoginRequest req = new LoginRequest();
        req.setUsername("john");
        req.setPassword("pass123");
        Assert.assertEquals(req.getUsername(), "john");
        Assert.assertEquals(req.getPassword(), "pass123");
    }

    @Test
    public void testSignupRequest() {
        SignupRequest req = new SignupRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");
        req.setPassword("secure123");
        req.setRoles(new HashSet(Arrays.asList("ROLE_USER")));
        Assert.assertEquals(req.getUsername(), "alice");
        Assert.assertEquals(req.getEmail(), "alice@example.com");
        Assert.assertEquals(req.getPassword(), "secure123");
        Assert.assertTrue(req.getRoles().contains("ROLE_USER"));
    }

    @Test
    public void testJwtResponse() {
        JwtResponse res = new JwtResponse("token123", "id1", "john",
                "john@test.com", Arrays.asList("ROLE_USER"));
        Assert.assertEquals(res.getToken(), "token123");
        Assert.assertEquals(res.getType(), "Bearer");
        Assert.assertEquals(res.getId(), "id1");
        Assert.assertEquals(res.getUsername(), "john");
        Assert.assertEquals(res.getEmail(), "john@test.com");
        Assert.assertTrue(res.getRoles().contains("ROLE_USER"));
    }

    @Test
    public void testMessageResponse() {
        MessageResponse res = new MessageResponse("Success!");
        Assert.assertEquals(res.getMessage(), "Success!");
    }

    @Test
    public void testResetPasswordRequest() {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("resettoken123");
        req.setNewPassword("newpass456");
        Assert.assertEquals(req.getToken(), "resettoken123");
        Assert.assertEquals(req.getNewPassword(), "newpass456");
    }
}