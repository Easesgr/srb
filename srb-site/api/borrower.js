import request from '@/utils/request'
export default{
  getCode(phone) {
    return request({
      url: `/api/sms/send/${phone}`,
      method: 'get'
    })
  },
  removeImg(fileName){
    return request({
      url: `/srb/oss/deleteFile?fileName=${fileName}`,
      method: 'post'
    })
  },
  listByDictCode(dictCode){
    return request({
      url: `/srb/front/dict/${dictCode}`,
      method: 'get'
    })
  },
  save(borrowerInfo){
    return request({
      url: `/srb/front/borrower/save`,
      method: 'post',
      data: borrowerInfo
    })
  },
  getStatus(){
    return request({
      url: `/srb/front/borrower/checkStatus`,
      method: 'get',
    })
  }
}