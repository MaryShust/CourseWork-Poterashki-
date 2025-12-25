import Vue from 'vue'
import Router from 'vue-router'
import Home from '../views/Home.vue'
import Dashboard from '../views/Dashboard.vue'
import Profile from '../views/Profile.vue'
import CreateAnnouncement from '../views/CreateAnnouncement.vue'
import EditAnnouncement from '../views/EditAnnouncement.vue'
import MyAnnouncements from '../views/MyAnnouncements.vue'
import AllAnnouncements from '../views/AllAnnouncements.vue'
import Details from '../views/Details.vue'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard,
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile,
      meta: { requiresAuth: true }
    },
    {
      path: '/create',
      name: 'CreateAnnouncement',
      component: CreateAnnouncement,
      meta: { requiresAuth: true }
    },
    {
      path: '/edit/:id',
      name: 'EditAnnouncement',
      component: EditAnnouncement,
      meta: { requiresAuth: true }
    },
    {
      path: '/my_announcements',
      name: 'MyAnnouncements',
      component: MyAnnouncements,
      meta: { requiresAuth: true }
    },
    {
      path: '/all_announcements',
      name: 'AllAnnouncements',
      component: AllAnnouncements,
      meta: { requiresAuth: true }
    },
    {
      path: '/details',
      name: 'Details',
      component: Details,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !localStorage.getItem('currentUser')) {
    next('/')
  } else {
    next()
  }
})

export default router