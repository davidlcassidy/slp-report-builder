import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue';
import NewReportPage from '../views/NewReportPage.vue';

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage
  },
  {
    path: '/new-report',
    name: 'NewReportPage',
    component: NewReportPage
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
