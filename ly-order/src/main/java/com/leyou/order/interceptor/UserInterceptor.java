package com.leyou.order.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.test.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: xiepanpan
 * @Date: 2019/8/18
 * @Description:
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    public UserInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            //解析用户信息
            UserInfo user = JwtUtils.getUserInfo(jwtProperties.getPublicKey(), token);

            //传递用户信息  存到threadlocal中 保证线程安全
            threadLocal.set(user);
            return true;
        } catch (Exception e) {
            log.error("[购物车服务] 解析用户身份失败：",e);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 视图渲染完调用
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //使用完 清除数据
        threadLocal.remove();
    }

    /**
     * 获取用户信息
     * @return
     */
    public static UserInfo getUser() {
        return threadLocal.get();
    }
}
