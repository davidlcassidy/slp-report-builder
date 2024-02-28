import { createRouter, createWebHistory } from 'vue-router';
import store from '../store';
import HomePage from '../views/HomePage.vue';
import UserLoginPage from '../views/UserLoginPage.vue';
import UserRegistrationPage from '../views/UserRegistrationPage.vue';
import ReportsPage from '../views/ReportsPage.vue';
import NewReportPage from '../views/NewReportPage.vue';

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
    path: '/reports',
    name: 'ReportsPage',
    component: ReportsPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/new-report',
    name: 'NewReportPage',
    component: NewReportPage,
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
