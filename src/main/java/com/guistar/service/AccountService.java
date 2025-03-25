package com.guistar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guistar.entity.Account;
import com.guistar.vo.AccountVO;
import com.guistar.vo.ConfirmResetVO;
import com.guistar.vo.RegisterEmailVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByUsernameOrEmail(String text);
    String askVerifyCodeByEmail(String email,String type,String ip);
    String registerEmailAccount(RegisterEmailVO registerEmailVO);
    String resetConfirm(ConfirmResetVO confirmResetVO);
    String resetEmailAccount(RegisterEmailVO registerEmailVO);
    Account findAccountById(Long id);
    AccountVO convertToAccountVO(Account account);
}
