<script setup lang="ts">
// import {useRoute} from "vue-router";
import {ref} from "vue";
// import {ref} from "vue/dist/vue";

// const route = useRoute();
// console.log(route);
// const { tags } = route.query;

const mockUser = {
  id: 123456,
  username: "cookies",
  userAccount: '12315',
  avatarUrl: "https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg",
  profile: "Stanta tell me, if he really cares, cause I can't give it all away.",
  gender: '女',
  phone: '13212691435',
  email: '3296952290@qq.com',
  userRole: 0,
  planetCode: '123456',
  tags: ['java', 'python', 'emo', '打工', '大三'],
  createTime: new Date(),
};
const userList = ref([mockUser, mockUser]);
</script>

<template>
<div class="card">
  <van-card
      v-for="user in userList"
      style="margin-top: 8px;margin-left: 8px"
      :tag="`${user.username} (${user.gender})`"
      :desc="user.profile"
      :title="user.username"
      :thumb="user.avatarUrl"
  >
    <template #tags>
      <van-tag plain type="primary" v-for="tag in mockUser.tags">
        {{ tag }}
      </van-tag>
    </template>
    <template #footer>
      <van-button size="mini">联系我</van-button>
    </template>
  </van-card>
</div>
</template>

<style scoped>
.card {
  position: fixed;
  top: 46px;
  left: 0;
  right: 0;
  width: 100%;
  padding: 0;
}
</style>