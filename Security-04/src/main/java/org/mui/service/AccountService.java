package org.mui.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mui.entity.dto.Account;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
}
