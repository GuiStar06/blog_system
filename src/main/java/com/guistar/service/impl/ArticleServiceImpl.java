package com.guistar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guistar.dto.ArticleDTO;
import com.guistar.entity.Article;
import com.guistar.entity.utils.Const;
import com.guistar.mapper.ArticleMapper;
import com.guistar.service.AccountService;
import com.guistar.service.ArticleService;
import com.guistar.service.CommentService;
import com.guistar.utils.PermissionUtils;
import com.guistar.utils.SecurityUtils;
import com.guistar.vo.AccountVO;
import com.guistar.vo.ArticleVO;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
    @Autowired
    private PermissionUtils permissionUtils;


    @Override
    public ArticleVO createArticle(ArticleDTO articleDTO) {
        return convertToArticleVO(convertToArticle(articleDTO));
    }

    @Override
    public ArticleVO getArticleVOById(Long id) {
        return convertToArticleVO(baseMapper.selectById(id));
    }



    @Override
    public List<ArticleVO> getAllArticlesVO(int pageNum, int pageSize) {
        Page<Article> page = new Page<>(pageNum,pageSize);
        Page<Article> articlePage = baseMapper.selectPage(page,new LambdaQueryWrapper<Article>().orderByDesc(Article::getCreateTime));
        return articlePage.convert(this::convertToArticleVO).getRecords().stream().toList();
    }

    @Override
    public List<ArticleVO> getArticlesVOByAccountId(Long accountId) {
        List<Article> articleList = getArticlesByAccountId(accountId);
        return articleList.stream().map(this::convertToArticleVO).toList();
    }

    @Override
    public ArticleVO updateArticle(Long articleId, ArticleDTO articleDTO) {

        if(articleId == null) {
            throw new IllegalArgumentException("文章id不能为空");
        }
        if(articleDTO == null) throw new IllegalArgumentException("文章内容不能为空");
        //作者有权更新
        if(!permissionUtils.isAuthor(securityUtils.getCurrentAccountId(),getArticleById(articleId).getAccountId())){
            throw new AccessDeniedException("无权更新文章");
        }
        Article article = convertToArticle(articleDTO);
        boolean update = baseMapper.update(article, new LambdaQueryWrapper<Article>().eq(Article::getId,articleId)) > 0;
        if(!update){
            throw new RuntimeException("更新失败");
        }
        return convertToArticleVO(article);
    }

    @Override
    public boolean deleteArticle(Long articleId) {
        if(articleId == null) throw new IllegalArgumentException("文章id不能为空");
        if(!permissionUtils.isAdminOrAuthor(securityUtils.getCurrentAccountId(),getArticleById(articleId).getAccountId())){
            throw new RuntimeException("无权操作");
        }
        return baseMapper.deleteById(articleId) > 0;
    }

    @Override
    public List<ArticleVO> searchArticles(String keywords, int pageNum, int pageSize) {
        if(pageNum <= 0 || pageSize <= 0) throw new IllegalArgumentException("分页参数不合法");

        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        if(StringUtils.isBlank(keywords)){
            throw new IllegalArgumentException("请输入关键词");
        }else {
        wrapper.like("title",keywords)
                .or().like("content",keywords)
                .or().like("nickname",keywords)
                .or().like("username",keywords).orderByDesc("create_time");
        }
        Page<Article> articlePage = baseMapper.selectPage(new Page<>(pageNum,pageSize), wrapper);
        return articlePage.convert(this::convertToArticleVO).getRecords().stream().toList();
    }

    @Override
    public Long getArticleCountByAcId(Long accountId) {
        List<Article> articleList = getArticlesByAccountId(accountId);
        return (long) articleList.size();
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

    private Article getArticleById(Long id){
        return baseMapper.selectById(id);
    }

    private List<Article> getAllArticles(int pageNum,int pageSize){
        if(pageNum <= 0 || pageSize <= 0){
            throw new IllegalArgumentException("分页参数不合法");
        }
        Page<Article> page = new Page<>(pageNum,pageSize);
        return baseMapper.selectPage(page,null).getRecords().stream().toList();
    }

    private List<Article> getArticlesByAccountId(Long accountId){
        if(accountId == null) return getAllArticles(Const.DEFAULT_PAGE_NUM, Const.DEFAULT_PAGE_SIZE);
        Page<Article> page = new Page<>(Const.DEFAULT_PAGE_NUM, Const.DEFAULT_PAGE_SIZE);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getAccountId,accountId);
        Page<Article> articlePage = baseMapper.selectPage(page, queryWrapper);
        return articlePage.getRecords().stream().toList();
    }
}
