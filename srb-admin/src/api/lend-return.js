import request from '@/utils/request'

export default {
  getList(lendId) {
    return request({
      url: `/srb/admin/lendReturn/list/` + lendId,
      method: 'get'
    })
  }
}