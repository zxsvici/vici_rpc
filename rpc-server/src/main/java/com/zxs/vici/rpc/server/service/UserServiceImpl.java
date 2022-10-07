package com.zxs.vici.rpc.server.service;

import com.zxs.vici.rpc.common.anno.RpcService;
import com.zxs.vici.rpc.user.model.User;
import com.zxs.vici.rpc.user.service.UserService;

@RpcService(name = "userService", version = "1.0")
public class UserServiceImpl implements UserService {

    @Override
    public Integer add(User user) {
        return 1;
    }

    @Override
    public Integer delete(Integer id) {
        return 2;
    }

    @Override
    public Integer update(User user) {
        return 3;
    }

    @Override
    public User queryById(Integer id) {
        return User.builder().id(4).name("4").sex(2).build();
    }
}
