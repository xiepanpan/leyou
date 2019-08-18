package com.leyou.gateway.filters;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.test.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.jnlp.FileContents;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: xiepanpan
 * @Date: 2019/8/18
 * @Description:
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilters extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private FilterProperties filterProperties;

    /**
     * 设置过滤器类型 前置过滤器
     * @return
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 在官方过滤器前面拦截
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
    }

    /**
     * 是否过滤
     * @return
     */
    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        String path = request.getRequestURI();
        return !isAllowPath(path);
    }

    private boolean isAllowPath(String path) {
        for (String allowpath:filterProperties.getAllowPaths()) {
            //路径以这个开头
            if (path.startsWith(allowpath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拦截处理
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取cookie
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            //解析cookie
            UserInfo userInfo = JwtUtils.getUserInfo(jwtProperties.getPublicKey(), token);
        } catch (Exception e) {
            //解析token失败 说明未登陆 拦截
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);

        }
        //权限验证
        return null;
    }
}
