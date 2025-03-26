package com.guistar.controller;

import com.guistar.dto.CommentDTO;
import com.guistar.entity.Comment;
import com.guistar.entity.utils.RestBean;
import com.guistar.service.CommentService;
import com.guistar.vo.CommentVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/comments")
public class CommentController {

    @Resource
    CommentService commentService;

    @PostMapping
    public RestBean<CommentVO> createComment(@RequestBody @Valid CommentDTO commentDTO){
        if(commentDTO.getArticleId() == null) return RestBean.illegalArgs("文章id不能为空");
        return RestBean.success(commentService.convertToCommentVO(commentService.convertToComment(commentDTO)));
    }


    @GetMapping("/{id}")
    public RestBean<CommentVO> getCommentVOById(@RequestParam @PathVariable Long id){
        if(id == null) return RestBean.illegalArgs("评论id不能为空");
        Comment comment = commentService.findCommentById(id);
        if(comment == null) return RestBean.failure(400,"评论不存在");
        return RestBean.success(commentService.convertToCommentVO(comment));
    }

    @GetMapping("/{id}")
    public RestBean<Boolean> deleteCommentById(@RequestParam @PathVariable Long id){
        if(id == null) return RestBean.illegalArgs("评论id不能为空");
        return RestBean.success(commentService.deleteCommentById(id));
    }
}
