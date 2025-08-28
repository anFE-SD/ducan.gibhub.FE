package com.bookoryteam.bookory.controller.admin;

import com.bookoryteam.bookory.model.Seller;
import com.bookoryteam.bookory.model.User;
import com.bookoryteam.bookory.service.SellerService;
import com.bookoryteam.bookory.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/seller")
public class SellerAdminController {
    // Yêu cầu cho Lê Đức An ở trang quản trị đối với [Seller]:
    // 1. Chỉ có thể Xóa mềm (Soft Delete).
    // 2. Không được phép Chỉnh sửa (Edit) thông tin người bán.
    // 3. Có thể Thêm người bán mẫu (Add new sample sellers).
    // 4. Có ô hiển thị các chỉ số thống kê: Tổng số người bán, Hoạt động, Vô hiệu hóa (như trang admin/dashboard).
	// 5. Khi bấm vào tên tài khoản [username] thì sẽ chuyển hướng đến trang admin/book nhưng với các [Book] mà [Seller] đó bán
	
    private final SellerService sellerService;
    private final UserService userService;

    public SellerAdminController(SellerService sellerService, UserService userService) {
        this.sellerService = sellerService;
        this.userService = userService;
    }

    @GetMapping
    public String listSellers(Model model) {
        List<Seller> sellers = sellerService.findAll();
        model.addAttribute("sellers", sellers);
        model.addAttribute("currentPageTitle", "Quản Lý Người Bán");
        return "admin/seller";
    }

    @GetMapping("/delete/{userId}")
    public String deleteSeller(@PathVariable("userId") Long userId, RedirectAttributes redirectAttributes) {
        // TODO: Xử lý logic xóa mềm người bán (set deleted = true)
        // TODO: Lưu lại đối tượng người bán sau khi cập nhật trạng thái xóa
        // TODO: Xử lý thông báo thành công/lỗi
        // TODO: Kiểm tra quyền Admin trước khi cho phép thực hiện
        return "redirect:/admin/seller";
    }
}