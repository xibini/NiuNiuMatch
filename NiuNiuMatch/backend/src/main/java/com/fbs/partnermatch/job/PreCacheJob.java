package com.fbs.partnermatch.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fbs.partnermatch.model.domain.User;
import com.fbs.partnermatch.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

//缓存预热任务
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    //每天执行
    @Scheduled(cron = "0 15 0 * * ?") //秒、分、时、日、月、年
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("niuniu:precachejob:docache:lock");
        //只有一个线程能获取锁
        try {
            //如果抢到了锁
            //参数：1、waittime:0等待时间0，只能被获取一次  2、持有时间
            //第二个参数改为-1，看门狗机制，会自动续期（10s一次）
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                System.out.println("getlock");
                for(Long userId:mainUserList){
                    QueryWrapper queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);//分页
                    String redisKey = String.format("niuniu:user:recommend:%s",userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    //写缓存
                    try {
                        valueOperations.set(redisKey,userPage,30, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.error("redis get key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        }finally {
            //防止try中代码出错锁未释放，需要将这一段放在finally里
            //判断是不是当前线程加的锁，只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
                System.out.println("unlock");
            }
        }

    }
}
