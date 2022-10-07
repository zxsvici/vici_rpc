package com.zxs.vici.rpc.client.controller;

import com.zxs.vici.rpc.common.anno.RpcAutowired;
import com.zxs.vici.rpc.user.model.User;
import com.zxs.vici.rpc.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @RpcAutowired(name = "userService", version = "1.0")
    private UserService service;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public Integer add(User user) {
        return service.add(user);
    }
}
