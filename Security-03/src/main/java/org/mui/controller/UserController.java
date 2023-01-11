package org.mui.controller;

import org.mui.entity.User;
import org.mui.utils.BaseUtil;
import org.mui.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
@PropertySource(value = "classpath:db.properties")
@RestController
public class UserController {

    @Value("${db.username}")
    private String USERNAME;
    @Value("${db.password}")
    private String PASSWORD;

    @PostMapping("/login")
    public User login(@RequestBody User user){

        Boolean password = BaseUtil.decryptPassword(user.getPassword(), PASSWORD);

        if (USERNAME.equals(user.getUsername()) && password){
            user.setToken(JwtUtil.createToken(user));
            System.out.println("login:\n"+user);
            return user;
        }
        return null;
    }

    @GetMapping("/checkToken")
    public Boolean checkToken(HttpServletRequest request){
        String token = request.getHeader("token");
        System.out.println("request请求头token:"+request.getHeader("token"));
        return JwtUtil.checkToken(token);
    }

    /**
     * 模拟接口返回数据
     * @param id
     * @return
     */
    @GetMapping("/vip/{id}")
    public String vipData(@PathVariable int id){
        if (id==1){
            return "普通用户";
        }
        if (id==2){
            return "VIP用户";
        }
        if (id==3){
            return "管理员";
        }
        return null;
    }


}
