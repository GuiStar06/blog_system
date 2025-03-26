package com.guistar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guistar.dto.ArticleDTO;
import com.guistar.entity.Article;
import com.guistar.vo.ArticleVO;

import java.util.List;

public interface ArticleService extends IService<Article> {
    ArticleVO createArticle(ArticleDTO articleDTO);
    ArticleVO getArticleVOById(Long id);
    List<ArticleVO> getAllArticlesVO(int pageNum,int pageSize);
    List<ArticleVO> getArticlesVOByAccountId(Long accountId);
    ArticleVO updateArticle(Long articleId, ArticleDTO articleDTO);
    boolean deleteArticle(Long articleId);
    List<ArticleVO> searchArticles(String keywords,int pageNum,int pageSize);
    Long getArticleCountByAcId(Long accountId);
    Article convertToArticle(ArticleDTO articleDTO);
    ArticleVO convertToArticleVO(Article article);
}
