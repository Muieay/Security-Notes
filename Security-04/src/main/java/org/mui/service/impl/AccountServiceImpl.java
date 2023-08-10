package org.mui.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mui.entity.dto.Account;
import org.mui.mapper.AccountMapper;
import org.mui.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
        implements AccountService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);

        UserDetails build = User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();

        return build;
    }

    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text)
                .or()
                .eq("email", text)
                .one();
    }
}
