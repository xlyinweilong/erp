package com.yin.erp.base.interceptor;

import com.yin.common.anno.LoginAnno;
import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 登录拦截器
 *
 * @author yin.weilong
 * @date 2018.12.21
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LoginService userService;

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        LoginAnno loginAnno = method.getAnnotation(LoginAnno.class);
        if (loginAnno == null) {
            UserSessionBo userSessionBo = userService.getUserSession(request);
            //判断userSessionBo是否含有角色，没有获取一下
            if (userSessionBo == null) {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\": 50008,\"message\":\"已经登出\"}");
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}
