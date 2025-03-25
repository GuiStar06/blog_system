package com.guistar.vo;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommentVO {
    Long id;
    String content;
    LocalDateTime createTime;
    AccountVO author;
}
