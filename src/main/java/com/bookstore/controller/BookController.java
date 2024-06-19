package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.services.BookServices;
import com.bookstore.services.CategoryServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookServices bookService;

    @Autowired
    private CategoryServices categoryService;

    @GetMapping
    public String showAllBooks(Model model) {
        try {

            List<Book> books = bookService.getALlBooks();
            model.addAttribute("books", books);
            return "book/list";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addBookForm(Model model) {
        try {
            model.addAttribute("book", new Book());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/add";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addBook(@Valid @ModelAttribute("book") Book book, BindingResult result, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("categories", categoryService.getAllCategories());
                return "book/add";
            }
            bookService.addBook(book);
            return "redirect:/books";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }


    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editBookForm(@PathVariable("id") Long id, Model model) {

        try {
            Book book = bookService.getBookById(id);
            if ( book == null) {
                model.addAttribute("message", "Không tìm thấy sách!");
                return "error/404";
            }
            if (book != null) {
                model.addAttribute("book", book);
                model.addAttribute("categories", categoryService.getAllCategories());
                return "book/edit";
            }
            return "redirect:/books";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }

    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateBook(@PathVariable("id") Long id, @Valid @ModelAttribute("book") Book book, BindingResult result, Model model) {

        try {
            if (result.hasErrors()) {
                model.addAttribute("categories", categoryService.getAllCategories());
                return "book/edit";
            }

            if ( bookService.getBookById(id) == null) {
                model.addAttribute("message", "Không tìm thấy sách!");
                return "error/404";
            }

            bookService.updateBook(book);
            return "redirect:/books";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }

    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteBook(@PathVariable("id") Long id, Model model) {

        try {
            if ( bookService.getBookById(id) == null) {
                model.addAttribute("message", "Không tìm thấy sách!");
                return "error/404";
            }

            bookService.deleteBook(id);
            return "redirect:/books";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }
}
