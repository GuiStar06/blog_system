package com.guistar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guistar.entity.Article;
import com.guistar.mapper.ArticleMapper;
import com.guistar.service.ArticleService;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
}
