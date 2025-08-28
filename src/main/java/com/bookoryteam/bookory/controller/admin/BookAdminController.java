package com.bookoryteam.bookory.controller.admin;

import java.util.List;
import java.util.Optional;
import org.springframework.ui.Model;
import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;   // <-- Add this!
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookoryteam.bookory.model.Book;
import com.bookoryteam.bookory.model.Seller;
import com.bookoryteam.bookory.model.User;
import com.bookoryteam.bookory.service.AuthorService;
import com.bookoryteam.bookory.service.BookService;
import com.bookoryteam.bookory.service.CategoryService;
import com.bookoryteam.bookory.service.PublisherService;
import com.bookoryteam.bookory.service.SellerService;

@Controller
@RequestMapping("/admin/book")
public class BookAdminController {
    // Yêu cầu cho Nguyễn Quốc Hải ở trang quản trị đối với [Book]:
    // 1. Chỉ có thể Xóa mềm (Soft Delete).
    // 2. Có thể (Edit) thông tin sách.
    // 3. Có thể Thêm sách (Add new book).
    // 4. Có ô hiển thị các chỉ số thống kê: Tổng số sách (như trang admin/dashboard).
    // 5. Khi bấm vào tên sách [title] thì sẽ chuyển hướng đến trang chi tiết sách

    private final BookService bookService;
    private final PublisherService publisherService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final SellerService sellerService;

    public BookAdminController(BookService bookService, PublisherService publisherService,
            AuthorService authorService, CategoryService categoryService,
            SellerService sellerService) {
			this.bookService = bookService;
			this.publisherService = publisherService;
			this.authorService = authorService;
			this.categoryService = categoryService;
			this.sellerService = sellerService;
}

    @GetMapping
    public String listBooks(Model model) {
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        model.addAttribute("currentPageTitle", "Quản Lý Sách");
        return "admin/book";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // **TODO**: Tạo một đối tượng Book rỗng và thêm vào model để bind với form
        // **TODO**: Đảm bảo có file 'src/main/resources/templates/book/create_edit.html'
        Book book = new Book();
        book.setDeleted(false); // Mặc định không bị xóa

        model.addAttribute("book", book);
        model.addAttribute("pageTitle", "Thêm sách mới");
        model.addAttribute("isEdit", false);

        // Load dữ liệu cho dropdown
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("sellers", sellerService.findAll());


        return "book/create_edit";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // **SỬA LỖI**: Xử lý Optional<Book>
            Optional<Book> optionalBook = bookService.findById(id);
            if (!optionalBook.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách với ID: " + id);
                return "redirect:/admin/book";
            }

            Book book = optionalBook.get();
            if (book.getSeller() == null) {
                book.setSeller(new Seller());
            }
            if (book.getSeller().getUser() == null) {
                book.getSeller().setUser(new User());
            }
            model.addAttribute("book", book);
            model.addAttribute("pageTitle", "Chỉnh sửa sách");
            model.addAttribute("isEdit", true);

            // Load dữ liệu cho dropdown
            model.addAttribute("publishers", publisherService.findAll());
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("sellers", sellerService.findByDeletedFalse());

            return "book/create_edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải thông tin sách: " + e.getMessage());
            return "redirect:/admin/book";
        }
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") Book book,
                           @RequestParam("sellerId") Long sellerId,
                           RedirectAttributes redirectAttributes) {
        try {
            if (book.getId() == null) {
                book.setDeleted(false);
            }

            // Lấy seller từ DB theo sellerId
            Optional<Seller> optionalSeller = sellerService.findById(sellerId);
            if (!optionalSeller.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Seller không tồn tại!");
                if (book.getId() == null) {
                    return "redirect:/admin/book/new";
                } else {
                    return "redirect:/admin/book/edit/" + book.getId();
                }
            }
            book.setSeller(optionalSeller.get());

            // Validate các trường khác như bạn đã làm

            Book savedBook = bookService.save(book);

            if (book.getId() == null) {
                redirectAttributes.addFlashAttribute("successMessage",
                    "Thêm sách '" + savedBook.getTitle() + "' thành công!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật sách '" + savedBook.getTitle() + "' thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Lỗi khi lưu sách: " + e.getMessage());
        }

        return "redirect:/admin/book";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // **TODO**: Xử lý logic xóa mềm sách (set deleted = true)
        // **TODO**: Lưu lại đối tượng sách sau khi cập nhật trạng thái xóa
        // **TODO**: Xử lý thông báo thành công/lỗi
        // **TODO**: Kiểm tra quyền Admin trước khi cho phép thực hiện

        // **HOÀN THIỆN**: Implement logic xóa mềm
        try {
            // **SỬA LỖI**: Xử lý Optional<Book>
            Optional<Book> optionalBook = bookService.findById(id);
            if (!optionalBook.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách với ID: " + id);
                return "redirect:/admin/book";
            }

            Book book = optionalBook.get();
            // Thực hiện xóa mềm
            book.setDeleted(true);
            bookService.save(book);

            redirectAttributes.addFlashAttribute("successMessage",
                "Đã ẩn sách '" + book.getTitle() + "' thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Lỗi khi ẩn sách: " + e.getMessage());
        }

        return "redirect:/admin/book";
    }
}