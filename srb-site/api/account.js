import request from '@/utils/request'
export default{
  commitCharge(chargeAmt) {
    return request({
      url: `/srb/front/account/commitCharge/${chargeAmt}`,
      method: 'post'
    })
  },
  commitWithdraw(fetchAmt){
    return request({
      url: `/srb/front/account/commitWithdraw/${fetchAmt}`,
      method: 'post'
    })
  }
}