<template>
  <div id="app">
    <AppHeader @login-clicked="showLogin = true" />

    <main class="main-content">
        <router-view />
    </main>

    <AppFooter />

    <LoginModal
      v-if="showLogin"
      @close="showLogin = false"
      @login-success="handleLoginSuccess"
    />
  </div>
</template>

<script>
import Vue from 'vue'
import AppHeader from './components/Header.vue'
import AppFooter from './components/Footer.vue'
import LoginModal from './components/LoginModal.vue'

export const EventBus = new Vue()

export default {
  name: 'App',
  components: {
    AppHeader,
    AppFooter,
    LoginModal
  },
  data() {
    return {
      showLogin: false
    }
  },
  provide() {
    return {
      eventBus: EventBus
    }
  },
  mounted() {
    this.$root.$on('show-login-modal', () => {
      console.log('🔔 Событие show-login-modal получено в App.vue!')
      console.log('🔄 Открываем модальное окно авторизации...')
      this.showLogin = true
    })
    this.checkAuth()
  },
  beforeDestroy() {
    this.$root.$off('show-login-modal')
  },
  methods: {
    checkAuth() {
      const user = localStorage.getItem('currentUser')
      console.log('🔍 Глобальная проверка авторизации:', user)
    },
    handleLoginSuccess(username) {
      console.log('✅ Успешный вход в App.vue! Пользователь:', username)
      this.showLogin = false
      this.$router.push('/dashboard')
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Arial', sans-serif;
  line-height: 1.6;
  color: #333;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  background-color: white;
}
</style>