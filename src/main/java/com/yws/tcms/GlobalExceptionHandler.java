package com.yws.tcms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.yws.tcms.web.vo.ResultJson;

/**
 * 全局异常处理类，基于AOP实现。
 * 
 * @author yws710
 *
 */
@ControllerAdvice(basePackages = { "com.yws.tcms.web" })
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// 判断是否是ajax请求
    public static boolean isAjax(HttpServletRequest req) {
        String ajaxHeader = req.getHeader("X-Requested-With");
        return (ajaxHeader != null && "XMLHttpRequest".equals(ajaxHeader));
    }
     
    // 异常处理，兼容web和ajax
    @ExceptionHandler(value=Exception.class)
    public Object errorHandler(HttpServletRequest req, HttpServletResponse res, Exception e) throws Exception {
    	LOGGER.error(e.getMessage());
        if(isAjax(req)) {
        	ResultJson resultJson = new ResultJson(false);
        	resultJson.setMsg(e.getMessage());
        	return resultJson;
        } else {
            ModelAndView mv = new ModelAndView();
            mv.addObject("msg",e.getMessage());
            mv.addObject("url", req.getRequestURL());
            mv.setViewName("error");
            return mv;
        }
    }

}
