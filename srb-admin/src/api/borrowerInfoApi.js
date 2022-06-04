import request from '@/utils/request'
export default{
  getInfoList(page,limit,keyword) {
    return request({
      url: `srb/admin/borrowInfo/list`,
      method: 'get'
    })
  },
  show(id) {
    return request({
      url: `srb/admin/borrowInfo/show/${id}`,
      method: 'get'
    })
  },
  approval(approvalVo) {
    return request({
      url: `srb/admin/borrowInfo/approval`,
      method: 'post',
      data:approvalVo
    })
  },
}