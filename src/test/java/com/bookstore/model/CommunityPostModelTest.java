package com.bookstore.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class CommunityPostModelTest {

    private CommunityPost post;

    @BeforeMethod
    public void setUp() {
        post = new CommunityPost();
        post.setId("p1");
        post.setUserId("u1");
        post.setUsername("alice");
        post.setTitle("Best books of 2024");
        post.setContent("Here are my top picks...");
    }

    @Test
    public void testPostFields() {
        Assert.assertEquals(post.getId(), "p1");
        Assert.assertEquals(post.getUserId(), "u1");
        Assert.assertEquals(post.getUsername(), "alice");
        Assert.assertEquals(post.getTitle(), "Best books of 2024");
        Assert.assertEquals(post.getContent(), "Here are my top picks...");
    }

    @Test
    public void testCreatedAtOnConstruction() {
        CommunityPost p = new CommunityPost();
        Assert.assertNotNull(p.getCreatedAt());
    }

    @Test
    public void testUpdateTitle() {
        post.setTitle("Updated Title");
        Assert.assertEquals(post.getTitle(), "Updated Title");
    }
}