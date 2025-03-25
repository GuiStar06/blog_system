package com.guistar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guistar.entity.Comment;
import com.guistar.mapper.CommentMapper;
import com.guistar.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
