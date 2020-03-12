package com.jju.gmall.oms.service.impl;

import com.jju.gmall.oms.entity.Order;
import com.jju.gmall.oms.mapper.OrderMapper;
import com.jju.gmall.oms.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
