package com.guistar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guistar.dto.ArticleDTO;
import com.guistar.entity.Article;
import com.guistar.mapper.ArticleMapper;
import com.guistar.service.AccountService;
import com.guistar.service.ArticleService;
import com.guistar.service.CommentService;
import com.guistar.utils.SecurityUtils;
import com.guistar.vo.AccountVO;
import com.guistar.vo.ArticleVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    SecurityUtils securityUtils;

    @Resource
    AccountService accountService;

    @Resource
    CommentService commentService;


    @Override
    public ArticleVO createArticle(ArticleDTO articleDTO, Long accountId) {
        return null;
    }

    @Override
    public ArticleVO getArticleById(Long id) {
        return null;
    }

    @Override
    public List<ArticleVO> getAllArticles(int pageNum, int pageSize) {
        return List.of();
    }

    @Override
    public List<ArticleVO> getArticlesByAccountId(Long accountId) {
        return List.of();
    }

    @Override
    public ArticleVO updateArticle(Long articleId, ArticleDTO articleDTO) {
        return null;
    }

    @Override
    public boolean deleteArticle(Long id) {
        return false;
    }

    @Override
    public List<ArticleVO> searchArticles(String keywords, int pageNum, int pageSize) {
        return List.of();
    }

    @Override
    public Long getArticleCountByAcId(Long id) {
        return 0L;
    }

    @Override
    public Article convertToArticle(ArticleDTO articleDTO) {
        Article article = articleDTO.asViewObj(Article.class,article1 -> article1.setId(null));
        article.setAccountId(securityUtils.getCurrentAccountId());
        return article;
    }

    @Override
    public ArticleVO convertToArticleVO(Article article) {
        AccountVO author = accountService.convertToAccountVO(accountService.findAccountById(article.getAccountId()));
        ArticleVO vo = article.asViewObj(ArticleVO.class,articleVO -> articleVO.setId(null));
        vo.setAuthor(author);
        vo.setComments(commentService.listCommentByArticleId(article.getId()));
        return vo;
    }
}
