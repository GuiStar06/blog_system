package com.guistar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guistar.dto.ArticleDTO;
import com.guistar.entity.Article;
import com.guistar.vo.ArticleVO;

import java.util.List;

public interface ArticleService extends IService<Article> {
    ArticleVO createArticle(ArticleDTO articleDTO,Long accountId);
    ArticleVO getArticleById(Long id);
    List<ArticleVO> getAllArticles(int pageNum,int pageSize);
    List<ArticleVO> getArticlesByAccountId(Long accountId);
    ArticleVO updateArticle(Long articleId, ArticleDTO articleDTO);
    boolean deleteArticle(Long id);
    List<ArticleVO> searchArticles(String keywords,int pageNum,int pageSize);
    Long getArticleCountByAcId(Long id);
    Article convertToArticle(ArticleDTO articleDTO);
    ArticleVO convertToArticleVO(Article article);
}
