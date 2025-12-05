<template>
  <div class="profile-page">
    <div class="profile-header">
      <h2>Профиль пользователя</h2>
      <p>Управляйте вашими персональными данными</p>
    </div>

    <div class="profile-content">
      <div class="profile-card">
        <div class="profile-avatar">
          <div class="avatar-circle">
            <span class="avatar-text">{{ avatarInitial }}</span>
          </div>
          <div v-if="profile.dateCreating" class="registration-date">
            На сервисе с {{ formatDate(profile.dateCreating) }}
          </div>
        </div>

        <form @submit.prevent="saveProfile" class="profile-form">
          <div class="form-section">
            <h3>Основная информация</h3>

            <div class="form-group">
              <label for="name">Имя пользователя *</label>
              <input
                type="text"
                id="name"
                v-model="profile.name"
                required
                placeholder="Введите ваше имя"
                :class="{ 'error': errors.name }"
              >
              <div v-if="errors.name" class="field-error">{{ errors.name }}</div>
            </div>

            <div class="form-group">
              <label for="phone">Номер телефона</label>
              <input
                type="tel"
                id="phone"
                v-model="profile.phone"
                placeholder="+7 (999) 123-45-67"
              >
              <div class="field-hint">Формат: +7 (999) 123-45-67</div>
            </div>

            <div class="form-group">
              <label for="city">Город проживания</label>
              <input
                type="text"
                id="city"
                v-model="profile.city"
                placeholder="Введите ваш город"
              >
            </div>
          </div>

          <div class="form-section">
            <h3>Контактная информация</h3>

            <div class="form-group">
              <label for="email">Email</label>
              <input
                type="email"
                id="email"
                v-model="profile.email"
                placeholder="example@mail.ru"
              >
            </div>

            <div class="form-group">
              <label>Дата регистрации</label>
              <div class="readonly-field">
                {{ profile.dateCreating ? formatDate(profile.dateCreating) : 'Неизвестно' }}
              </div>
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-primary" :disabled="loading">
              {{ loading ? 'Сохранение...' : 'Сохранить изменения' }}
            </button>
            <button type="button" class="btn-secondary" @click="loadProfile">
              Обновить данные
            </button>
            <button type="button" class="btn-text" @click="goBack">
              ← Назад
            </button>
          </div>

          <div v-if="successMessage" class="success-message">
            ✅ {{ successMessage }}
          </div>

          <div v-if="errorMessage" class="error-message">
            ❌ {{ errorMessage }}
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  inject: ['eventBus'],
  data() {
    return {
      loading: false,
      loadingProfile: false,
      successMessage: '',
      errorMessage: '',
      errors: {},
      profile: {
        id: null,
        name: '',
        phone: '',
        city: '',
        email: '',
        dateCreating: null
      }
    }
  },
  computed: {
    avatarInitial() {
      if (this.profile.name) {
        return this.profile.name.charAt(0).toUpperCase()
      }
      return '?'
    }
  },
  mounted() {
    this.checkAuth()
    this.loadProfile()
    this.$root.$on('auth-changed', this.checkAuth)
  },
  beforeDestroy() {
    this.$root.$off('auth-changed')
  },
  methods: {
    checkAuth() {
      const user = localStorage.getItem('currentUser')
      const userId = localStorage.getItem('currentUserId')

      console.log('🔍 Проверка авторизации в профиле:')
      console.log('👤 Пользователь:', user)
      console.log('🆔 ID пользователя:', userId)

      if (!user || !userId) {
        console.log('❌ Не авторизован, перенаправляем на главную')
        this.$router.push('/')
      } else {
        this.profile.id = userId
        if (user !== this.profile.name) {
          console.log('🔄 Синхронизация имени из localStorage:', user)
          this.profile.name = user
        }
      }
    },

    async loadProfile() {
      const userId = localStorage.getItem('currentUserId')
      if (!userId) {
        console.log('⚠️ ID пользователя не найден')
        return
      }

      console.log('📥 Загрузка профиля с сервера для ID:', userId)
      this.loadingProfile = true
      this.errorMessage = ''

      try {
        const response = await fetch(`/profile_data?id=${userId}`)

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }

        const data = await response.json()
        console.log('✅ Профиль получен с сервера:', data)

        // Обновляем данные профиля
        this.profile = {
          ...this.profile,
          name: data.name || '',
          phone: data.phone || '',
          city: data.city || '',
          email: data.email || '',
          dateCreating: data.dateCreating || null
        }

        // Синхронизируем с localStorage если имя изменилось
        const currentUser = localStorage.getItem('currentUser')
        if (data.name && currentUser !== data.name) {
          console.log(`🔄 Обновление имени в localStorage: ${currentUser} → ${data.name}`)
          localStorage.setItem('currentUser', data.name)
          this.$root.$emit('auth-changed')
          this.eventBus.$emit('user-name-changed', data.name)
        }

        this.successMessage = 'Данные профиля загружены'
        setTimeout(() => {
          this.successMessage = ''
        }, 3000)

      } catch (error) {
        console.error('💥 Ошибка загрузки профиля:', error)
        this.errorMessage = 'Не удалось загрузить профиль с сервера'

        // Загружаем из localStorage как fallback
        this.loadFromLocalStorage()
      } finally {
        this.loadingProfile = false
      }
    },

    loadFromLocalStorage() {
      console.log('📥 Загрузка профиля из localStorage как fallback')
      const savedProfile = localStorage.getItem('userProfile')
      if (savedProfile) {
        try {
          const parsed = JSON.parse(savedProfile)
          this.profile = { ...this.profile, ...parsed }
        } catch (e) {
          console.error('❌ Ошибка загрузки из localStorage:', e)
        }
      }
    },

    async saveProfile() {
      console.log('💾 Сохранение профиля на сервер...')
      this.loading = true
      this.successMessage = ''
      this.errorMessage = ''
      this.errors = {}

      if (!this.profile.name.trim()) {
        this.errors.name = 'Имя пользователя обязательно'
        this.loading = false
        return
      }

      try {
        const userId = localStorage.getItem('currentUserId')
        if (!userId) {
          throw new Error('ID пользователя не найден')
        }

        const requestData = {
          id: userId,
          name: this.profile.name,
          phone: this.profile.phone,
          city: this.profile.city,
          email: this.profile.email
        }

        console.log('📤 Отправка данных на сервер:', requestData)

        const response = await fetch('/update_profile', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestData)
        })

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }

        const result = await response.json()
        console.log('✅ Ответ от сервера:', result)

        if (result === true) {
          // Сохраняем в localStorage как backup
          localStorage.setItem('userProfile', JSON.stringify(this.profile))

          const oldUsername = localStorage.getItem('currentUser')
          if (oldUsername !== this.profile.name) {
            console.log(`🔄 Изменение имени пользователя: ${oldUsername} → ${this.profile.name}`)
            localStorage.setItem('currentUser', this.profile.name)
            this.$root.$emit('auth-changed')
            this.eventBus.$emit('user-name-changed', this.profile.name)
            this.successMessage = `Имя успешно изменено на "${this.profile.name}"!`
          } else {
            this.successMessage = 'Профиль успешно сохранен!'
          }
        } else {
          throw new Error('Сервер вернул false')
        }

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)

      } catch (error) {
        console.error('💥 Ошибка сохранения профиля:', error)
        this.errorMessage = 'Ошибка сохранения профиля. Попробуйте снова.'

        // Сохраняем локально как fallback
        localStorage.setItem('userProfile', JSON.stringify(this.profile))
      } finally {
        this.loading = false
      }
    },

    goBack() {
      this.$router.go(-1)
    },

    formatDate(dateString) {
      try {
        const date = new Date(dateString)
        return date.toLocaleDateString('ru-RU', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric'
        })
      } catch (e) {
        return 'Неизвестно'
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
  min-height: 100vh;
}

