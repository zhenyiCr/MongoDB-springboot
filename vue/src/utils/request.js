import axios from "axios";
import {ElMessage} from "element-plus";

const request = axios.create({
    baseURL: 'http://localhost:8088',
    timeout: 30000
})

// request拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8'
    let user = JSON.parse(localStorage.getItem('user') || '{}')
    config.headers['token'] = user.token
    return config
}, error => {
    return Promise.reject(error)
});

// response拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 兼容服务器返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        return res;
    },
    error => {
        if (error.response.status === 404){
            ElMessage.error("未找到请求接口")
        } else if (error.response.status === 500){
            ElMessage.error("系统异常,请查看后端控制台摆错")
        } else {
            console.error(error.message)
        }
        return Promise.reject(error)
    }
)

export default request