package com.guistar.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.guistar.entity.utils.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account")
public class Account implements BaseData, Serializable {
        @TableId(type = IdType.AUTO)
        private Long id;// 主键，自增
        private String username;    // 用户名，唯一
        private String password;      // 密码（加密存储）
        private String email;     //邮箱
        private String role;
        private String nickname;      // 昵称
        private String avatar;        // 头像URL
        @TableField(value = "register_time",fill = FieldFill.INSERT)
        private Date registerTime;
}
