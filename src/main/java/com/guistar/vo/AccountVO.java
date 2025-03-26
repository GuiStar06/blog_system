package com.guistar.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Data
public class AccountVO implements Serializable {
    String username;
    String nickname;
    String avatar;
    String bio;
    Date registerTime;
    String articleCount;
    List<ArticleVO> articleVOS;
}
