import request from '@/utils/request'
export default{
  getByparentId(parentId) {
    return request({
      url: `/srb/admin/dict/${parentId}`,
      method: 'get'
    })
  },
}