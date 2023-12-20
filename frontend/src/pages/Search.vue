<script setup lang="ts">
import { ref } from 'vue';
import {useRouter} from "vue-router";

const router = useRouter();

// 标签列表
// 搜索文本
const searchText = ref('');

/**
 * 搜索过滤
 */
const originTagList = [
  {
    text: '性别',
    children: [
      { text: '男', id: '男' },
      { text: '女', id: '女' },
    ],
  },
  {
    text: '年级',
    children: [
      { text: '大一', id: '大一' },
      { text: '大二', id: '大二' },
      { text: '大三', id: '大三' },
      { text: '大四', id: '大四' },
    ],
  },
  {
    text: '研究方向',
    children: [
      { text: 'Java', id: 'Java' },
      { text: 'C', id: 'C' },
      { text: 'C++', id: 'C++' },
      { text: 'Python', id: 'Python' },
    ],
  },
];
const onSearch = () => {
  // 将所有子标签拿出来拼成一个数组
  // console.log(tagList.flatMap(parentTag => parentTag.children))
  tagList.value = originTagList.map(parentTag => {
    const temptChildren = [...parentTag.children]; // 保留原来的子标签
    const temptParentTag = {...parentTag}; // 保留原来的父标签
    temptParentTag.children = temptChildren.filter(item => item.text.includes(searchText.value)); // 过滤掉子标签
    return temptParentTag;
  });
}
// 点击取消搜索的处理
const onCancel = () => {
 searchText.value = '';
 tagList.value = originTagList;
};

// 可关闭标签
// const show = ref(true);

// const activeName = ref('1');

// 已选中的标签
const activeIds = ref([]);

const activeIndex = ref(0);
const tagList = ref([
      {
        text: '性别',
        children: [
          { text: '男', id: '男' },
          { text: '女', id: '女' },
        ],
      },
      {
        text: '年级',
        children: [
          { text: '大一', id: '大一' },
          { text: '大二', id: '大二' },
          { text: '大三', id: '大三' },
          { text: '大四', id: '大四' },
        ],
      },
  {
    text: '研究方向',
    children: [
      { text: 'Java', id: 'Java' },
      { text: 'C', id: 'C' },
      { text: 'C++', id: 'C++' },
      { text: 'Python', id: 'Python' },
    ],
  },
    ]);

//移除标签
const doClose = ( tag:any) => {
  activeIds.value = activeIds.value.filter(item => {
    return item !== tag
  })
};

/**
 * 点击搜索按钮后，跳转到搜索结果页面
 */
const doSearchResult = () => {
  router.push({
    path: '/User/List',
    query: {
      tags: activeIds.value
    },
  })
}


</script>

<template>
  <div id="search">
    <form action="/">
      <van-search
          v-model="searchText"
          show-action
          placeholder="请输入搜索关键词"
          @search="onSearch"
          @cancel="onCancel"
      />
    </form>
  </div>
  <div class="choosedTag">
    <van-divider content-position="left" >已选标签</van-divider>
  </div>
  <div v-if="activeIds.length === 0" class="taglist">
    请选择标签
  </div>

  <div class="taglist">
<!--    gutter设置间距-->
    <van-row gutter="16" style="padding: 16px">
      <van-col v-for="tag in activeIds">
        <van-tag closeable size="medium" type="primary" @close="doClose(tag)">
          {{ tag }}
        </van-tag>
      </van-col>
    </van-row>
  </div>

  <div class="tochooseTag">
    <van-divider content-position="left" >选择标签</van-divider>
  </div>
  <!--  main-active-index 左侧选中索引即父标签-->
  <!--  active-id是已经选中的标签的id-->
  <div class="tree">
    <van-tree-select
        v-model:active-id="activeIds"
        v-model:main-active-index="activeIndex"
        :items="tagList"
    />
  </div>
  <div class="searchBtn">
    <van-button type="primary" @click="doSearchResult()">搜索</van-button>
  </div>




</template>

<style scoped>

 #search {
   position: fixed;
   top: 46px;
   left: 0;
   right: 0;
 }
 .choosedTag {
   position: fixed;
   top: 100px;
   left: 0;
   right: 0;
 }
 .taglist {
   position: fixed;
   top:150px;
   left: 0;
   right: 0;
   height: 100px;
 }
 .tochooseTag {
   position: fixed;
   top: 250px;
   left: 0;
   right: 0;
   bottom: 0;
 }
 .tree {
   position: fixed;
   top:300px;
   left: 0;
   right: 0;
 }

 .searchBtn {
   position: fixed;
   top: 520px;
   left: 0;
   right: 0;
 }

</style>