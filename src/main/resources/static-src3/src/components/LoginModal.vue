<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal" @click.stop>
      <div class="modal-header">
        <h3>Авторизация</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>

      <div class="tabs">
        <button
          class="tab-btn"
          :class="{ 'active': activeTab === 'login' }"
          @click="activeTab = 'login'"
        >
          Войти
        </button>
        <button
          class="tab-btn"
          :class="{ 'active': activeTab === 'register' }"
          @click="activeTab = 'register'"
        >
          Регистрация
        </button>
      </div>

      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label for="login-email">Email:</label>
          <input
            type="email"
            id="login-email"
            v-model="loginData.email"
            required
            placeholder="Введите ваш email"
            :class="{ 'error': loginErrors.email }"
          >
          <div v-if="loginErrors.email" class="field-error">{{ loginErrors.email }}</div>
        </div>
        <div class="form-group">
          <label for="login-password">Пароль:</label>
          <input
            type="password"
            id="login-password"
            v-model="loginData.password"
            required
            placeholder="Введите ваш пароль"
            :class="{ 'error': loginErrors.password }"
          >
          <div v-if="loginErrors.password" class="field-error">{{ loginErrors.password }}</div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="loginLoading">
            {{ loginLoading ? 'Вход...' : 'Войти' }}
          </button>
          <button type="button" class="btn-secondary" @click="clearLoginForm">
            Очистить
          </button>
        </div>

        <div v-if="loginError" class="error-message">
          {{ loginError }}
        </div>
      </form>

      <form v-else @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label for="register-name">Имя:</label>
          <input
            type="text"
            id="register-name"
            v-model="registerData.name"
            required
            placeholder="Введите ваше имя"
            :class="{ 'error': registerErrors.name }"
          >
          <div v-if="registerErrors.name" class="field-error">{{ registerErrors.name }}</div>
        </div>
        <div class="form-group">
          <label for="register-email">Email:</label>
          <input
            type="email"
            id="register-email"
            v-model="registerData.email"
            required
            placeholder="Введите ваш email"
            :class="{ 'error': registerErrors.email }"
          >
          <div v-if="registerErrors.email" class="field-error">{{ registerErrors.email }}</div>
        </div>
        <div class="form-group">
          <label for="register-password">Пароль:</label>
          <input
            type="password"
            id="register-password"
            v-model="registerData.password"
            required
            placeholder="Придумайте пароль"
            :class="{ 'error': registerErrors.password }"
          >
          <div v-if="registerErrors.password" class="field-error">{{ registerErrors.password }}</div>
          <div class="field-hint">Минимум 6 символов</div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="registerLoading">
            {{ registerLoading ? 'Регистрация...' : 'Зарегистрироваться' }}
          </button>
          <button type="button" class="btn-secondary" @click="clearRegisterForm">
            Очистить
          </button>
        </div>
      </form>

      <div class="form-switch">
        <span v-if="activeTab === 'login'">
          Нет аккаунта?
          <button class="switch-link" @click="activeTab = 'register'">Зарегистрируйтесь</button>
        </span>
        <span v-else>
          Уже есть аккаунт?
          <button class="switch-link" @click="activeTab = 'login'">Войдите</button>
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginModal',
  data() {
    return {
      activeTab: 'login', // 'login' или 'register'
      loginData: {
        email: '',
        password: ''
      },
      loginLoading: false,
      loginError: '',
      loginErrors: {},
      registerData: {
        name: '',
        email: '',
        password: ''
      },
      registerLoading: false,
      registerErrors: {}
    }
  },
  methods: {
    async handleLogin() {
      this.loginLoading = true
      this.loginError = ''
      this.loginErrors = {}

      if (!this.validateLoginForm()) {
        this.loginLoading = false
        return
      }

      try {
        const response = await fetch('/auth', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            userEmail: this.loginData.email,
            password: this.loginData.password
          })
        })

        if (!response.ok) {
          const error = await response.text()
          throw new Error(error || 'Ошибка входа')
        }

        const result = await response.json()

        if (result.id) {
          localStorage.setItem('currentUser', result.name || this.loginData.email.split('@')[0])
          localStorage.setItem('currentUserId', result.id)
          localStorage.setItem('userEmail', this.loginData.email)

          this.$root.$emit('auth-changed')
          this.$emit('login-success', result.name || this.loginData.email.split('@')[0])
        } else {
          throw new Error('Некорректный ответ от сервера')
        }
      } catch (err) {
        console.error('Ошибка входа:', err)
        this.loginError = 'Неверный email или пароль'
      } finally {
        this.loginLoading = false
      }
    },

    validateLoginForm() {
      let isValid = true
      this.loginErrors = {}

      if (!this.loginData.email.trim()) {
        this.loginErrors.email = 'Email обязателен'
        isValid = false
      } else if (!this.isValidEmail(this.loginData.email)) {
        this.loginErrors.email = 'Введите корректный email'
        isValid = false
      }

      if (!this.loginData.password.trim()) {
        this.loginErrors.password = 'Пароль обязателен'
        isValid = false
      } else if (this.loginData.password.length < 6) {
        this.loginErrors.password = 'Пароль должен быть не менее 6 символов'
        isValid = false
      }

      return isValid
    },

    clearLoginForm() {
      this.loginData = {
        email: '',
        password: ''
      }
      this.loginError = ''
      this.loginErrors = {}
    },

    async handleRegister() {
      this.registerLoading = true
      this.registerErrors = {}

      if (!this.validateRegisterForm()) {
        this.registerLoading = false
        return
      }

      try {
        const response = await fetch('/registration', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            userName: this.registerData.name,
            userEmail: this.registerData.email,
            password: this.registerData.password
          })
        })

        if (!response.ok) {
          const error = await response.text()
          throw new Error(error || 'Ошибка регистрации')
        }

        const result = await response.json()

        if (result.id) {
          localStorage.setItem('currentUser', result.name || this.loginData.email.split('@')[0])
          localStorage.setItem('currentUserId', result.id)
          localStorage.setItem('userEmail', this.loginData.email)

          this.$root.$emit('auth-changed')
          this.$emit('login-success', result.name || this.loginData.email.split('@')[0])
        } else {
          throw new Error('Некорректный ответ от сервера')
        }
      } catch (err) {
        console.error('Ошибка регистрации:', err)
      } finally {
        this.registerLoading = false
      }
    },

    validateRegisterForm() {
      let isValid = true
      this.registerErrors = {}

      if (!this.registerData.name.trim()) {
        this.registerErrors.name = 'Имя обязательно'
        isValid = false
      } else if (this.registerData.name.length < 2) {
        this.registerErrors.name = 'Имя должно быть не менее 2 символов'
        isValid = false
      }

      if (!this.registerData.email.trim()) {
        this.registerErrors.email = 'Email обязателен'
        isValid = false
      } else if (!this.isValidEmail(this.registerData.email)) {
        this.registerErrors.email = 'Введите корректный email'
        isValid = false
      }

      if (!this.registerData.password.trim()) {
        this.registerErrors.password = 'Пароль обязателен'
        isValid = false
      } else if (this.registerData.password.length < 6) {
        this.registerErrors.password = 'Пароль должен быть не менее 6 символов'
        isValid = false
      }

      return isValid
    },
    clearRegisterForm() {
      this.registerData = {
        name: '',
        email: '',
        password: ''
      }
      this.registerErrors = {}
    },
    isValidEmail(email) {
      const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return re.test(email)
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 15px;
  width: 90%;
  max-width: 450px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e9ecef;
  flex-shrink: 0;
}

