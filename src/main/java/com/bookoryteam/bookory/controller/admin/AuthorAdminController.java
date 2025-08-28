package com.bookoryteam.bookory.controller.admin;

import java.util.List;
import java.util.Optional;
import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookoryteam.bookory.model.Author;
import com.bookoryteam.bookory.service.AuthorService;

@Controller
@RequestMapping("/admin/author")
public class AuthorAdminController {
    // Yêu cầu cho Nguyễn Quốc Hải ở trang quản trị đối với [Author]:
    // 1. Chỉ có thể Xóa mềm (Soft Delete).
    // 2. Có thể (Edit) thông tin tác giả.
    // 3. Có thể Thêm tác giả (Add new author).
    // 4. Có ô hiển thị các chỉ số thống kê: Tổng số tác giả (như trang admin/dashboard).
    // 5. Khi bấm vào tên tác giả [name] thì sẽ chuyển hướng đến trang admin/author nhưng với các [Book] mà [Author] đó được gán

    private final AuthorService authorService;

    public AuthorAdminController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String listAuthors(Model model) {
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        model.addAttribute("currentPageTitle", "Quản Lý Tác Giả");
        return "admin/author";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // **TODO**: Tạo một đối tượng Author rỗng và thêm vào model để bind với form
        // **TODO**: Đảm bảo có file 'src/main/resources/templates/author/create_edit.html'
        Author author = new Author();
        model.addAttribute("author", author);
        model.addAttribute("pageTitle", "Thêm tác giả mới");
        model.addAttribute("isEdit", false);
        return "author/create_edit"; // Tên thư mục view là author (số ít)
    }

    // **BỔ SUNG**: Method để hiển thị form edit
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // **SỬA LỖI**: Xử lý Optional<Author>
            Optional<Author> optionalAuthor = authorService.findById(id);
            if (!optionalAuthor.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tác giả với ID: " + id);
                return "redirect:/admin/author";
            }

            Author author = optionalAuthor.get();
            model.addAttribute("author", author);
            model.addAttribute("pageTitle", "Chỉnh sửa tác giả");
            model.addAttribute("isEdit", true);
            return "author/create_edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải thông tin tác giả: " + e.getMessage());
            return "redirect:/admin/author";
        }
    }

    @PostMapping("/save")
    public String saveAuthor(@ModelAttribute("author") Author author, RedirectAttributes redirectAttributes) {
        // **TODO**: Gọi AuthorService để lưu/cập nhật tác giả.
        // **TODO**: Xử lý thành công/thất bại và thêm thông báo.
        try {
            // **SỬA LỖI**: Chỉ set deleted = false khi tạo mới
            if (author.getId() == null) {
                author.setDeleted(false);
            }

            // Kiểm tra validation cơ bản
            if (author.getName() == null || author.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tên tác giả không được để trống.");
                // **SỬA LỖI**: Redirect về đúng URL
                if (author.getId() == null) {
                    return "redirect:/admin/author/new";
                } else {
                    return "redirect:/admin/author/edit/" + author.getId();
                }
            }

            Author savedAuthor = authorService.save(author);

            if (author.getId() == null) {
                redirectAttributes.addFlashAttribute("successMessage",
                    "Thêm tác giả '" + savedAuthor.getName() + "' thành công!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật tác giả '" + savedAuthor.getName() + "' thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Lỗi khi lưu tác giả: " + e.getMessage());
        }

        // **SỬA LỖI**: Redirect về đúng URL
        return "redirect:/admin/author";
    }

    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // **TODO**: Xử lý logic xóa mềm tác giả (set deleted = true)
        // **TODO**: Lưu lại đối tượng tác giả sau khi cập nhật trạng thái xóa
        // **TODO**: Xử lý thông báo thành công/lỗi
        // **TODO**: Kiểm tra quyền Admin trước khi cho phép thực hiện

        // **HOÀN THIỆN**: Implement logic xóa mềm
        try {
            // **SỬA LỖI**: Xử lý Optional<Author>
            Optional<Author> optionalAuthor = authorService.findById(id);
            if (!optionalAuthor.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tác giả với ID: " + id);
                return "redirect:/admin/author";
            }

            Author author = optionalAuthor.get();
            // Thực hiện xóa mềm
            author.setDeleted(true);
            authorService.save(author);

            redirectAttributes.addFlashAttribute("successMessage",
                "Đã ẩn tác giả '" + author.getName() + "' thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Lỗi khi ẩn tác giả: " + e.getMessage());
        }

        return "redirect:/admin/author";
    }
}