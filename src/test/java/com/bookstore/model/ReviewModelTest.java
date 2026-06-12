package com.bookstore.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class ReviewModelTest {

    private Review review;

    @BeforeMethod
    public void setUp() {
        review = new Review();
        review.setId("r1");
        review.setBookId("b1");
        review.setUserId("u1");
        review.setUsername("john");
        review.setRating(5);
        review.setComment("Excellent book!");
    }

    @Test
    public void testReviewFields() {
        Assert.assertEquals(review.getId(), "r1");
        Assert.assertEquals(review.getBookId(), "b1");
        Assert.assertEquals(review.getUserId(), "u1");
        Assert.assertEquals(review.getUsername(), "john");
        Assert.assertEquals(review.getRating(), 5);
        Assert.assertEquals(review.getComment(), "Excellent book!");
    }

    @Test
    public void testRatingRange() {
        for (int i = 1; i <= 5; i++) {
            review.setRating(i);
            Assert.assertEquals(review.getRating(), i);
        }
    }

    @Test
    public void testCreatedAtSetOnConstruction() {
        Review r = new Review();
        Assert.assertNotNull(r.getCreatedAt());
    }
}