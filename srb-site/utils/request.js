import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import cookie from 'js-cookie'
// 创建axios实例
const service = axios.create({
baseURL: 'http://localhost:8888', // api的base_url
timeout: 20000 // 请求超时时间
})

// http request 拦截器
service.interceptors.request.use(
  config => {
  //debugger
  if (cookie.get('token')) {
    config.headers['token'] = cookie.get('token');
  }
    return config
  },
  err => {
  return Promise.reject(err);
  })
  // http response 拦截器
  service.interceptors.response.use(
  response => {
    if (response.data.code === 0) {
      return response.data; 
    } else if (response.data.code === -211) {
     
      // debugger
      cookie.set('userInfo', '')
      window.location.href = '/login'
    } else {
      Message({
        message: response.data.message,
        type: 'error',
        duration: 5 * 1000,
      })
      return Promise.reject(response)
    }
    },
    error => {
     return Promise.reject(error.response) // 返回接口返回的错误信息
    });
export default service