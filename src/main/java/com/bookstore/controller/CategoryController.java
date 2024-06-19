package com.bookstore.controller;

import com.bookstore.entity.Category;
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
@RequestMapping("/categories")
@PreAuthorize("hasAuthority('ADMIN')")
public class CategoryController {

    @Autowired
    private CategoryServices categoryServices;

    @GetMapping()
    public String showAllCategories(Model model) {
        try {
            List<Category> categories = categoryServices.getAllCategories();
            model.addAttribute("categories", categories);
            return "category/list";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        try {
            model.addAttribute("category", new Category());
            return "category/add";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult result, Model model) {
        try {
            if (result.hasErrors()) {
                return "category/add";
            }
            categoryServices.saveCategory(category);
            return "redirect:/categories";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            Category category = categoryServices.getCategoryById(id);

            if ( category == null) {
                model.addAttribute("message", "Không tìm thấy danh mục!");
                return "error/404";
            }

            if (category != null) {
                model.addAttribute("category", category);
                return "Category/edit";
            }
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid @ModelAttribute("category") Category category, BindingResult result, Model model) {
        try {
            if (result.hasErrors()) {
                return "category/edit";
            }

            if ( categoryServices.getCategoryById(id) == null) {
                model.addAttribute("message", "Không tìm thấy danh mục!");
                return "error/404";
            }

            categoryServices.saveCategory(category);
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model) {
        try {
            if ( categoryServices.getCategoryById(id) == null) {
                model.addAttribute("message", "Không tìm thấy danh mục!");
                return "error/404";
            }

            categoryServices.deleteCategory(id);
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }
}
