package com.guistar.vo;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmResetVO {
    @Email
    String email;
    @Length(max = 6,min = 6)
    String code;
}
