package com.example.controller;

import com.example.model.Blog;
import com.example.model.BlogDto;
import com.example.service.IBlogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/blogs")
@SessionAttributes("message")
public class BlogController {
    @Autowired
    private IBlogService blogService;

    @Autowired
    private ModelMapper modelMapper;

    //    @ExceptionHandler(DuplicateNameException.class)
//    public ModelAndView showNameException(){
//        return new ModelAndView("name-exception");
//    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataException(DataIntegrityViolationException e, HttpServletRequest request, HttpSession session) {
        String requestURI = request.getRequestURI();
        session.setAttribute("message", "Update failed. Try again");
        if (requestURI.endsWith("/create")) {
            return "redirect:/blogs/create";
        } else if (requestURI.endsWith("/update")) {
            Long id = Long.parseLong(request.getParameter("id"));
            return "redirect:/blogs/update/" + id;
        } else {
            return "redirect:/blogs/error-404";
        }
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView showException() {
        return new ModelAndView("name-exception");
    }

    @GetMapping("list")
    public ModelAndView list(@PageableDefault(value = 5) Pageable pageable,
                             @RequestParam(value = "search", required = false) String search) {
        ModelAndView modelAndView = new ModelAndView("list");
        Page<BlogDto> blogDtos;
        if (search != null && !search.isEmpty()) {
            blogDtos = blogService.queryByName(search, pageable);
        } else {
            blogDtos = blogService.findAll(pageable);
        }
        modelAndView.addObject("blogDtos", blogDtos);
        modelAndView.addObject("search", search);
        return modelAndView;
    }


    @GetMapping("/create")
    public ModelAndView createForm() {
        ModelAndView modelAndView = new ModelAndView("create");
        modelAndView.addObject("blogDto", new BlogDto());
        return modelAndView;
    }

    //data binding khong cần them @ModelAttribute("blog") vào phuong thuc
    @PostMapping("/create")
    public ModelAndView create(@Valid BlogDto blogDto, BindingResult bindingResult) {
        new BlogDto().validate(blogDto, bindingResult);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("create");
            return modelAndView;
        } else {
            Blog blog = modelMapper.map(blogDto, Blog.class);
            blogService.save(blogDto);
            ModelAndView modelAndView = new ModelAndView("create");
            modelAndView.addObject("message", blogDto.getName() +" created successfully");
            return modelAndView;
        }
    }


    @GetMapping("/update/{id}")
    public ModelAndView updateForm(@PathVariable Long id) {
        Optional<BlogDto> blogDto = blogService.findById(id);
        if (blogDto.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("update");
            modelAndView.addObject("blogDto", blogDto.get());
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("error.404");
            return modelAndView;
        }
    }

    @PostMapping("/update")
    public ModelAndView update(@ModelAttribute("blogDto") BlogDto blogDto) {
        blogService.save(blogDto);
        return new ModelAndView("update", "message", blogDto.getName() + " updated successfully");
    }

    @GetMapping("/delete/{id}")
    public ModelAndView DeleteForm(@PathVariable Long id) {
        Optional<BlogDto> blogDto = blogService.findById(id);
        if (blogDto.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("delete");
            modelAndView.addObject("blogDto", blogDto.get());
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("error.404");
            return modelAndView;
        }
    }

    //  @PostMapping("/delete")
//    public String deleteBlog(@ModelAttribute("blog") Blog blog) {
//        blogService.remove(blog.getId());
//        return "redirect:/blogs";
//    }
    @PostMapping("/delete")
    public String deleteBlog(Long id) {
        blogService.remove(id);
        return "redirect:/blogs";
    }
//  trong the input trong view phai dat name = id thi moi lay dc id
    @GetMapping("/error-404")
    public String error(){
        return "error.404";
    }
}
