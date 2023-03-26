package com.codegym.cms.controller;

import com.codegym.cms.model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @GetMapping("/view/{id}")
    public ModelAndView viewCustomer(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("/info");
        RestTemplate restTemplate = new RestTemplate();
        //goi du lieu qua dau api khong qua service
        Customer customer = restTemplate.getForObject("http://localhost:8080/api/customers/" + id, Customer.class);
        modelAndView.addObject("customer", customer);
        return modelAndView;
    }
}
//    RestTemplate là một lớp trong thư viện Spring Framework dùng để gửi và nhận yêu cầu HTTP từ các dịch vụ web REST.
//
//        RestTemplate hỗ trợ các phương thức HTTP chính như GET, POST, PUT, DELETE và có thể sử dụng để gửi các yêu cầu
//        với các định dạng dữ liệu khác nhau như JSON, XML hoặc form data. Nó cũng hỗ trợ việc đăng nhập xác thực và sử dụng cookies.
