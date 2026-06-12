package com.bookstore.security;

import com.bookstore.model.User;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Test
public class UserDetailsImplTest {

    private User user;

    @BeforeMethod
    public void setUp() {
        user = new User();
        user.setId("u1");
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("hashedpassword");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
    }

    @Test
    public void testBuildFromUser() {
        UserDetailsImpl details = UserDetailsImpl.build(user);
        Assert.assertEquals(details.getId(), "u1");
        Assert.assertEquals(details.getUsername(), "john");
        Assert.assertEquals(details.getEmail(), "john@example.com");
        Assert.assertEquals(details.getPassword(), "hashedpassword");
    }

    @Test
    public void testAuthorities() {
        UserDetailsImpl details = UserDetailsImpl.build(user);
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        Assert.assertEquals(authorities.size(), 1);
        Assert.assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void testMultipleRoles() {
        user.getRoles().add("ROLE_ADMIN");
        UserDetailsImpl details = UserDetailsImpl.build(user);
        Assert.assertEquals(details.getAuthorities().size(), 2);
    }

    @Test
    public void testIsEnabled() {
        UserDetailsImpl details = UserDetailsImpl.build(user);
        Assert.assertTrue(details.isEnabled());
    }

    @Test
    public void testEqualsWithSameId() {
        UserDetailsImpl d1 = UserDetailsImpl.build(user);
        UserDetailsImpl d2 = UserDetailsImpl.build(user);
        Assert.assertEquals(d1, d2);
    }

    @Test
    public void testEqualsWithDifferentId() {
        User user2 = new User();
        user2.setId("u2");
        user2.setUsername("jane");
        user2.setEmail("jane@example.com");
        user2.setPassword("pass");
        user2.setRoles(new HashSet<>());

        UserDetailsImpl d1 = UserDetailsImpl.build(user);
        UserDetailsImpl d2 = UserDetailsImpl.build(user2);
        Assert.assertNotEquals(d1, d2);
    }
}