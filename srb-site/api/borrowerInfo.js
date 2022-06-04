import request from '@/utils/request'
export default{
  getBorrowCount() {
    return request({
      url: `/srb/front/borrowerInfo/getMoney`,
      method: 'get'
    })
  },
  save(borrowerInfo){
    return request({
      url: `/srb/front/borrowerInfo/save`,
      method: 'post',
      data:borrowerInfo
    })
  },
  checkStatus(){
    return request({
      url: `/srb/front/borrowerInfo/checkStatus`,
      method: 'get',
    })
  }
}