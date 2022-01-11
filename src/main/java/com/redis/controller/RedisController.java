package com.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RedisController {

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("/lock")
    public void  lock(){
        //1获取锁，setne
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "111");
        //2获取锁成功、查询num的值
        if(lock){
            Object value = redisTemplate.opsForValue().get("num");
            //2.1判断num为空return
            if(StringUtils.isEmpty(value)){
                return;
            }
            //2.2有值就转成成int
            int num = Integer.parseInt(value+"");
            //2.3把redis的num加1
            redisTemplate.opsForValue().set("num", ++num);
            //2.4释放锁，del
            redisTemplate.delete("lock");

        }else{
            //3获取锁失败、每隔0.1秒再获取
            try {
                Thread.sleep(100);
                lock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
