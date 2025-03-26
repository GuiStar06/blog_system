package com.guistar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guistar.entity.Account;
import com.guistar.vo.AccountVO;
import com.guistar.dto.RegisterEmailDTO;
import com.guistar.dto.ResetEmailDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByUsernameOrEmail(String text);
    String askVerifyCodeByEmail(String email,String type,String ip);
    String registerEmailAccount(RegisterEmailDTO registerEmailDTO);
    String resetEmailAccount(ResetEmailDTO resetEmailDTO);
    Account findAccountById(Long id);
    AccountVO convertToAccountVO(Account account);
}
