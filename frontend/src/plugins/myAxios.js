import axios, {request} from "axios";

// 后台地址写在这
const myAxios = axios.create({
    baseURL: 'https://localhost:8080/api',
    timeout: 1000,
    headers: {'X-Custom-Header': 'foobar'}
});

// 添加请求拦截器
myaxios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么 向后台发
    console.log("我要发请求啦", request())
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
myaxios.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    console.log("我要接收请求啦", response)
    return response;
}, function (error) {
    // 对响应错误做点什么
    return Promise.reject(error);
});
export default myAxios;
