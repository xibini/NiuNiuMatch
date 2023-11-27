import Index from "../pages/Index.vue";
import Team from "../pages/Team.vue";
import Message from "../pages/Message.vue";
import Friends from "../pages/Friends.vue";
import User from "../pages/User.vue";
import Search from "../pages/Search.vue";


const routes = [
    { path: '/', component: Index },
    { path: '/Team', component: Team },
    { path: '/Message', component: Message },
    { path: '/User', component: User },
    { path: '/Friends', component: Friends },
    { path: '/Search', component: Search },
]

// 导出路由
export default routes;