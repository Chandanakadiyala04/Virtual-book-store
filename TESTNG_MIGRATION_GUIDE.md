# TestNG Migration Guide — Virtual Book Store Backend

## What's in this package

| File | Purpose |
|---|---|
| `pom.xml` | Updated Maven config — TestNG added, JUnit 5 excluded |
| `src/test/resources/testng.xml` | TestNG suite file — controls groups, ordering, parallelism |
| `src/test/java/.../model/BookModelTest.java` | Converted model test |
| `src/test/java/.../model/OrderModelTest.java` | Converted model test |
| `src/test/java/.../model/ReviewModelTest.java` | Converted model test |
| `src/test/java/.../dto/DTOTest.java` | Converted DTO test |
| `src/test/java/.../controller/BookControllerTest.java` | Converted controller test |
| `src/test/java/.../controller/ReviewControllerTest.java` | Converted controller test |

---

## Step 1 — Update pom.xml (3 changes)

### 1a. Exclude JUnit 5 from spring-boot-starter-test

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 1b. Add TestNG and mockito-testng

```xml
<!-- TestNG -->
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.9.0</version>
    <scope>test</scope>
</dependency>

<!-- Bridges Mockito to TestNG lifecycle -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-testng</artifactId>
    <version>0.5.2</version>
    <scope>test</scope>
</dependency>
```

### 1c. Configure maven-surefire-plugin to use testng.xml

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>src/test/resources/testng.xml</suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

---

## Step 2 — The import swap (every test file)

This is the most mechanical part. Do a find-and-replace across all test files:

| Remove (JUnit 5) | Add (TestNG) |
|---|---|
| `import org.junit.jupiter.api.Test;` | `import org.testng.annotations.Test;` |
| `import org.junit.jupiter.api.BeforeEach;` | `import org.testng.annotations.BeforeMethod;` |
| `import org.junit.jupiter.api.AfterEach;` | `import org.testng.annotations.AfterMethod;` |
| `import org.junit.jupiter.api.BeforeAll;` | `import org.testng.annotations.BeforeClass;` |
| `import org.junit.jupiter.api.AfterAll;` | `import org.testng.annotations.AfterClass;` |
| `import static org.junit.jupiter.api.Assertions.*;` | `import static org.testng.Assert.*;` |

And rename annotations accordingly:

| JUnit 5 annotation | TestNG annotation |
|---|---|
| `@BeforeEach` | `@BeforeMethod` |
| `@AfterEach` | `@AfterMethod` |
| `@BeforeAll` | `@BeforeClass` |
| `@AfterAll` | `@AfterClass` |

---

## Step 3 — Fix assertEquals argument order ⚠️ CRITICAL

This is the most common migration bug. The order of arguments is **reversed**:

```java
// JUnit 5:  assertEquals(expected, actual)
assertEquals(299.99, book.getPrice());

// TestNG:   assertEquals(actual, expected)
assertEquals(book.getPrice(), 299.99);
```

If you mix this up, your tests still compile and pass correctly — but failure
messages will be misleading (they'll print "expected X but was Y" backwards).

---

## Step 4 — Spring controller tests: extend AbstractTestNGSpringContextTests

Plain TestNG doesn't know how to load a Spring ApplicationContext. You must
extend this base class to make `@Autowired`, `@MockBean`, `@WebMvcTest` etc. work:

```java
// BEFORE (JUnit 5 — no base class needed)
@WebMvcTest(BookController.class)
public class BookControllerTest { ... }

// AFTER (TestNG — extend the Spring-TestNG bridge)
@WebMvcTest(BookController.class)
public class BookControllerTest extends AbstractTestNGSpringContextTests { ... }
```

Import: `org.springframework.test.context.testng.AbstractTestNGSpringContextTests`

This applies to **all** `@WebMvcTest`, `@SpringBootTest`, and `@DataJpaTest` classes.
Pure unit tests (model, DTO) do **not** need it.

---

## Step 5 — Test methods must be public

JUnit 5 allows package-private test methods. TestNG requires them to be `public`:

```java
// JUnit 5 — works fine
@Test
void testBookTitle() { ... }

// TestNG — must be public
@Test
public void testBookTitle() { ... }
```

---

## Step 6 — Create testng.xml

Place this at `src/test/resources/testng.xml`:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="VirtualBookStoreSuite" verbose="1">

    <test name="ModelTests">
        <groups><run><include name="unit"/></run></groups>
        <classes>
            <class name="com.bookstore.model.BookModelTest"/>
            <class name="com.bookstore.model.OrderModelTest"/>
            <class name="com.bookstore.model.ReviewModelTest"/>
            <class name="com.bookstore.dto.DTOTest"/>
        </classes>
    </test>

    <test name="ControllerTests">
        <groups><run><include name="controller"/></run></groups>
        <classes>
            <class name="com.bookstore.controller.BookControllerTest"/>
            <class name="com.bookstore.controller.AuthControllerTest"/>
            <class name="com.bookstore.controller.ReviewControllerTest"/>
        </classes>
    </test>

</suite>
```

Tag test classes with groups so you can run subsets:

```java
@Test(groups = "unit")        // model/DTO tests
@Test(groups = "controller")  // @WebMvcTest slice tests
```

---

## Running tests

```bash
# Run all tests
mvn test

# Run only unit tests (fast — no Spring context)
mvn test -Dgroups=unit

# Run only controller tests
mvn test -Dgroups=controller

# Run a single test class
mvn test -Dtest=BookModelTest
```

---

## TestNG features you can now use

### Data-driven tests with @DataProvider

```java
@DataProvider(name = "priceData")
public Object[][] priceProvider() {
    return new Object[][] {
        { 0.0,   true  },   // zero is valid
        { 100.0, true  },
        { -5.0,  false },   // negative is invalid
    };
}

@Test(dataProvider = "priceData")
public void testBookPriceValidation(double price, boolean expectedValid) {
    Book book = new Book();
    book.setPrice(price);
    assertEquals(book.getPrice() >= 0, expectedValid);
}
```

### Dependent tests with dependsOnMethods

```java
@Test
public void testCreateBook() { ... }

@Test(dependsOnMethods = "testCreateBook")
public void testUpdateBook() { ... }  // only runs if testCreateBook passes
```

### Expected exceptions

```java
// JUnit 5
assertThrows(IllegalArgumentException.class, () -> book.setPrice(-1));

// TestNG
@Test(expectedExceptions = IllegalArgumentException.class)
public void testNegativePriceThrows() {
    book.setPrice(-1);  // must throw, or test fails
}
```

---

## Complete migration checklist

- [ ] `pom.xml` — JUnit 5 excluded, TestNG + mockito-testng added, Surefire updated
- [ ] `testng.xml` created in `src/test/resources/`
- [ ] All `@Test` imports changed to `org.testng.annotations.Test`
- [ ] All `@BeforeEach` → `@BeforeMethod`, `@AfterEach` → `@AfterMethod`, etc.
- [ ] All `assertEquals` calls swapped to `(actual, expected)` order
- [ ] All test methods changed to `public`
- [ ] All `@WebMvcTest` / `@SpringBootTest` classes extend `AbstractTestNGSpringContextTests`
- [ ] `mvn test` passes with 0 failures
