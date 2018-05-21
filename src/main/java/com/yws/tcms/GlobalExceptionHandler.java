package com.yws.tcms;

import javax.servlet.http.HttpServletRequest;

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

	/**
	 * 判断是否为ajax请求
	 * @param request
	 * @return
	 */
    public static boolean isAjax(HttpServletRequest request) {
        String ajaxHeader = request.getHeader("X-Requested-With");
        return (ajaxHeader != null && "XMLHttpRequest".equals(ajaxHeader));
    }
     
    /**
     * 异常处理，兼容web和ajax
     * @param request
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value=Exception.class)
    public Object errorHandler(HttpServletRequest request, Exception e) throws Exception {
    	LOGGER.error(e.getMessage());
        if(isAjax(request)) {
        	ResultJson resultJson = new ResultJson(false);
        	resultJson.setMsg(e.getMessage());
        	return resultJson;
        } else {
            ModelAndView mv = new ModelAndView();
            mv.addObject("msg",e.getMessage());
            mv.addObject("url", request.getRequestURL());
            mv.setViewName("error");
            return mv;
        }
    }

}
