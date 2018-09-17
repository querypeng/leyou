package com.leyou.Filter;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @auther ff
 * @create 2018-08-06 16:50
 */

public class UserInterceptor implements HandlerInterceptor {


    private JwtProperties jwtProperties;

    public UserInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserInterceptor.class);

    //声明线程对象
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        //解析token
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //储存到当前线程
            tl.set(userInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析token失败"+ e);
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //执行结束,当前线程中删除
        tl.remove();
    }

    public static UserInfo getUser(){
        return tl.get();
    }
}
