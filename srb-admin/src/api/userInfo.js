import request from '@/utils/request'
export default{
  getList(page,limit,searchInfo) {
    return request({
      url: `srb/admin/userInfo/list/${page}/${limit}`,
      method: 'post',
      data: searchInfo
    })
  },
  changeStatus(userId,status) {
    return request({
      url: `srb/admin/userInfo/change/${userId}/${status}`,
      method: 'get',
    })
  },
  getLoginLog(userId){
    return request({
      url: `srb/admin/userLoginRecord/list/${userId}`,
      method: 'get',
    })
  }
} 