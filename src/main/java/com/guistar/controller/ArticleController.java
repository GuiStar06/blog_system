package com.guistar.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guistar.dto.ArticleDTO;
import com.guistar.entity.utils.Const;
import com.guistar.entity.utils.RestBean;
import com.guistar.service.ArticleService;
import com.guistar.service.CommentService;
import com.guistar.vo.ArticleVO;
import com.guistar.vo.CommentVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/article")
@RestController
@Validated
public class ArticleController {


    @Resource
    ArticleService articleService;

    @Resource
    CommentService commentService;

    @PostMapping("/create-article")
    public RestBean<ArticleVO> createArticle(@RequestBody @Valid ArticleDTO articleDTO) {
        if(articleDTO == null) return RestBean.illegalArgs("文章不能为空");
        return RestBean.success(articleService.convertToArticleVO(articleService.convertToArticle(articleDTO)));
    }

    @GetMapping("/get-article/{id}")
    public RestBean<ArticleVO> getArticleById(@RequestParam @PathVariable Long id){
        if(id == null) return RestBean.illegalArgs("文章id不能为空");
        return RestBean.success(articleService.getArticleVOById(id));
    }

    @GetMapping("/")
    public RestBean<List<ArticleVO>> getAllArticlesVO(@RequestParam(defaultValue = "1") int pageNum,
                                                      @RequestParam(defaultValue = "10") int pageSize){
        if(pageNum <= 0 || pageSize <= 0){
            return RestBean.illegalArgs("分页参数不合法");
        }
        return RestBean.success(articleService.getAllArticlesVO(pageNum,pageSize));
    }

    @GetMapping("/get-articles/{accountId}")
    public RestBean<List<ArticleVO>> getArticlesVOByAccountId(@RequestParam @PathVariable Long accountId){
        if(accountId == null) return RestBean.illegalArgs("用户id不能为空");
        return RestBean.success(articleService.getArticlesVOByAccountId(accountId));
    }

    @PutMapping("/update-article/{id}")
    public RestBean<ArticleVO> updateArticleById(@RequestParam @PathVariable Long id,
                                                 @RequestBody @Valid ArticleDTO articleDTO){
        if(id == null) return RestBean.illegalArgs("文章id不能为空");
        ArticleVO vo = articleService.updateArticle(id,articleDTO);
        if(vo == null) return RestBean.failure(400,"文章更新失败");
        return RestBean.success(vo);
    }

    @DeleteMapping("/delete-article/{id}")
    public RestBean<Boolean> deleteArticleById(@RequestParam @PathVariable Long id){
        if(id == null) return RestBean.illegalArgs("文章id不能为空");
        if(!articleService.deleteArticle(id)) return RestBean.failure(400,"文章删除失败");
        return RestBean.success();
    }

    @GetMapping("/search-articles")
    public RestBean<List<ArticleVO>> searchArticles(@RequestParam(required = false) String keywords,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize){
        if(pageNum <= 0 || pageSize <= 0)  return RestBean.illegalArgs("分页参数不合法");
        if(keywords == null) return RestBean.illegalArgs("请输入关键词");
        return RestBean.success(articleService.searchArticles(keywords,pageNum,pageSize));
    }

    @GetMapping("/{articleId}/list-comments")
    public RestBean<List<CommentVO>> listCommentsByArticleId(@RequestParam @PathVariable Long articleId){
        if(articleId == null) return RestBean.illegalArgs("文章id不能为空");
        return RestBean.success(commentService.listCommentByArticleId(articleId));
    }

    @GetMapping("/{articleId}/page-comments")
    public RestBean<Page<CommentVO>> pageCommentsByArticleId(@RequestParam @PathVariable Long articleId){
        if(articleId == null) throw new IllegalArgumentException("文章id不能为空");
        return RestBean.success(commentService.pageCommentVOByArticleId(Const.DEFAULT_PAGE_NUM,Const.DEFAULT_PAGE_SIZE,articleId));
    }


}
