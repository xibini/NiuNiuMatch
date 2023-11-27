import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import * as VueRouter from 'vue-router';
import "vant/lib/index.css";
import { NavBar, Icon, Tabbar, TabbarItem } from 'vant';
import routes from "./config/route.ts";
import { Search, Tag, Divider, Collapse, CollapseItem, TreeSelect, Col, Row } from 'vant';



const app = createApp(App);
app.use(NavBar);
app.use(Icon);
app.use(Tabbar);
app.use(TabbarItem);
app.use(Search);
app.use(Tag);
app.use(Divider);
app.use(Collapse);
app.use(CollapseItem);
app.use(TreeSelect);
app.use(Col);
app.use(Row);

// 定义路由组件.
// 也可以从其他文件导入
// 创建路由实例并传递 `routes` 配置
// 你可以在这里输入更多的配置，但我们在这里
// 暂时保持简单
const router = VueRouter.createRouter({
    // 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
    history: VueRouter.createWebHashHistory(),
    routes, // `routes: routes` 的缩写
})

// 创建并挂载根实例
app.use(router);

app.mount('#app');








