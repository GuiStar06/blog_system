package com.guistar.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleVO {
    Long id;
    String title;
    String content;
    LocalDateTime createTime;
    LocalDateTime updateTime;
    ArticleAccountVO articleAccountVO;
    List<CommentVO> comments;
}
