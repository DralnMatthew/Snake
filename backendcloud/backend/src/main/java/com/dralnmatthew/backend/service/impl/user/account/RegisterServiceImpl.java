package com.dralnmatthew.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dralnmatthew.backend.mapper.UserMapper;
import com.dralnmatthew.backend.pojo.User;
import com.dralnmatthew.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if (username == null) {
            map.put("error_message", "username cannot be empty");
            return map;
        }
        if (password == null || confirmedPassword == null) {
            map.put("error_message", "password cannot be empty");
            return map;
        }

        username = username.trim();
        if (username.length() == 0) {
            map.put("error_message", "username cannot be empty");
            return map;
        }

        if (password.length() == 0 || confirmedPassword.length() == 0) {
            map.put("error_message", "password cannot be empty");
            return map;
        }

        if (username.length() > 100) {
            map.put("error_message", "the length of username cannot be greater than 100");
            return map;
        }

        if (password.length() > 100 || confirmedPassword.length() > 100) {
            map.put("error_message", "the length of password cannot be greater than 100");
            return map;
        }

        if (!password.equals(confirmedPassword)) {
            map.put("error_message", "password unmatched");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()) {
            map.put("error_message", "username already exists");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://res.cloudinary.com/dbdezejym/image/upload/v1691787770/1_1db2488f17-anonymous_bldsqd.png";
        User user = new User(null, username, encodedPassword, photo, 1500);
        userMapper.insert(user);

        map.put("error_message", "success");
        return map;
    }
}
