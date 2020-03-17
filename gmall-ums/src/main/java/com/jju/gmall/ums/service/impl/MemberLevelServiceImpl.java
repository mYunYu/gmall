package com.jju.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jju.gmall.ums.entity.MemberLevel;
import com.jju.gmall.ums.mapper.MemberLevelMapper;
import com.jju.gmall.ums.service.MemberLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 会员等级表 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
@Component
@Service
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

}
