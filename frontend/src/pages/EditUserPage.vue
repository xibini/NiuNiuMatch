<script setup lang="ts">
import { ref } from 'vue';
import {useRoute} from "vue-router";

// 页面从User跳转过来，接收参数
const route = useRoute();
const editUser = ref({
   editKey: route.query.editKey as string,
   editName: route.query.editName as string,
   currentValue: route.query.currentValue as string,
})

/*
 提交
 */
const onSubmit = (values:any) => {
  // todo 把editKey, editName, editValue提交到后台
  console.log('onSubmit', values);
};
</script>

<template>
  <div class="edit">
  <van-form  @submit="onSubmit">
      <van-field
          v-model="editUser.currentValue"
          :name="editUser.editKey"
          :label="editUser.editName"
          :placeholder="`请输入${editUser.editName}`"
          :rules="[{ required: true, message: `请输入${editUser.editName}` }]"
      />
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
  </div>
</template>

<style scoped>
.edit {
  position: fixed;
  top: 46px;
  left: 0;
  right: 0;
}
</style>