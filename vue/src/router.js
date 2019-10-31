import Vue from 'vue'
import Router from 'vue-router'
import ViewHome from './components/ViewHome'
import ViewGuildRoot from './components/ViewGuildRoot'
import ViewGuilds from './components/ViewGuilds'
import ViewGuild from './components/ViewGuild'
import ViewNotFound from './components/ViewNotFound'

Vue.use(Router)

export default new Router({
  history: true,
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: ViewHome
    },
    {
      path: '/guild',
      component: ViewGuildRoot,
      children: [
        {
          path: 'guilds',
          component: ViewGuilds
        },
        {
          path: ':id',
          component: ViewGuild
        }
      ]
    },
    {
      path: '*',
      name: 'Error 404',
      component: ViewNotFound
    }
  ]
})
