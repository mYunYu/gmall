package com.jju.gmall.ums.service;

import com.jju.gmall.ums.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
public interface AdminService extends IService<Admin> {

    Admin login(String username, String password);

    Admin getUserInfo(String userName);
}
