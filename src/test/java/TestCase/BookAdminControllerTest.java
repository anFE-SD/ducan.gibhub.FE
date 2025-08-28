package TestCase;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookAdminControllerTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @Order(1)
    void testAccessBookList() {
        driver.get(BASE_URL + "/admin/book");
        Assertions.assertTrue(driver.getPageSource().contains("Quản Lý Sách"));
    }

    @Test
    @Order(2)
    void testAddNewBook() {
        driver.get(BASE_URL + "/admin/book/new");

        driver.findElement(By.name("title")).sendKeys("Sách Kiểm thử Java");
        driver.findElement(By.name("price")).sendKeys("100000");
        driver.findElement(By.name("stockQuantity")).sendKeys("50");

        // Chọn seller (nếu dropdown tồn tại)
        Select sellerSelect = new Select(driver.findElement(By.name("sellerId")));
        sellerSelect.selectByIndex(1);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/book"));
        Assertions.assertTrue(driver.getPageSource().contains("Sách Kiểm thử Java"));
    }

    @Test
    @Order(3)
    void testEditBook() {
        driver.get(BASE_URL + "/admin/book");

        // Giả sử có link chỉnh sửa với class .edit-book
        driver.findElement(By.cssSelector("a.edit-book")).click();

        WebElement titleField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("title")));
        titleField.clear();
        titleField.sendKeys("Sách Kiểm thử Java - Bản cập nhật");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/book"));
        Assertions.assertTrue(driver.getPageSource().contains("Sách Kiểm thử Java - Bản cập nhật"));
    }

    @Test
    @Order(4)
    void testDeleteBook() {
        driver.get(BASE_URL + "/admin/book");

        // Giả sử có nút xóa với class .delete-book
        driver.findElement(By.cssSelector("a.delete-book")).click();

        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException ignored) {}

        wait.until(ExpectedConditions.urlContains("/admin/book"));
        Assertions.assertFalse(driver.getPageSource().contains("Sách Kiểm thử Java - Bản cập nhật"));
    }
}
