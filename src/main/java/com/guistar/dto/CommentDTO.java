package com.guistar.dto;

import com.guistar.entity.utils.BaseData;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO implements BaseData {
    @NotBlank(message = "评论内容不能为空")
    private String content;

    @NotBlank(message = "文章id不能为空")
    private Long articleId;
}
