<script setup lang="ts">
// import {ref} from "vue";
import {showToast} from "vant";
import {useRouter} from "vue-router";

const router = useRouter();
const onClickLeft = () =>
{
  router.push('/')
};
const onClickRight = () => {
  router.push('/Search')
};

// 默认高亮index页面
// const active = ref("index");

// 点击不同页面按钮，更改高亮位置
const onChange = (index : any) => showToast(`标签 ${index}`);

</script>

<template>
  <!--   导航栏-->
  <div class="nav-bar">
    <van-nav-bar
        title="标题"
        left-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"
    >
      <!--搜索插槽->搜索页面;插槽-可以调到你想去的页面-->
      <template #right>
        <van-icon name="search" size="18" />
      </template>
    </van-nav-bar>
  </div>
  <div id="content">
    <router-view>

    </router-view>
  </div>
<!--  <router-link to="/">Go to 主页</router-link>-->
<!--  <router-link to="/Team">Go to 队伍</router-link>-->
  <!--  tabbar栏 v-model="active"-->
  <div>
    <!--    replace-to不支持回退-->
    <van-tabbar route @change="onChange">
      <van-tabbar-item to="/" icon="home-o" name="index">主页</van-tabbar-item>
      <van-tabbar-item to="/Team" icon="bulb-o" name="team">队伍</van-tabbar-item>
      <van-tabbar-item to='/Message' icon="chat-o" name="message">消息</van-tabbar-item>
      <van-tabbar-item to="/Friends" icon="friends-o" name="friends">伙伴</van-tabbar-item>
      <van-tabbar-item to="/User" icon="user-o" name="user">个人</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<style scoped>
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
}

</style>