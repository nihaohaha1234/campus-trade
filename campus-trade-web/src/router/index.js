import {createRouter,createWebHistory} from "vue-router";
import LoginView from '../views/LoginView.vue'
import ProductListView from '../views/ProductListView.vue'
import ProductDetailView from '../views/ProductDetailView.vue'
import ProductPublishView from '../views/ProductPublishView.vue'
import ProductMyView from '../views/ProductMyView.vue'
import ProductMyDetailView from '../views/ProductMyDetailView.vue'
import ProductUpdateView from '../views/ProductUpdateView.vue'
import AdminHomeView from '../views/AdminHomeView.vue'
import AdminProductListView from "../views/AdminProductListView.vue";
import AdminProductReviewView from "../views/AdminProductReviewView.vue";
import AdminProductDetailView from '../views/AdminProductDetailView.vue'
import AdminUserListView from '../views/AdminUserListView.vue'
import AdminOrderListView from '../views/AdminOrderListView.vue'
import RegisterView from "../views/RegisterView.vue";
import OrderListView from "../views/OrderListView.vue";
import FavoriteListView from '../views/FavoriteListView.vue'
import OrderDetailView from '../views/OrderDetailView.vue'
import AdminAIReviewLogView from "../views/AdminAIReviewLogView.vue";

const router = createRouter({
    history:createWebHistory(),
    routes:[
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/register',
            component: RegisterView
        },
        {
            path: '/login',
            component: LoginView
        },
        {
            path: '/favorites',
            component: FavoriteListView
        },
        {
            path: '/orders',
            component: OrderListView
        },
        {
            path: '/orders/:id',
            component: OrderDetailView
        },
        {
            path: '/admin',
            component: AdminHomeView
        },
        {
            path: '/admin/users',
            component: AdminUserListView
        },
        {
            path:'/admin/aireviewlogs',
            component: AdminAIReviewLogView
        },
        {
            path: '/admin/orders',
            component: AdminOrderListView
        },
        {
            path: '/admin/products/review',
            component: AdminProductReviewView
        },
        {
            path: '/admin/products',
            component: AdminProductListView
        },
        {
            path: '/admin/products/:id',
            component: AdminProductDetailView
        },
        {
            path: '/products',
            component: ProductListView
        },
        {
            path: '/products/publish',
            component: ProductPublishView
        },
        {
            path: '/products/my',
            component: ProductMyView
        },
        {
            path: '/products/my/:id/update',
            component:ProductUpdateView
        },
        {
            path: '/products/my/:id',
            component: ProductMyDetailView
        },
        {
            path: '/products/:id',
            component: ProductDetailView
        }
    ]
})
router.beforeEach((to,from,next)=>{
    if (!to.path.startsWith('/admin')){
        next()
        return
    }
    const json = localStorage.getItem('user')
    if(!json){
        next('/login')
        return;
    }
    const user = JSON.parse(json)
    if(user.role != 1){
        next('/products')
        return
    }
    next()
})

export default router