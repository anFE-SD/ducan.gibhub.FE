package TestCase;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublisherAdminControllerTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String SCREENSHOT_DIR = "screenshots/publisher";

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--start-maximized"));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        new File(SCREENSHOT_DIR).mkdirs();
    }

    @AfterAll
    static void teardown() {
        if (driver != null) driver.quit();
    }

    private void takeScreenshot(String name) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String filename = String.format("%s/%s_%s.png", SCREENSHOT_DIR, name,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            FileUtils.copyFile(src, new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void hienThiDanhSachPublisher() {
        driver.get(BASE_URL + "/admin/publisher");
        takeScreenshot("01_danh_sach");

        WebElement heading = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
        Assertions.assertTrue(heading.getText().contains("Nhà xuất bản") || heading.getText().contains("Publisher"));
    }

    @Test
    @Order(2)
    void themMoiPublisher_HopLe() {
        driver.get(BASE_URL + "/admin/publisher/new");

        String uniqueName = "NXB Test " + System.currentTimeMillis();
        driver.findElement(By.name("name")).sendKeys(uniqueName);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/publisher"));
        takeScreenshot("02_them_thanh_cong");

        Assertions.assertTrue(driver.getPageSource().contains(uniqueName), "Tên nhà xuất bản mới phải xuất hiện trong danh sách");
    }

    @Test
    @Order(3)
    void themMoiPublisher_TenRong_ThatBai() {
        driver.get(BASE_URL + "/admin/publisher/new");

        driver.findElement(By.name("name")).sendKeys("");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/publisher/new"));
        takeScreenshot("03_ten_rong");

        Assertions.assertTrue(driver.getPageSource().contains("không được để trống"), "Phải hiển thị lỗi tên bị trống");
    }

    @Test
    @Order(4)
    void chinhSuaPublisher() {
        driver.get(BASE_URL + "/admin/publisher");
        WebElement nutSua = driver.findElement(By.cssSelector("a[href*='/edit/']")); // Lấy dòng đầu
        nutSua.click();

        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("name")));
        input.clear();
        input.sendKeys("NXB Đã Sửa");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/publisher"));
        takeScreenshot("04_sua");

        Assertions.assertTrue(driver.getPageSource().contains("NXB Đã Sửa"), "Tên nhà xuất bản đã sửa phải được hiển thị");
    }

    @Test
    @Order(5)
    void xoaMemPublisher() {
        driver.get(BASE_URL + "/admin/publisher");

        WebElement nutXoa = driver.findElement(By.cssSelector("a[href*='/delete/']")); // giả định có
        String href = nutXoa.getAttribute("href");
        nutXoa.click();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/admin/publisher"));
        takeScreenshot("05_xoa_mem");

        Assertions.assertFalse(driver.getPageSource().contains(href), "NXB bị ẩn không nên hiển thị sau khi xóa mềm");
    }
}
