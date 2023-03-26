package com.example.controller.restful;


import com.example.model.Blog;
import com.example.model.BlogDto;
import com.example.model.UpdateBlogDto;
import com.example.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BlogRestController {
    @Autowired
    private IBlogService blogService;

@GetMapping("/blogs/list")
public ResponseEntity<Page<BlogDto>> listBlogs(@PageableDefault(value = 5) Pageable pageable,
                                               @RequestParam(value = "search", required = false) String search) {
    Page<BlogDto> blogDtos;
    if (search != null && !search.isEmpty()) {
        blogDtos = blogService.queryByName(search, pageable);
    } else {
        blogDtos = blogService.findAll(pageable);
    }
    return new ResponseEntity<>(blogDtos, HttpStatus.OK);
}

    @GetMapping("/blogs/{id}")
    public ResponseEntity<BlogDto> showInfo(@PathVariable Long id){
        Optional<BlogDto> blogDto = blogService.findById(id);
        if (blogDto.isPresent()) {
            return new ResponseEntity<>(blogDto.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
//        return new ResponseEntity<>(blogDto.get(), HttpStatus.OK);
    }

    @PostMapping("blogs/save")
    public ResponseEntity<BlogDto> save(@Valid @RequestBody BlogDto blogDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        blogService.save(blogDto);
        return new ResponseEntity<>(blogDto, HttpStatus.CREATED);
    }
//    @PutMapping("blogs/update/{id}")
//    public ResponseEntity<BlogDto> update(@PathVariable Long id,@RequestBody BlogDto blogDto){
//        Optional<BlogDto> blogDtoOptional = blogService.findById(id);
//        if(!blogDtoOptional.isPresent()){
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        blogDto.setId(id);
//        blogService.save(blogDto);
//        return new ResponseEntity<>(blogDto,HttpStatus.ACCEPTED);
//    }
    @PutMapping("blogs/update/{id}")
    public ResponseEntity<Blog> update(@PathVariable Long id,@RequestBody UpdateBlogDto requestDto){
        Blog currentBlog = blogService.findByIdEntity(id)   //doi tuong lay len tu database
                .orElse(null);
        if(ObjectUtils.isEmpty(currentBlog)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentBlog.setName(requestDto.getName());  //dto nay tao boi du lieu body postman, class k có constructor
        currentBlog.setAuthor(requestDto.getAuthor());
        currentBlog.setAccess(requestDto.getAccess());
        blogService.save(currentBlog);
        return new ResponseEntity<>(currentBlog,HttpStatus.ACCEPTED);
    }

}
