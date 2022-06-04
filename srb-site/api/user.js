import request from '@/utils/request'
export default{
  getCode(phone) {
    return request({
      url: `/api/sms/send/${phone}`,
      method: 'get'
    })
  },
  register(registerInfo){
    return request({
      url: `/srb/front/user/info/register`,
      method: 'post',
      data:registerInfo
    })
  },
  login(userInfo){
    return request({
      url: `/srb/front/user/info/login`,
      method: 'post',
      data:userInfo
    })
  },
  checkToken(phone){
    return request({
      url: `/srb/front/user/info/checkToken/${phone}`,
      method: 'get'
    })
  },
  userBind(userBindInfo){
    return request({
      url: `/srb/front/userBind/auth/bind`,
      method: 'post',
      data: userBindInfo
    })
  },
  getTranFlowList(){
    return request({
      url: `/srb/front/transFlow`,
      method: 'get',
    })
  },
  getInfo(){
    return request({
      url: `/srb/front/user/info/getIndexUserInfo`,
      method: 'get',
    })
  }
}