import { createRouter, createWebHistory } from 'vue-router';
import store from '../store';
import HomePage from '../views/HomePage.vue';
import UserLoginPage from '../views/UserLoginPage.vue';
import UserRegistrationPage from '../views/UserRegistrationPage.vue';
import DashboardPage from '../views/DashboardPage.vue';
import NewReportPage from '../views/NewReportPage.vue';
import UserProfilePage from '../views/UserProfilePage.vue';

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage
  },
  {
    path: '/login',
    name: 'UserLoginPage',
    component: UserLoginPage
  },
  {
    path: '/registration',
    name: 'UserRegistrationPage',
    component: UserRegistrationPage
  },
  {
    path: '/dashboard',
    name: 'DashboardPage',
    component: DashboardPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/new-report',
    name: 'NewReportPage',
    component: NewReportPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/user-profile',
    name: 'UserProfilePage',
    component: UserProfilePage,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  if (to.matched.some(route => route.meta.requiresAuth) && !store.getters.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router;
