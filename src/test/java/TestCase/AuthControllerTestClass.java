package TestCase;

import org.junit.jupiter.api.*;          
import org.openqa.selenium.*;           
import org.openqa.selenium.chrome.*;     
import org.openqa.selenium.support.ui.*; 
import org.apache.commons.io.FileUtils;  
import io.github.bonigarcia.wdm.*;       

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTestClass {
    
    private static WebDriver trieuDongWeb;
    private static WebDriverWait choDoiWeb;
    private static final String DUONG_DAN_CO_SO = "http://localhost:8080"; 
    private static final String THU_MUC_CHUP_MAN_HINH = "anh-chup-kiem-thu";
    
    @BeforeAll
    static void caiDatBanDau() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions tuyChonChrome = new ChromeOptions();
        tuyChonChrome.addArguments("--start-maximized");         
        tuyChonChrome.addArguments("--disable-blink-features=AutomationControlled");

        
        trieuDongWeb = new ChromeDriver(tuyChonChrome);
        choDoiWeb = new WebDriverWait(trieuDongWeb, Duration.ofSeconds(10));
        
        new File(THU_MUC_CHUP_MAN_HINH).mkdirs();
    }
    
    @AfterAll
    static void dongDonSau() {
        if (trieuDongWeb != null) {
            trieuDongWeb.quit();  
        }
    }
    
    private void chupManHinh(String tenKiemThu) {
        try {
            TakesScreenshot chupAnh = (TakesScreenshot) trieuDongWeb;
            File tepNguon = chupAnh.getScreenshotAs(OutputType.FILE);
            
            String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String tenTep = String.format("%s/%s_%s.png", THU_MUC_CHUP_MAN_HINH, tenKiemThu, thoiGian);
            
            FileUtils.copyFile(tepNguon, new File(tenTep));
            System.out.println("Đã lưu ảnh chụp màn hình: " + tenTep);
        } catch (IOException e) {
            System.err.println("Lỗi khi chụp màn hình: " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    void kiemThuHienThiFormDangNhap() {
        System.out.println("=== KIỂM THỬ 1: Hiển Thị Form Đăng Nhập ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        chupManHinh("01_form_dang_nhap");
        
        WebElement truongTenDangNhap = choDoiWeb.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        WebElement truongMatKhau = trieuDongWeb.findElement(By.name("password"));
        WebElement nutDangNhap = trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']"));
        
        Assertions.assertTrue(truongTenDangNhap.isDisplayed(), "Trường tên đăng nhập phải hiển thị");
        Assertions.assertTrue(truongMatKhau.isDisplayed(), "Trường mật khẩu phải hiển thị");
        Assertions.assertTrue(nutDangNhap.isDisplayed(), "Nút đăng nhập phải hiển thị");
        
        System.out.println("✅ Form đăng nhập hiển thị thành công");
    }
    
    @Test
    @Order(2)
    void kiemThuDangNhap_ThongTinHopLe_ThanhCong() {
        System.out.println("=== KIỂM THỬ 2: Đăng Nhập Hợp Lệ ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        chupManHinh("02_truoc_dang_nhap_hop_le");
        
        // Nhập thông tin đăng nhập hợp lệ
        trieuDongWeb.findElement(By.name("username")).sendKeys("levanan"); 
        trieuDongWeb.findElement(By.name("password")).sendKeys("12345678"); 
        
        chupManHinh("02_da_dien_form_dang_nhap");
        
        // Nhấn đăng nhập
        trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();
        
        // Chờ chuyển hướng
        choDoiWeb.until(ExpectedConditions.urlContains("dashboard"));
        chupManHinh("02_sau_dang_nhap_hop_le");
        
        // Xác minh chuyển hướng đến dashboard
        String duongDanHienTai = trieuDongWeb.getCurrentUrl();
        Assertions.assertTrue(duongDanHienTai.contains("dashboard") || duongDanHienTai.contains("home"), 
                             "Phải chuyển hướng đến dashboard sau khi đăng nhập thành công");
        
        System.out.println("✅ Đăng nhập hợp lệ thành công, chuyển hướng đến: " + duongDanHienTai);
    }
    
    @Test
    @Order(3)
    void kiemThuDangNhap_ThongTinKhongHopLe_ThatBai() {
        System.out.println("=== KIỂM THỬ 3: Đăng Nhập Không Hợp Lệ ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        chupManHinh("03_truoc_dang_nhap_sai");
        
        // Nhập thông tin sai
        trieuDongWeb.findElement(By.name("username")).sendKeys("nguoidungsai");
        trieuDongWeb.findElement(By.name("password")).sendKeys("matkhausai");
        
        chupManHinh("03_da_dien_thong_tin_sai");
        
        // Nhấn đăng nhập
        trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();
        
        // Chờ thông báo lỗi
        try {
            WebElement thongBaoLoi = choDoiWeb.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".error, .alert-danger, .text-danger, [class*='error']")));
            chupManHinh("03_sau_dang_nhap_sai");
            
            Assertions.assertTrue(thongBaoLoi.isDisplayed(), "Thông báo lỗi phải được hiển thị");
            System.out.println("✅ Đăng nhập không hợp lệ đã bị từ chối với lỗi: " + thongBaoLoi.getText());
        } catch (TimeoutException e) {
            chupManHinh("03_sau_dang_nhap_sai_khong_co_loi");
            // Kiểm tra xem có vẫn ở trang đăng nhập không
            Assertions.assertTrue(trieuDongWeb.getCurrentUrl().contains("login"), 
                                 "Phải ở lại trang đăng nhập sau khi nhập thông tin sai");
            System.out.println("✅ Đăng nhập không hợp lệ bị từ chối (vẫn ở trang đăng nhập)");
        }
    }
    
    @Test
    @Order(4)
    void kiemThuHienThiFormDangKy() {
        System.out.println("=== KIỂM THỬ 4: Hiển Thị Form Đăng Ký ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/register");
        chupManHinh("04_form_dang_ky");
        
        // Xác minh các thành phần form đăng ký
        WebElement truongTenDangNhap = choDoiWeb.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        WebElement truongMatKhau = trieuDongWeb.findElement(By.name("password"));
        WebElement nutDangKy = trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']"));
        
        Assertions.assertTrue(truongTenDangNhap.isDisplayed(), "Trường tên đăng nhập phải hiển thị");
        Assertions.assertTrue(truongMatKhau.isDisplayed(), "Trường mật khẩu phải hiển thị");
        Assertions.assertTrue(nutDangKy.isDisplayed(), "Nút đăng ký phải hiển thị");
        
        System.out.println("✅ Form đăng ký hiển thị thành công");
    }
    
    @Test
    @Order(5)
    void kiemThuDangKy_DuLieuHopLe_ThanhCong() throws InterruptedException {
        System.out.println("=== KIỂM THỬ 5: Đăng Ký Hợp Lệ ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/register");
        chupManHinh("05_truoc_dang_ky");
        
        // Tạo tên đăng nhập duy nhất
        String tenDangNhapDuyNhat = "nguoidungkiem_" + System.currentTimeMillis();
        
        // Nhập thông tin đăng ký
        trieuDongWeb.findElement(By.name("username")).sendKeys(tenDangNhapDuyNhat);
        trieuDongWeb.findElement(By.name("password")).sendKeys("matkhau123");
        
        // Nếu có trường xác nhận mật khẩu
        try {
            trieuDongWeb.findElement(By.name("confirmPassword")).sendKeys("matkhau123");
        } catch (NoSuchElementException e) {
            // Không có trường xác nhận mật khẩu
        }
        
        chupManHinh("05_da_dien_form_dang_ky");
        
        // Nhấn đăng ký
        trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();
        
        // Chờ chuyển hướng hoặc thông báo thành công
        Thread.sleep(2000); // Chờ xử lý
        chupManHinh("05_sau_dang_ky");
        
        String duongDanHienTai = trieuDongWeb.getCurrentUrl();
        boolean dangKyThanhCong = duongDanHienTai.contains("login") || duongDanHienTai.contains("success") || 
                                 duongDanHienTai.contains("dashboard");
        
        Assertions.assertTrue(dangKyThanhCong, "Phải chuyển hướng sau khi đăng ký thành công");
        System.out.println("✅ Đăng ký thành công, chuyển hướng đến: " + duongDanHienTai);
    }
    
    @Test
    @Order(6)
    void kiemThuDangXuat_ThanhCong() throws InterruptedException {
        System.out.println("=== KIỂM THỬ 6: Đăng Xuất ===");
        
        // Đăng nhập trước
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        trieuDongWeb.findElement(By.name("username")).sendKeys("admin");
        trieuDongWeb.findElement(By.name("password")).sendKeys("matkhau123");
        trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();
        
        Thread.sleep(2000);
        chupManHinh("06_truoc_dang_xuat");
        
        // Tìm và nhấn đăng xuất
        try {
            WebElement nutDangXuat = trieuDongWeb.findElement(By.linkText("Đăng xuất"));
            nutDangXuat.click();
        } catch (NoSuchElementException e) {
            // Thử các selector khác
            try {
                trieuDongWeb.findElement(By.cssSelector("a[href*='logout']")).click();
            } catch (NoSuchElementException ex) {
                trieuDongWeb.get(DUONG_DAN_CO_SO + "/logout"); // Đăng xuất trực tiếp
            }
        }
        
        // Chờ chuyển hướng về đăng nhập
        choDoiWeb.until(ExpectedConditions.urlContains("login"));
        chupManHinh("06_sau_dang_xuat");
        
        String duongDanHienTai = trieuDongWeb.getCurrentUrl();
        Assertions.assertTrue(duongDanHienTai.contains("login"), "Phải chuyển hướng về trang đăng nhập sau khi đăng xuất");
        
        System.out.println("✅ Đăng xuất thành công, chuyển hướng đến: " + duongDanHienTai);
    }
    
    // Tài khoản rỗng
    @Test
    @Order(7)
    void kiemThuDangNhap_TenDangNhapRong_LoiXacThuc() {
        System.out.println("=== KIỂM THỬ 7: Tên Đăng Nhập Rỗng ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        trieuDongWeb.findElement(By.name("username")).sendKeys("");
        trieuDongWeb.findElement(By.name("password")).sendKeys("123456");
        trieuDongWeb.findElement(By.cssSelector("button[type='submit']")).click();

        chupManHinh("07_ten_dang_nhap_rong");
        Assertions.assertTrue(trieuDongWeb.getCurrentUrl().contains("login"), "Phải ở lại trang đăng nhập");
        System.out.println("✅ Kiểm thử tên đăng nhập rỗng thành công");
    }
    
    // Mật khẩu rỗng
    @Test
    @Order(8)
    void kiemThuDangNhap_MatKhauRong_LoiXacThuc() {
        System.out.println("=== KIỂM THỬ 8: Mật Khẩu Rỗng ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        trieuDongWeb.findElement(By.name("username")).sendKeys("admin");
        trieuDongWeb.findElement(By.name("password")).sendKeys("");
        trieuDongWeb.findElement(By.cssSelector("button[type='submit']")).click();

        chupManHinh("08_mat_khau_rong");
        Assertions.assertTrue(trieuDongWeb.getCurrentUrl().contains("login"), "Phải ở lại trang đăng nhập");
        System.out.println("✅ Kiểm thử mật khẩu rỗng thành công");
    }
    
    // Tài khoản đã tồn tại
    @Test
    @Order(9)
    void kiemThuDangKy_TenDangNhapTrung_ThatBai() {
        System.out.println("=== KIỂM THỬ 9: Tên Đăng Nhập Đã Tồn Tại ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/register");
        trieuDongWeb.findElement(By.name("username")).sendKeys("admin"); // đã tồn tại
        trieuDongWeb.findElement(By.name("password")).sendKeys("123456");
        trieuDongWeb.findElement(By.cssSelector("button[type='submit']")).click();

        chupManHinh("09_dang_ky_trung_lap");
        Assertions.assertTrue(trieuDongWeb.getPageSource().contains("tồn tại") || 
                              trieuDongWeb.getCurrentUrl().contains("register"),
                              "Phải hiển thị lỗi hoặc ở lại trang đăng ký");
        System.out.println("✅ Kiểm thử tên đăng nhập trùng lặp thành công");
    }
    
    // Mật khẩu ngắn
    @Test
    @Order(10)
    void kiemThuDangKy_MatKhauYeu_LoiXacThuc() {
        System.out.println("=== KIỂM THỬ 10: Mật Khẩu Yếu ===");
        
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/register");
        trieuDongWeb.findElement(By.name("username")).sendKeys("nguoidungmatkhaungan");
        trieuDongWeb.findElement(By.name("password")).sendKeys("123"); //  ngắn
        trieuDongWeb.findElement(By.cssSelector("button[type='submit']")).click();

        chupManHinh("10_mat_khau_ngan_dang_ky");
        Assertions.assertTrue(trieuDongWeb.getCurrentUrl().contains("register"),
                             "Phải ở lại trang đăng ký do mật khẩu yếu");
        System.out.println("✅ Kiểm thử mật khẩu yếu thành công");
    }
}