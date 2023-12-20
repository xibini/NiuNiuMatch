<script setup lang="ts">
import {useRouter} from "vue-router";

const user = {
  id: 1,
  username: '小象',
  userAccount: 'elephant',
  avatarUrl: '"C:/Users/向丙宁/Desktop/壁纸/头像.png"',
  gender: '女',
  phone: '13212691435',
  email: '3296952290@qq.com',
  planetCode: '1234',
  createTime: new Date(),
}

// 页面跳转，将参数editKey:属性 editName:标签 currentValue:当前值传过去
const router = useRouter();
const toEdit = (editKey: string, editName: string, currentValue: string) => {
    router.push({
      path: '/User/Edit',
      query: {
        editKey,
        editName,
        currentValue,
      }
    })
}
</script>

<template>
  <div class="userInfo">
    <van-cell title="昵称" is-link to="/User/Edit" :value="user.username" @click="toEdit('username','昵称', user.username)" />
    <van-cell title="账号" :value="user.userAccount" />
    <van-cell title="头像" is-link to="/User/Edit" :value="user.avatarUrl" @click="toEdit('avatarUrl','头像', user.avatarUrl)">
      <img :src="user.avatarUrl" style="height: 48px" />
    </van-cell>
    <van-cell title="性别" is-link to="/User/Edit" :value="user.gender"  @click="toEdit('gender','性别', user.gender)"/>
    <van-cell title="电话号码" is-link to="/User/Edit" :value="user.phone" @click="toEdit('phone','电话号码', user.phone)"/>
    <van-cell title="邮箱" is-link to="/User/Edit" :value="user.email" @click="toEdit('email','邮箱', user.email)"/>
    <van-cell title="星球编号" :value="user.planetCode" />
    <van-cell title="注册时间" :value="user.createTime.toISOString()" />
  </div>

</template>

<style scoped>
.userInfo {
  position: fixed;
  top: 46px;
  left: 0;
  right: 0;
}
</style>