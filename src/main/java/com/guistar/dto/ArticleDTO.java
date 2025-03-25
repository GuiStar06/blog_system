package com.guistar.dto;

import com.guistar.entity.utils.BaseData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleDTO implements BaseData {
    @NotBlank(message = "标题不能为空")
    @Size(max = 100,message = "标题长度不能超过100个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
}
