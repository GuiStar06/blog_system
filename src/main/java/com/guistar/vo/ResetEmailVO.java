package com.guistar.vo;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
@Data
public class ResetEmailVO {
    @Email
    String email;
    @Length(max = 6,min = 6)
    String code;
    @Length(min = 6,max = 20)
    String password;
}
