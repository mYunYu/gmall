package com.jju.gmall.pms.service;

import com.jju.gmall.pms.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jju.gmall.vo.PageInfoVo;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
public interface BrandService extends IService<Brand> {

    PageInfoVo brandPageInfo(String keyword, Integer pageNum, Integer pageSize);
}
