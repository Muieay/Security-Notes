package org.mui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HelloController {

    @RequestMapping ({"/","index"})
    public String hello(){
        System.out.println("Hello Index");
        return "index";
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/a/{id}")
//    @PreAuthorize("hasAnyAuthority('vip1')")  //设置权限规则
    public String toA(@PathVariable int id){
        return "a/"+id;
    }

    @RequestMapping("/b/{id}")
    public String toB(@PathVariable int id){
        return "b/"+id;
    }

    @RequestMapping("/c/{id}")
    public String toC(@PathVariable int id){
        return "c/"+id;
    }
}
