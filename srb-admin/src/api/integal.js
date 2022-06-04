import request from '@/utils/request'
export default{
  getIntergalList() {
    return request({
      url: `/srb/admin/integralGrade/list`,
      method: 'get'
    })
  },
  saveIntergal(info) {
    return request({
      url: `/srb/admin/integralGrade/save`,
      method: 'post',
      data:info
    })
  },  
  updateIntergal(info) {
    return request({
      url: `/srb/admin/integralGrade/update`,
      method: 'put',
      data:info
    })
  },
  getIntergalById(id) {
    return request({
      url: `/srb/admin/integralGrade/${id}`,
      method: 'get'
    })
  },
  deleteIntergalById(id) {
    return request({
      url: `/srb/admin/integralGrade/${id}`,
      method: 'delete'
    })
  } 
}