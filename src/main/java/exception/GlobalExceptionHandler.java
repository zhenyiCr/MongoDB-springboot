package exception;


import com.example.mongo_demo.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


//全局异常处理
@ControllerAdvice("com.example.controller")
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody  //将Result对象 转换成 json的格式
    public Result error(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常");
    }


    @ExceptionHandler(CustomerException.class)
    @ResponseBody  //将Result对象 转换成 json的格式
    public Result customerError(CustomerException e) {
        log.error("自定义异常", e);
        return Result.error(e.getCode(), e.getMsg());
    }


}
