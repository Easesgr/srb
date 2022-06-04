import request from '@/utils/request'
export default{
  getPageList(page,limit,keyword) {
    return request({
      url: `/srb/admin/borrower/list/${page}/${limit}?keyword=${keyword}`,
      method: 'get'
    })
  },
  show(id) {
    return request({
      url: `/srb/admin/borrower/show/${id}`,
      method: 'get'
    })
  },
  approval(approvalInfo) {
    return request({
      url: `/srb/admin/borrower/approval`,
      method: 'post',
      data: approvalInfo
    })
  },
  getByparentId(parentId) {
    return request({
      url: `/srb/admin/dict/${parentId}`,
      method: 'get'
    })
  },
}