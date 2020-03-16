package com.jju.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jju.gmall.pms.entity.Brand;
import com.jju.gmall.pms.mapper.BrandMapper;
import com.jju.gmall.pms.service.BrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jju.gmall.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
@Component
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Autowired
    BrandMapper brandMapper;

    @Override
    public PageInfoVo brandPageInfo(String keyword, Integer pageNum, Integer pageSize) {

        QueryWrapper<Brand> queryWrapper = null;
        //自动拼接%
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper = new QueryWrapper<Brand>().like("name", keyword);
        }
        IPage<Brand> page = brandMapper.selectPage(new Page<Brand>(pageNum.longValue(), pageSize.longValue()),
                queryWrapper);

        PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(), page.getPages(),
                pageSize.longValue(), page.getRecords(), page.getCurrent());

        return pageInfoVo;
    }
}
