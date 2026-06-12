package com.bookstore.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.HashSet;
import java.util.Set;

@Test
public class UserModelTest {

    private User user;

    @BeforeMethod
    public void setUp() {
        user = new User();
        user.setId("u1");
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("password123");
    }

    @Test
    public void testUserFields() {
        Assert.assertEquals(user.getId(), "u1");
        Assert.assertEquals(user.getUsername(), "john");
        Assert.assertEquals(user.getEmail(), "john@example.com");
        Assert.assertEquals(user.getPassword(), "password123");
    }

    @Test
    public void testAddRole() {
        user.getRoles().add("ROLE_USER");
        Assert.assertTrue(user.getRoles().contains("ROLE_USER"));
        Assert.assertEquals(user.getRoles().size(), 1);
    }

    @Test
    public void testMultipleRoles() {
        user.getRoles().add("ROLE_USER");
        user.getRoles().add("ROLE_ADMIN");
        Assert.assertEquals(user.getRoles().size(), 2);
    }

    @Test
    public void testSetRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        user.setRoles(roles);
        Assert.assertEquals(user.getRoles().size(), 1);
        Assert.assertTrue(user.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    public void testAddFavorite() {
        user.getFavorites().add("book1");
        user.getFavorites().add("book2");
        Assert.assertEquals(user.getFavorites().size(), 2);
        Assert.assertTrue(user.getFavorites().contains("book1"));
    }

    @Test
    public void testAllArgsConstructor() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        Set<String> favs = new HashSet<>();
        favs.add("book1");
        User u = new User("id1", "alice", "alice@test.com", "pass", roles, favs);
        Assert.assertEquals(u.getId(), "id1");
        Assert.assertEquals(u.getUsername(), "alice");
        Assert.assertEquals(u.getEmail(), "alice@test.com");
    }
}