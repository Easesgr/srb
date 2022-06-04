import request from '@/utils/request'
export default{
  getList(parentId) {
    return request({
      url: `/srb/admin/lend/list`,
      method: 'get'
    })
  },
  show(id){
    return request({
      url: `/srb/admin/lend/show/${id}`,
      method: 'get'
    })
  },
  makeLoan(id){
    return request({
      url: `/srb/admin/lend/makeLoan/${id}`,
      method: 'get'
    })
  },
  getLendItem(lendId){
    return request({
      url: `/srb/admin/lendItem/list/${lendId}`,
      method: 'get'
    })
  }
}