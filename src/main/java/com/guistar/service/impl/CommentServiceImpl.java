package com.guistar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guistar.dto.CommentDTO;
import com.guistar.entity.Account;
import com.guistar.entity.Comment;
import com.guistar.mapper.CommentMapper;
import com.guistar.service.AccountService;
import com.guistar.service.CommentService;
import com.guistar.utils.PermissionUtils;
import com.guistar.utils.SecurityUtils;
import com.guistar.vo.AccountVO;
import com.guistar.vo.CommentVO;
import jakarta.annotation.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    SecurityUtils securityUtils;

    @Resource
    AccountService accountService;

    @Resource
    PermissionUtils permissionUtils;

    @Override
    public CommentVO createComment(CommentDTO commentDTO) {
        Long currentAccountId = securityUtils.getCurrentAccountId();
        Comment comment = commentDTO.asViewObj(Comment.class,comment1 -> comment1.setId(null));
        comment.setAccountId(currentAccountId);
        if(!this.save(comment)) throw new RuntimeException("评论保存失败");
        return convertToCommentVO(comment);
    }

    @Override
    public Comment getCommentById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public CommentVO convertToCommentVO(Comment comment) {
        Long currentAccountId = securityUtils.getCurrentAccountId();
        Account ac = accountService.findAccountById(currentAccountId);
        AccountVO author = accountService.convertToAccountVO(ac);
        return comment.asViewObj(CommentVO.class,commentVO -> commentVO.setAuthor(author));
    }

    @Override
    public List<CommentVO> listCommentByArticleId(Long articleId) {
        if(articleId == null) throw new IllegalArgumentException("文章id不能为空");
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,articleId)
                .orderByDesc(Comment::getCreateTime);
        return baseMapper.selectList(queryWrapper).stream().map(this::convertToCommentVO).toList();
    }

    @Override
    public Page<CommentVO> pageCommentVOByArticleId(int pageNum, int pageSize,Long articleId) {
        if(articleId == null) throw new IllegalArgumentException("文章id不能为空");
        if(pageNum <= 0 || pageSize <= 0) throw new RuntimeException("分页格式不合法");
        Page<Comment> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,articleId)
                .orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = baseMapper.selectPage(page,queryWrapper);
        return (Page<CommentVO>) commentPage.convert(this::convertToCommentVO);
    }

    @Override
    public boolean deleteCommentById(Long commentId) {
        if(commentId == null) throw new IllegalArgumentException("评论参数不能为空");
        Comment comment = findCommentById(commentId);
        if(comment == null) throw new RuntimeException("评论不存在");
        Long currentAccountId = securityUtils.getCurrentAccountId();
        Long authorId = comment.getAccountId();
        if(!permissionUtils.isAdminOrAuthor(currentAccountId,authorId)){
            throw new AccessDeniedException("无权删除文章");
        }
        return baseMapper.deleteById(commentId) > 0;
    }

    private Comment findCommentById(Long commentId){
        return this.query().eq("id",commentId).one();
    }
}
