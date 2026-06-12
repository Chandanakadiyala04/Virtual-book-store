package com.bookstore.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class BookModelTest {

    private Book book;

    @BeforeMethod
    public void setUp() {
        book = new Book("1", "Clean Code", "Robert Martin", "A book about clean code",
                "Technology", 29.99, 100, "seller1", "http://image.url");
    }

    @Test
    public void testBookConstructor() {
        Assert.assertEquals(book.getId(), "1");
        Assert.assertEquals(book.getTitle(), "Clean Code");
        Assert.assertEquals(book.getAuthor(), "Robert Martin");
        Assert.assertEquals(book.getDescription(), "A book about clean code");
        Assert.assertEquals(book.getCategory(), "Technology");
        Assert.assertEquals(book.getPrice(), 29.99);
        Assert.assertEquals(book.getStock(), 100);
        Assert.assertEquals(book.getSellerId(), "seller1");
        Assert.assertEquals(book.getImageUrl(), "http://image.url");
    }

    @Test
    public void testDefaultConstructor() {
        Book emptyBook = new Book();
        Assert.assertNull(emptyBook.getId());
        Assert.assertNull(emptyBook.getTitle());
        Assert.assertEquals(emptyBook.getPrice(), 0.0);
        Assert.assertEquals(emptyBook.getStock(), 0);
    }

    @Test
    public void testSettersAndGetters() {
        book.setId("99");
        book.setTitle("Refactoring");
        book.setAuthor("Martin Fowler");
        book.setDescription("Improving design");
        book.setCategory("Software");
        book.setPrice(39.99);
        book.setStock(50);
        book.setSellerId("seller2");
        book.setImageUrl("http://new.url");
        book.setIsbn("978-0201485677");

        Assert.assertEquals(book.getId(), "99");
        Assert.assertEquals(book.getTitle(), "Refactoring");
        Assert.assertEquals(book.getAuthor(), "Martin Fowler");
        Assert.assertEquals(book.getDescription(), "Improving design");
        Assert.assertEquals(book.getCategory(), "Software");
        Assert.assertEquals(book.getPrice(), 39.99);
        Assert.assertEquals(book.getStock(), 50);
        Assert.assertEquals(book.getSellerId(), "seller2");
        Assert.assertEquals(book.getImageUrl(), "http://new.url");
        Assert.assertEquals(book.getIsbn(), "978-0201485677");
    }

    @Test
    public void testIsbn() {
        Assert.assertNull(book.getIsbn());
        book.setIsbn("978-0132350884");
        Assert.assertEquals(book.getIsbn(), "978-0132350884");
    }
}