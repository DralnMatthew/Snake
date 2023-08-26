package com.dralnmatthew.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dralnmatthew.backend.mapper.BotMapper;
import com.dralnmatthew.backend.pojo.Bot;
import com.dralnmatthew.backend.pojo.User;
import com.dralnmatthew.backend.service.impl.utils.UserDetailsImpl;
import com.dralnmatthew.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (title == null || title.length() == 0) {
            map.put("error_message", "Title cannot be empty");
            return map;
        }

        if (title.length() > 100) {
            map.put("error_message", "The length of title cannot be greater than 100");
            return map;
        }

        if (description == null || description.length() == 0) {
            description = "...";
        }

        if (description.length() > 300) {
            map.put("error_message", "The description of bot cannot be greater than 300");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("error_message", "Content cannot be empty");
            return map;
        }

        if (content.length() > 10000) {
            map.put("error_message", "The length of content cannot be greater than 10000");
            return map;
        }

        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        if (botMapper.selectCount(queryWrapper) >= 10) {
            map.put("error_message", "Every user can only create at most 10 bots");
            return map;
        }

        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, now, now);

        botMapper.insert(bot);
        map.put("error_message", "success");

        return map;
    }
}
