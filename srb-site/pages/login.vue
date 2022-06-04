<template>
  <!--登录-->
  <div class="wrap">
    <div class="tdbModule loginPage">
      <div class="registerTitle">用户登录</div>
      <div class="registerCont">
        <ul>
          <li>
            <span class="dis"></span>
            <input v-model="userInfo.userType" type="radio" value="1" />
            投资人
            <input v-model="userInfo.userType" type="radio" value="2" />
            借款人
          </li>
          <li>
            <span class="dis">手机号：</span>
            <input class="input" v-model="userInfo.mobile" />
          </li>

          <li>
            <span class="dis">密码：</span>
            <input class="input" v-model="userInfo.password" type="password" />
          </li>
          <li class="btn">
            <button @click="login()" :class="{ disabled: !isValid }">
              立即登录
            </button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import '~/assets/css/register.css'
import cookie from 'js-cookie'
import userApi from '~/api/user.js'
export default {
  data() {
    return {
      userInfo: {
        userType: 1,
      },
      isValid: true, //表单校验是否成功
    }
  },

  methods: {
    //登录
    login() {
      userApi.login(this.userInfo).then((res) => {
        cookie.set('userInfo', res.data.userInfo)
        cookie.set('token', res.data.userInfo.token)
        this.$message({
          message: '登录成功',
          type: 'success',
        })
        window.location.href = '/user'
      })
    },
  },
}
</script>
