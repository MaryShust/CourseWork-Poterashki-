<template>
  <header class="header">
    <div class="header-content">
      <h1 class="header-title" @click="goHome">Потеряшки</h1>
      <div class="header-actions">
        <span v-if="currentUser" class="user-greeting">
          Добро пожаловать, {{ currentUser }}!
        </span>
        <button v-if="currentUser" class="profile-btn" @click="goToProfile">
          👤 Профиль
        </button>
        <button v-if="!currentUser" class="login-btn" @click="$emit('login-clicked')">
          Войти
        </button>
        <button v-else class="logout-btn" @click="logout">
          Выйти
        </button>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  name: 'AppHeader',
  inject: ['eventBus'],
  data() {
    return {
      currentUser: null
    }
  },
  mounted() {
    console.log('HEADER CREATE')
    this.checkAuth()
    window.addEventListener('storage', this.checkAuth)
    this.$root.$on('auth-changed', this.checkAuth)
    this.eventBus.$on('user-name-changed', this.checkAuth)
  },
  beforeDestroy() {
    console.log('HEADER DESTROY')
    window.removeEventListener('storage', this.checkAuth)
    this.$root.$off('auth-changed', this.checkAuth)
    this.eventBus.$off('user-name-changed', this.checkAuth)
  },
  methods: {
    checkAuth() {
      const user = localStorage.getItem('currentUser')
      console.log('🔍 Проверка авторизации в Header:', user)
      this.currentUser = user
    },
    logout() {
      console.log('🚪 Выход из системы')
      localStorage.removeItem('currentUser')
      localStorage.removeItem('currentUserId')
      this.currentUser = null
      this.$root.$emit('auth-changed')
      this.$router.push('/')

      if (this.$route.path !== '/') {
        this.$router.push('/')
      } else {
        console.log('📍 Уже находимся на главной странице')
      }
    },
    goHome() {
      this.$router.push('/')
    },
    goToProfile() {
      this.$router.push('/profile')
    }
  }
}
</script>

<style scoped>
.header {
  background-color: #8B5CF6;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 2rem;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.3s;
}

.header-title:hover {
  opacity: 0.8;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-greeting {
  font-weight: 500;
}

.login-btn, .logout-btn {
  padding: 0.5rem 1.5rem;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
  border: none;
}

.login-btn {
  background: rgba(255,255,255,0.2);
  color: white;
  border: 2px solid white;
}

.login-btn:hover {
  background: white;
  color: #8B5CF6;
}

.logout-btn {
  background: #ff4757;
  color: white;
}

.logout-btn:hover {
  background: #ff3742;
}

.profile-btn {
  background: rgba(255,255,255,0.2);
  color: white;
  border: 2px solid white;
  padding: 0.5rem 1.5rem;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.profile-btn:hover {
  background: white;
  color: #8B5CF6;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>