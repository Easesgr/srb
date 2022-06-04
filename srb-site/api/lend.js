import request from '@/utils/request'
export default{
  getList() {
    return request({
      url: `/srb/front/lend/list`,
      method: 'get'
    })
  },
  show(id) {
    return request({
      url: `/srb/front/lend/show/${id}`,
      method: 'get'
    })
  },
  getCount(){
    return request({
      url: `/srb/front/lend/getCount`,
      method: 'get'
    })
  },
  getInterestCount(invest,yearRate,totalMonth,returnMethod){
    return request({
      url: `/srb/front/lend/getInterestCount/${invest}/${yearRate}/${totalMonth}/${returnMethod}`,
      method: 'get'
    })
  },
  commitInvest(investVO){
    return request({
      url: `/srb/front/lendItem/commitInvest`,
      method: 'post',
      data:investVO
    })
  },
  getLendItem(id){
    return request({
      url: `/srb/front/lendItem/list/${id}`,
      method: 'get'
    })
  },
  getLendReturnList(id){
    return request({
      url: `/srb/front/lendReturn/list/${id}`,
      method: 'get'
    })
  },
  getLendItemReturnList(id){
    return request({
      url: `/srb/front/lendItemReturn/list/${id}`,
      method: 'get'
    })
  },
  commitReturn(lendReturnId){
    return request({
      url: `/srb/front/lendReturn/commitReturn/${lendReturnId}`,
      method: 'post'
    })
  }
}