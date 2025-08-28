package com.bookoryteam.bookory.controller.admin;

import com.bookoryteam.bookory.model.User;
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
@RequestMapping("/admin/user")
public class UserAdminController {
    // Yêu cầu cho Lê Đức An ở trang quản trị đối với [User]:
    // 1. Chỉ có thể Xóa mềm (Soft Delete).
    // 2. Không được phép Chỉnh sửa (Edit) thông tin người dùng.
    // 3. Có thể Thêm người dùng mẫu (Add new sample users).
    // 4. Có ô hiển thị các chỉ số thống kê: Tổng số người dùng, Hoạt động, Vô hiệu hóa (như trang admin/dashboard).
	
    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("currentPageTitle", "Quản Lý Người Dùng");
        // TODO: Thêm dữ liệu cho các ô hiển thị thống kê: Tổng số người dùng, Tổng hoạt động, Tổng vô hiệu hóa.
        return "admin/user";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // TODO: Xử lý logic xóa mềm người dùng (set deleted = true)
        // TODO: Lưu lại đối tượng người dùng sau khi cập nhật trạng thái xóa
        // TODO: Xử lý thông báo thành công/lỗi
        // TODO: Kiểm tra quyền Admin trước khi cho phép thực hiện
        return "redirect:/admin/user";
    }
}