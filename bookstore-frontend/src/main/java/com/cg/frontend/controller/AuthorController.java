package com.cg.frontend.controller;

import com.cg.frontend.dto.AuthorDto;
import com.cg.frontend.dto.PageMetaDto;
import com.cg.frontend.dto.TitleAuthorDto;
import com.cg.frontend.service.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String listAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            Model model) {

        List<AuthorDto> authors;
        PageMetaDto pageMeta;

        if (search != null && !search.isBlank()) {
            authors = authorService.searchAuthors(search);
            pageMeta = new PageMetaDto();
            pageMeta.setTotalElements(authors.size());
            pageMeta.setTotalPages(1);
            pageMeta.setNumber(0);
            pageMeta.setSize(authors.size());
        } else {
            authors = authorService.getAllAuthors(page, size);
            pageMeta = authorService.getPageMeta(page, size);
        }

        model.addAttribute("authors", authors);
        model.addAttribute("pageMeta", pageMeta);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        model.addAttribute("newAuthor", new AuthorDto());
        model.addAttribute("pageTitle", "Authors — Shubham's Module");
        return "authors/list";
    }

    @GetMapping("/{auId}")
    public String viewAuthor(@PathVariable String auId, Model model) {
        AuthorDto author = authorService.getAuthorById(auId);
        if (author == null) {
            return "redirect:/authors";
        }
        List<TitleAuthorDto> titleAuthors = authorService.getTitlesByAuthor(auId);
        model.addAttribute("author", author);
        model.addAttribute("titleAuthors", titleAuthors);
        model.addAttribute("pageTitle", "Author Detail — " + author.getFullName());
        return "authors/detail";
    }

    @PostMapping("/create")
    public String createAuthor(@ModelAttribute AuthorDto author, RedirectAttributes ra) {
        String error = authorService.createAuthor(author);
        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
            return "redirect:/authors?error=create";
        }
        return "redirect:/authors?success=create";
    }

    @PostMapping("/update/{auId}")
    public String updateAuthor(@PathVariable String auId, @ModelAttribute AuthorDto author, RedirectAttributes ra) {
        author.setAuId(auId);
        String error = authorService.updateAuthor(auId, author);
        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
            return "redirect:/authors?error=update";
        }
        return "redirect:/authors?success=update";
    }
}
