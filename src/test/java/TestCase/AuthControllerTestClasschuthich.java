package TestCase;

// ===== IMPORT CÁC THỨ VIỆN CẦN THIẾT =====
import org.junit.jupiter.api.*;          // Thư viện JUnit 5 cho kiểm thử đơn vị
import org.openqa.selenium.*;            // Selenium WebDriver cốt lõi - điều khiển trình duyệt
import org.openqa.selenium.chrome.*;     // Driver dành riêng cho Chrome browser
import org.openqa.selenium.support.ui.*; // WebDriverWait, ExpectedConditions - chờ đợi phần tử
import org.apache.commons.io.FileUtils;  // Tiện ích File của Apache để xử lý chụp màn hình
import io.github.bonigarcia.wdm.*;       // WebDriverManager tự động tải và quản lý driver

import java.io.File;
import java.io.IOException;
import java.time.Duration;                // Thời gian chờ
import java.time.LocalDateTime;           // Thời gian hiện tại
import java.time.format.DateTimeFormatter; // Định dạng thời gian

// Chú thích này báo cho JUnit chạy các test theo thứ tự đã định
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTestClasschuthich {
    
    // ===== KHAI BÁO CÁC BIẾN TĨNH CHUNG CHO TẤT CẢ TEST =====
    private static WebDriver trieuDongWeb;    // Đối tượng điều khiển trình duyệt
    private static WebDriverWait choDoiWeb;   // Đối tượng chờ đợi phần tử xuất hiện
    private static final String DUONG_DAN_CO_SO = "http://localhost:8080"; // URL gốc của ứng dụng
    private static final String THU_MUC_CHUP_MAN_HINH = "anh-chup-kiem-thu"; // Thư mục lưu ảnh
    
    // ===== PHƯƠNG THỨC CHẠY TRƯỚC TẤT CẢ CÁC TEST =====
    @BeforeAll
    static void caiDatBanDau() {
        // Tự động tải và cài đặt ChromeDriver phiên bản phù hợp
        WebDriverManager.chromedriver().setup();
        
        // Cấu hình các tùy chọn cho Chrome browser
        ChromeOptions tuyChonChrome = new ChromeOptions();
        tuyChonChrome.addArguments("--start-maximized");          // Mở trình duyệt toàn màn hình
        tuyChonChrome.addArguments("--disable-blink-features=AutomationControlled"); // Ẩn dấu hiệu tự động hóa

        // Khởi tạo trình duyệt Chrome với các tùy chọn đã cấu hình
        trieuDongWeb = new ChromeDriver(tuyChonChrome);
        // Cài đặt thời gian chờ tối đa là 10 giây cho các phần tử
        choDoiWeb = new WebDriverWait(trieuDongWeb, Duration.ofSeconds(10));
        
        // Tạo thư mục để lưu ảnh chụp màn hình (nếu chưa tồn tại)
        new File(THU_MUC_CHUP_MAN_HINH).mkdirs();
    }
    
    // ===== PHƯƠNG THỨC CHẠY SAU TẤT CẢ CÁC TEST =====
    @AfterAll
    static void dongDonSau() {
        // Kiểm tra xem trình duyệt có tồn tại không
        if (trieuDongWeb != null) {
            trieuDongWeb.quit();  // Đóng trình duyệt và giải phóng tài nguyên
        }
    }
    
    // ===== PHƯƠNG THỨC CHỤP MÀN HÌNH =====
    private void chupManHinh(String tenKiemThu) {
        try {
            // Chuyển đổi WebDriver thành đối tượng có thể chụp màn hình
            TakesScreenshot chupAnh = (TakesScreenshot) trieuDongWeb;
            // Chụp màn hình và lưu vào file tạm
            File tepNguon = chupAnh.getScreenshotAs(OutputType.FILE);
            
            // Tạo tên file với thời gian hiện tại
            String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String tenTep = String.format("%s/%s_%s.png", THU_MUC_CHUP_MAN_HINH, tenKiemThu, thoiGian);
            
            // Sao chép file tạm vào thư mục đích
            FileUtils.copyFile(tepNguon, new File(tenTep));
            System.out.println("Đã lưu ảnh chụp màn hình: " + tenTep);
        } catch (IOException e) {
            // Xử lý lỗi nếu không thể chụp màn hình
            System.err.println("Lỗi khi chụp màn hình: " + e.getMessage());
        }
    }
    
    // ===== TEST CASE 1: KIỂM THỬ HIỂN THỊ FORM ĐĂNG NHẬP =====
    @Test
    @Order(1) // Chạy test này đầu tiên
    void kiemThuHienThiFormDangNhap() {
        System.out.println("=== KIỂM THỬ 1: Hiển Thị Form Đăng Nhập ===");
        
        // Điều hướng đến trang đăng nhập
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        // Chụp màn hình để ghi lại trạng thái
        chupManHinh("01_form_dang_nhap");
        
        // Tìm và xác minh các thành phần form đăng nhập
        // Chờ đợi trường username xuất hiện (tối đa 10 giây)
        WebElement truongTenDangNhap = choDoiWeb.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        // Tìm trường password
        WebElement truongMatKhau = trieuDongWeb.findElement(By.name("password"));
        // Tìm nút submit (đăng nhập)
        WebElement nutDangNhap = trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']"));
        
        // Kiểm tra các phần tử có hiển thị không
        Assertions.assertTrue(truongTenDangNhap.isDisplayed(), "Trường tên đăng nhập phải hiển thị");
        Assertions.assertTrue(truongMatKhau.isDisplayed(), "Trường mật khẩu phải hiển thị");
        Assertions.assertTrue(nutDangNhap.isDisplayed(), "Nút đăng nhập phải hiển thị");
        
        System.out.println("✅ Form đăng nhập hiển thị thành công");
    }
    
    // ===== TEST CASE 2: KIỂM THỬ ĐĂNG NHẬP VỚI THÔNG TIN HỢP LỆ =====
    @Test
    @Order(2) // Chạy test này thứ hai
    void kiemThuDangNhap_ThongTinHopLe_ThanhCong() {
        System.out.println("=== KIỂM THỬ 2: Đăng Nhập Hợp Lệ ===");
        
        // Điều hướng đến trang đăng nhập
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        chupManHinh("02_truoc_dang_nhap_hop_le");
        
        // Nhập thông tin đăng nhập hợp lệ
        trieuDongWeb.findElement(By.name("username")).sendKeys("levanan"); // Nhập tên đăng nhập
        trieuDongWeb.findElement(By.name("password")).sendKeys("12345678"); // Nhập mật khẩu
        
        // Chụp màn hình sau khi điền form
        chupManHinh("02_da_dien_form_dang_nhap");
        
        // Nhấn nút đăng nhập
        trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();
        
        // Chờ đợi chuyển hướng đến trang dashboard
        choDoiWeb.until(ExpectedConditions.urlContains("dashboard"));
        chupManHinh("02_sau_dang_nhap_hop_le");
        
        // Lấy URL hiện tại và kiểm tra
        String duongDanHienTai = trieuDongWeb.getCurrentUrl();
        // Xác minh đã chuyển hướng đến dashboard hoặc home
        Assertions.assertTrue(duongDanHienTai.contains("dashboard") || duongDanHienTai.contains("home"), 
                             "Phải chuyển hướng đến dashboard sau khi đăng nhập thành công");
        
        System.out.println("✅ Đăng nhập hợp lệ thành công, chuyển hướng đến: " + duongDanHienTai);
    }
    
    // ===== TEST CASE 3: KIỂM THỬ ĐĂNG NHẬP VỚI THÔNG TIN KHÔNG HỢP LỆ =====
    @Test
    @Order(3) // Chạy test này thứ ba
    void kiemThuDangNhap_ThongTinKhongHopLe_ThatBai() {
        System.out.println("=== KIỂM THỬ 3: Đăng Nhập Không Hợp Lệ ===");
        
        // Điều hướng đến trang đăng nhập
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        chupManHinh("03_truoc_dang_nhap_sai");
        
        // Nhập thông tin sai
        trieuDongWeb.findElement(By.name("username")).sendKeys("nguoidungsai");
        trieuDongWeb.findElement(By.name("password")).sendKeys("matkhausai");
        
        chupManHinh("03_da_dien_thong_tin_sai");
        
        // Nhấn nút đăng nhập
        trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();
        
        // Chờ thông báo lỗi xuất hiện
        try {
            // Tìm phần tử thông báo lỗi với nhiều selector khác nhau
            WebElement thongBaoLoi = choDoiWeb.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".error, .alert-danger, .text-danger, [class*='error']")));
            chupManHinh("03_sau_dang_nhap_sai");
            
            // Kiểm tra thông báo lỗi có hiển thị không
            Assertions.assertTrue(thongBaoLoi.isDisplayed(), "Thông báo lỗi phải được hiển thị");
            System.out.println("✅ Đăng nhập không hợp lệ đã bị từ chối với lỗi: " + thongBaoLoi.getText());
        } catch (TimeoutException e) {
            // Nếu không tìm thấy thông báo lỗi, kiểm tra vẫn ở trang đăng nhập
            chupManHinh("03_sau_dang_nhap_sai_khong_co_loi");
            // Xác minh vẫn ở trang đăng nhập
            Assertions.assertTrue(trieuDongWeb.getCurrentUrl().contains("login"), 
                                 "Phải ở lại trang đăng nhập sau khi nhập thông tin sai");
            System.out.println("✅ Đăng nhập không hợp lệ bị từ chối (vẫn ở trang đăng nhập)");
        }
    }
    
    // ===== TEST CASE 4: KIỂM THỬ HIỂN THỊ FORM ĐĂNG KÝ =====
    @Test
    @Order(4) // Chạy test này thứ tư
    void kiemThuHienThiFormDangKy() {
        System.out.println("=== KIỂM THỬ 4: Hiển Thị Form Đăng Ký ===");
        
        // Điều hướng đến trang đăng ký
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/register");
        chupManHinh("04_form_dang_ky");
        
        // Xác minh các thành phần form đăng ký
        // Chờ đợi trường username xuất hiện
        WebElement truongTenDangNhap = choDoiWeb.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        // Tìm trường password
        WebElement truongMatKhau = trieuDongWeb.findElement(By.name("password"));
        // Tìm nút submit (đăng ký)
        WebElement nutDangKy = trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']"));
        
        // Kiểm tra các phần tử có hiển thị không
        Assertions.assertTrue(truongTenDangNhap.isDisplayed(), "Trường tên đăng nhập phải hiển thị");
        Assertions.assertTrue(truongMatKhau.isDisplayed(), "Trường mật khẩu phải hiển thị");
        Assertions.assertTrue(nutDangKy.isDisplayed(), "Nút đăng ký phải hiển thị");
        
        System.out.println("✅ Form đăng ký hiển thị thành công");
    }
    
    // ===== TEST CASE 5: KIỂM THỬ ĐĂNG KÝ VỚI DỮ LIỆU HỢP LỆ =====
    @Test
    @Order(5) // Chạy test này thứ năm
    void kiemThuDangKy_DuLieuHopLe_ThanhCong() throws InterruptedException {
        System.out.println("=== KIỂM THỬ 5: Đăng Ký Hợp Lệ ===");
        
        // Điều hướng đến trang đăng ký
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/register");
        chupManHinh("05_truoc_dang_ky");
        
        // Tạo tên đăng nhập duy nhất bằng cách thêm timestamp
        String tenDangNhapDuyNhat = "nguoidungkiem_" + System.currentTimeMillis();
        
        // Nhập thông tin đăng ký
        trieuDongWeb.findElement(By.name("username")).sendKeys(tenDangNhapDuyNhat);
        trieuDongWeb.findElement(By.name("password")).sendKeys("matkhau123");
        
        // Kiểm tra xem có trường xác nhận mật khẩu không
        try {
            // Nếu có trường confirmPassword thì điền vào
            trieuDongWeb.findElement(By.name("confirmPassword")).sendKeys("matkhau123");
        } catch (NoSuchElementException e) {
            // Không có trường xác nhận mật khẩu - bỏ qua
        }
        
        chupManHinh("05_da_dien_form_dang_ky");
        
        // Nhấn nút đăng ký
        trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();
        
        // Chờ xử lý đăng ký (2 giây)
        Thread.sleep(2000);
        chupManHinh("05_sau_dang_ky");
        
        // Lấy URL hiện tại
        String duongDanHienTai = trieuDongWeb.getCurrentUrl();
        // Kiểm tra đăng ký thành công bằng cách xem có chuyển hướng không
        boolean dangKyThanhCong = duongDanHienTai.contains("login") || duongDanHienTai.contains("success") || 
                                 duongDanHienTai.contains("dashboard");
        
        // Xác minh đăng ký thành công
        Assertions.assertTrue(dangKyThanhCong, "Phải chuyển hướng sau khi đăng ký thành công");
        System.out.println("✅ Đăng ký thành công, chuyển hướng đến: " + duongDanHienTai);
    }
    
    // ===== TEST CASE 6: KIỂM THỬ ĐĂNG XUẤT =====
    @Test
    @Order(6) // Chạy test này thứ sáu
    void kiemThuDangXuat_ThanhCong() throws InterruptedException {
        System.out.println("=== KIỂM THỬ 6: Đăng Xuất ===");
        
        // Đăng nhập trước khi test đăng xuất
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        trieuDongWeb.findElement(By.name("username")).sendKeys("admin");
        trieuDongWeb.findElement(By.name("password")).sendKeys("matkhau123");
        trieuDongWeb.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();
        
        // Chờ đăng nhập hoàn tất
        Thread.sleep(2000);
        chupManHinh("06_truoc_dang_xuat");
        
        // Tìm và nhấn nút đăng xuất
        try {
            // Thử tìm link "Đăng xuất" bằng text
            WebElement nutDangXuat = trieuDongWeb.findElement(By.linkText("Đăng xuất"));
            nutDangXuat.click();
        } catch (NoSuchElementException e) {
            // Nếu không tìm thấy, thử các selector khác
            try {
                // Tìm link có href chứa "logout"
                trieuDongWeb.findElement(By.cssSelector("a[href*='logout']")).click();
            } catch (NoSuchElementException ex) {
                // Nếu vẫn không tìm thấy, đăng xuất trực tiếp bằng URL
                trieuDongWeb.get(DUONG_DAN_CO_SO + "/logout");
            }
        }
        
        // Chờ đợi chuyển hướng về trang đăng nhập
        choDoiWeb.until(ExpectedConditions.urlContains("login"));
        chupManHinh("06_sau_dang_xuat");
        
        // Lấy URL hiện tại và kiểm tra
        String duongDanHienTai = trieuDongWeb.getCurrentUrl();
        // Xác minh đã chuyển hướng về trang đăng nhập
        Assertions.assertTrue(duongDanHienTai.contains("login"), "Phải chuyển hướng về trang đăng nhập sau khi đăng xuất");
        
        System.out.println("✅ Đăng xuất thành công, chuyển hướng đến: " + duongDanHienTai);
    }
    
    // ===== TEST CASE 7: KIỂM THỬ ĐĂNG NHẬP VỚI TÊN ĐĂNG NHẬP RỖNG =====
    @Test
    @Order(7) // Chạy test này thứ bảy
    void kiemThuDangNhap_TenDangNhapRong_LoiXacThuc() {
        System.out.println("=== KIỂM THỬ 7: Tên Đăng Nhập Rỗng ===");
        
        // Điều hướng đến trang đăng nhập
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        // Để trống tên đăng nhập
        trieuDongWeb.findElement(By.name("username")).sendKeys("");
        // Nhập mật khẩu bình thường
        trieuDongWeb.findElement(By.name("password")).sendKeys("123456");
        // Nhấn nút đăng nhập
        trieuDongWeb.findElement(By.cssSelector("button[type='submit']")).click();

        chupManHinh("07_ten_dang_nhap_rong");
        // Xác minh vẫn ở trang đăng nhập (không được phép đăng nhập)
        Assertions.assertTrue(trieuDongWeb.getCurrentUrl().contains("login"), "Phải ở lại trang đăng nhập");
        System.out.println("✅ Kiểm thử tên đăng nhập rỗng thành công");
    }
    
    // ===== TEST CASE 8: KIỂM THỬ ĐĂNG NHẬP VỚI MẬT KHẨU RỖNG =====
    @Test
    @Order(8) // Chạy test này thứ tám
    void kiemThuDangNhap_MatKhauRong_LoiXacThuc() {
        System.out.println("=== KIỂM THỬ 8: Mật Khẩu Rỗng ===");
        
        // Điều hướng đến trang đăng nhập
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/login");
        // Nhập tên đăng nhập bình thường
        trieuDongWeb.findElement(By.name("username")).sendKeys("admin");
        // Để trống mật khẩu
        trieuDongWeb.findElement(By.name("password")).sendKeys("");
        // Nhấn nút đăng nhập
        trieuDongWeb.findElement(By.cssSelector("button[type='submit']")).click();

        chupManHinh("08_mat_khau_rong");
        // Xác minh vẫn ở trang đăng nhập (không được phép đăng nhập)
        Assertions.assertTrue(trieuDongWeb.getCurrentUrl().contains("login"), "Phải ở lại trang đăng nhập");
        System.out.println("✅ Kiểm thử mật khẩu rỗng thành công");
    }
    
    // ===== TEST CASE 9: KIỂM THỬ ĐĂNG KÝ VỚI TÊN ĐĂNG NHẬP ĐÃ TỒN TẠI =====
    @Test
    @Order(9) // Chạy test này thứ chín
    void kiemThuDangKy_TenDangNhapTrung_ThatBai() {
        System.out.println("=== KIỂM THỬ 9: Tên Đăng Nhập Đã Tồn Tại ===");
        
        // Điều hướng đến trang đăng ký
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/register");
        // Nhập tên đăng nhập đã tồn tại trong hệ thống
        trieuDongWeb.findElement(By.name("username")).sendKeys("admin"); // đã tồn tại
        trieuDongWeb.findElement(By.name("password")).sendKeys("123456");
        // Nhấn nút đăng ký
        trieuDongWeb.findElement(By.cssSelector("button[type='submit']")).click();

        chupManHinh("09_dang_ky_trung_lap");
        // Kiểm tra có thông báo lỗi "tồn tại" hoặc vẫn ở trang đăng ký
        Assertions.assertTrue(trieuDongWeb.getPageSource().contains("tồn tại") || 
                              trieuDongWeb.getCurrentUrl().contains("register"),
                              "Phải hiển thị lỗi hoặc ở lại trang đăng ký");
        System.out.println("✅ Kiểm thử tên đăng nhập trùng lặp thành công");
    }
    
    // ===== TEST CASE 10: KIỂM THỬ ĐĂNG KÝ VỚI MẬT KHẨU YẾU =====
    @Test
    @Order(10) // Chạy test này cuối cùng
    void kiemThuDangKy_MatKhauYeu_LoiXacThuc() {
        System.out.println("=== KIỂM THỬ 10: Mật Khẩu Yếu ===");
        
        // Điều hướng đến trang đăng ký
        trieuDongWeb.get(DUONG_DAN_CO_SO + "/register");
        // Nhập tên đăng nhập mới
        trieuDongWeb.findElement(By.name("username")).sendKeys("nguoidungmatkhaungan");
        // Nhập mật khẩu quá ngắn (chỉ 3 ký tự)
        trieuDongWeb.findElement(By.name("password")).sendKeys("123"); // quá ngắn
        // Nhấn nút đăng ký
        trieuDongWeb.findElement(By.cssSelector("button[type='submit']")).click();

        chupManHinh("10_mat_khau_ngan_dang_ky");
        // Xác minh vẫn ở trang đăng ký (không được phép đăng ký do mật khẩu yếu)
        Assertions.assertTrue(trieuDongWeb.getCurrentUrl().contains("register"),
                             "Phải ở lại trang đăng ký do mật khẩu yếu");
        System.out.println("✅ Kiểm thử mật khẩu yếu thành công");
    }
}