.modal-header h3 {
  color: #8B5CF6;
  margin: 0;
  font-size: 1.4rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.close-btn:hover {
  background: #f8f9fa;
  color: #333;
}

.tabs {
  display: flex;
  border-bottom: 1px solid #e9ecef;
}

.tab-btn {
  flex: 1;
  padding: 1rem;
  background: none;
  border: none;
  font-size: 1rem;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.tab-btn.active {
  color: #8B5CF6;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 3px;
  background: #8B5CF6;
  border-radius: 3px 3px 0 0;
}

.tab-btn:hover:not(.active) {
  background: #f8f9fa;
  color: #333;
}

.auth-form {
  padding: 1.5rem;
  flex: 1;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
  font-size: 0.9rem;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-group input:focus {
  border-color: #8B5CF6;
  outline: none;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.form-group input.error {
  border-color: #ff4757;
  background-color: #fff5f5;
}

.field-error {
  color: #ff4757;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.field-hint {
  color: #666;
  font-size: 0.85rem;
  margin-top: 0.5rem;
  font-style: italic;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-primary, .btn-secondary {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-primary {
  background: #8B5CF6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #7c3aed;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.error-message {
  color: #ff4757;
  text-align: center;
  margin-top: 1rem;
  padding: 0.75rem;
  background: #ffe6e6;
  border-radius: 8px;
  border: 1px solid #ff4757;
  font-size: 0.9rem;
}

.form-switch {
  text-align: center;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e9ecef;
  color: #666;
  font-size: 0.9rem;
}

.switch-link {
  background: none;
  border: none;
  color: #8B5CF6;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 0;
  margin-left: 0.25rem;
}

.switch-link:hover {
  text-decoration: underline;
}

.auth-form::-webkit-scrollbar {
  width: 6px;
}

.auth-form::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.auth-form::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.auth-form::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

@media (max-width: 480px) {
  .modal {
    width: 95%;
    margin: 1rem;
  }

  .modal-header {
    padding: 1.25rem;
  }

  .auth-form {
    padding: 1.25rem;
  }

  .form-actions {
    flex-direction: column;
    gap: 0.75rem;
  }

  .tab-btn {
    padding: 0.85rem;
    font-size: 0.95rem;
  }
}
</style>