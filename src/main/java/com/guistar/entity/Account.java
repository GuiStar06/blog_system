package com.guistar.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
        @TableId(type = IdType.AUTO)
        private Long id;// 主键，自增
        private String username;      // 用户名，唯一
        private String password;      // 密码（加密存储）
        private String email;     //邮箱
        private String role;
        private String nickname;      // 昵称
        private String avatar;        // 头像URL
        private Date registerTime;
}