.profile-header {
  text-align: center;
  margin-bottom: 2rem;
}

.profile-header h2 {
  color: #8B5CF6;
  font-size: 2.2rem;
  margin-bottom: 0.5rem;
}

.profile-header p {
  color: #666;
  font-size: 1.1rem;
}

.profile-content {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
  overflow: hidden;
}

.profile-avatar {
  background: linear-gradient(135deg, #8B5CF6 0%, #7c3aed 100%);
  padding: 2rem;
  text-align: center;
}

.avatar-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: white;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.2);
}

.avatar-text {
  font-size: 3rem;
  font-weight: bold;
  color: #8B5CF6;
}

.registration-date {
  color: white;
  margin-top: 1rem;
  font-size: 0.9rem;
  opacity: 0.9;
}

.profile-form {
  padding: 2rem;
}

.form-section {
  margin-bottom: 2.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 1.5rem;
}

.form-section h3 {
  color: #333;
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #f0f0f0;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
  font-size: 0.95rem;
}

.form-group input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
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

.readonly-field {
  padding: 0.75rem 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  color: #666;
  font-size: 1rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  flex-wrap: wrap;
}

.btn-primary, .btn-secondary, .btn-text {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s;
  border: none;
  min-width: 150px;
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

.btn-text {
  background: transparent;
  color: #666;
  text-decoration: underline;
  min-width: auto;
}

.btn-text:hover {
  color: #8B5CF6;
}

.success-message {
  background: #d4edda;
  color: #155724;
  padding: 1rem;
  border-radius: 8px;
  margin-top: 1.5rem;
  border: 1px solid #c3e6cb;
  animation: fadeIn 0.3s ease;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border-radius: 8px;
  margin-top: 1.5rem;
  border: 1px solid #f5c6cb;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 768px) {
  .profile-page {
    padding: 1rem;
  }

  .profile-form {
    padding: 1.5rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary, .btn-secondary, .btn-text {
    width: 100%;
    min-width: auto;
  }

  .avatar-circle {
    width: 80px;
    height: 80px;
  }

  .avatar-text {
    font-size: 2.5rem;
  }
}
</style>