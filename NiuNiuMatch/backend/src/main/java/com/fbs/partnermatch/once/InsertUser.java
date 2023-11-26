package com.fbs.partnermatch.once;
import java.util.Date;

import com.fbs.partnermatch.mapper.UserMapper;
import com.fbs.partnermatch.model.domain.User;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUser {

    @Resource
    public UserMapper userMapper;

    /**
    * 批量插入用户
    */
    //@Scheduled(fixedDelay = 5000)
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for(int i=0;i<INSERT_NUM;i++){
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("testuser");
            user.setAvatarUrl("https://www.yw11.com/uploads/allimg/160321/11-1603211J15H02.jpg");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("111111");
            user.setTags("[]");

            userMapper.insert(user);

        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
