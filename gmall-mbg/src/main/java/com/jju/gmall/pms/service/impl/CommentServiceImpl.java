package com.jju.gmall.pms.service.impl;

import com.jju.gmall.pms.entity.Comment;
import com.jju.gmall.pms.mapper.CommentMapper;
import com.jju.gmall.pms.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评价表 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-16
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
