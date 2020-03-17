package com.jju.gmall.admin.ums;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jju.gmall.to.CommonResult;
import com.jju.gmall.ums.entity.MemberLevel;
import com.jju.gmall.ums.service.MemberLevelService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {

    @Reference
    MemberLevelService memberLevelService;

    /**
     *  查出所有会员信息
     * @return
     */
    @GetMapping("/list")
    public Object memberLevelList(){
        List<MemberLevel> list = memberLevelService.list();
        return new CommonResult().success(list);
    }

}
