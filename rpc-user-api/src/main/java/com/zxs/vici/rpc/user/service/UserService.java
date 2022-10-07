package com.zxs.vici.rpc.user.service;

import com.zxs.vici.rpc.user.model.User;

public interface UserService {

    Integer add(User user);

    Integer delete(Integer id);

    Integer update(User user);

    User queryById(Integer id);
}
