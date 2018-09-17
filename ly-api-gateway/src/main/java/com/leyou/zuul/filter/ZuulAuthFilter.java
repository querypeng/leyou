package com.leyou.zuul.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils2;
import com.leyou.zuul.config.FilterProperties;
import com.leyou.zuul.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @auther ff
 * @create 2018-08-05 15:20
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class ZuulAuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        //获取request对象路径
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String requestPath = request.getRequestURI();
        //判断是否拦截
        return !isAllowPath(requestPath); //true,必须拦截
    }

    private boolean isAllowPath(String requestPath) {
        //请求路径是否为空
        if (StringUtils.isBlank(requestPath)) {
            return false;
        }
        List<String> allowPaths = filterProperties.getAllowPaths();
        //判断allowPaths是否为空
        if (CollectionUtils.isEmpty(allowPaths)) {
            return false;
        }
        for (String allowPath : allowPaths) {
            //判断是否以允许的路径开头
            if (requestPath.startsWith(allowPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文request对象
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //解析token
        //通过cookiename获取token
        String token = CookieUtils2.getCookieValue(request, jwtProperties.getCookieName());
        if (StringUtils.isBlank(token)) {
            //设置上下文状态码未授权
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            //设置阻止网关响应
            ctx.setSendZuulResponse(false);
        }
        try {
            return JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //设置上下文状态码未授权
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            //设置阻止网关响应
            ctx.setSendZuulResponse(false);
        }
        return null;
    }
}
