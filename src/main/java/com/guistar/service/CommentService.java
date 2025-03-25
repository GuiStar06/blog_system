package com.guistar.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guistar.dto.CommentDTO;
import com.guistar.entity.Comment;
import com.guistar.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    CommentVO createComment(CommentDTO commentDTO);
    Comment getCommentById(Long id);
    CommentVO convertToCommentVO(Comment comment);
    List<CommentVO> listCommentByArticleId(Long articleId);
    Page<CommentVO> pageCommentVOByArticleId(int pageNum,int pageSize,Long articleId);
    boolean deleteCommentById(Long commentId);
}
