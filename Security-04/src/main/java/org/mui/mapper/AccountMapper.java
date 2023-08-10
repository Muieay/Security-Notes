package org.mui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mui.entity.dto.Account;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
