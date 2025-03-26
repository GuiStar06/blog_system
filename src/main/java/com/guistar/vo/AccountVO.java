package com.guistar.vo;

import java.util.Date;
import java.util.List;

public class AccountVO {
    String username;
    String nickname;
    String avatar;
    String bio;
    Date registerTime;
    String articleCount;
    List<ArticleVO> articleVOS;
}
