<template>
  <div class="dashboard-page">
    <div class="dashboard-header">
      <h2>Личный кабинет</h2>
      <p>Управляйте вашими объявлениями и настройками</p>
    </div>

    <div class="dashboard-content">
      <div class="dashboard-grid">
        <div class="dashboard-card stats-card">
          <h3>Моя статистика</h3>
          <div v-if="loading" class="loading">Загрузка статистики...</div>
          <div v-else class="stats">
            <div class="stat">
              <span class="stat-value">{{ statistics.activeAnnouncements }}</span>
              <span class="stat-label">Активных объявлений</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ statistics.resolvedAnnouncements }}</span>
              <span class="stat-label">Найденных вещей</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ formatCurrency(statistics.totalRemuneration) }}</span>
              <span class="stat-label">Общее вознаграждение</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ formatCurrency(statistics.maxRemuneration) }}</span>
              <span class="stat-label">Максимальное вознаграждение</span>
            </div>
          </div>
          <div v-if="error" class="error-message">{{ error }}</div>
        </div>

        <div class="dashboard-card actions-card">
          <h3>Быстрые действия</h3>
          <div class="action-buttons">
            <button class="action-btn primary" @click="createAnnouncement">
              📝 Добавить объявление о потере
            </button>
            <button class="action-btn gray" @click="findItem">
              🔍 Найти вещь
            </button>
            <button class="action-btn gray" @click="setReward">
              💰 Установить вознаграждение
            </button>
            <button class="action-btn gray" @click="goToProfile">
              👤 Редактировать профиль
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  inject: ['eventBus'],
  data() {
    return {
      currentUser: null,
      currentUserId: null,
      loading: false,
      loadingResponses: false,
      error: '',
      statistics: {
        activeAnnouncements: 0,
        resolvedAnnouncements: 0,
        totalRemuneration: 0,
        maxRemuneration: 0
      }
    }
  },
  mounted() {
    this.checkAuth()
    this.loadStatistics()
    this.$root.$on('auth-changed', () => {
      this.checkAuth()
      this.loadStatistics()
    })
  },
  beforeDestroy() {
    this.$root.$off('auth-changed')
  },
  methods: {
    checkAuth() {
      const user = localStorage.getItem('currentUser')
      const userId = localStorage.getItem('currentUserId')

      console.log('🔍 Проверка авторизации в Dashboard:')
      console.log('👤 Пользователь:', user)
      console.log('🆔 ID пользователя:', userId)

      this.currentUser = user
      this.currentUserId = userId

      if (!user || !userId) {
        console.log('❌ Не авторизован, перенаправляем на главную')
        this.$router.push('/')
      }
    },

    async loadStatistics() {
      if (!this.currentUserId) {
        console.log('⚠️ ID пользователя не найден, пропускаем загрузку статистики')
        return
      }

      console.log('📊 Начинаем загрузку статистики для ID:', this.currentUserId)
      this.loading = true
      this.error = ''

      try {
        const response = await fetch(`/api/public/my-statistics`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': this.currentUserId
            }
        })

        if (!response.ok) {
          throw new Error(`Ошибка запроса: ${response.status}`)
        }

        const data = await response.json()
        console.log('📥 Получены данные статистики:', data)

        // Маппинг полей из нового формата в старые ключи (если нужно сохранить обратную совместимость)
        this.statistics = {
          activeAnnouncements: data.active || 0,
          resolvedAnnouncements: data.foundTotal || 0,
          totalRemuneration: data.totalFee || 0,
          maxRemuneration: data.maxFee || 0
        }

        console.log('✅ Статистика успешно загружена:', this.statistics)

      } catch (err) {
        console.error('💥 Ошибка загрузки статистики:', err)
        this.error = 'Не удалось загрузить статистику. Попробуйте позже.'

        // Заглушки на случай ошибки
        this.statistics = {
          activeAnnouncements: 0,
          resolvedAnnouncements: 0,
          totalRemuneration: 0,
          maxRemuneration: 0
        }
      } finally {
        this.loading = false
      }
    },

    formatCurrency(amount) {
      return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        minimumFractionDigits: 0
      }).format(amount)
    },

    createAnnouncement() {
      this.$router.push('/create')
    },

    goToProfile() {
      this.$router.push('/profile')
    },

    findItem() {
      this.$router.push('/all_announcements')
    },

    setReward() {
      this.$router.push('/my_announcements')
    }
  }
}
</script>

<style scoped>
.dashboard-page {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 3rem;
}

.dashboard-header h2 {
  color: #8B5CF6;
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.dashboard-header p {
  color: #666;
  font-size: 1.1rem;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.dashboard-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  border: 1px solid #e9ecef;
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

.dashboard-card h3 {
  color: #8B5CF6;
  margin-bottom: 1.5rem;
  font-size: 1.3rem;
  border-bottom: 2px solid #f8f9fa;
  padding-bottom: 0.5rem;
}

.stats {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  flex: 1;
}

.stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  transition: transform 0.2s;
  min-height: 60px;
}

.stat:hover {
  transform: translateY(-2px);
  background: #e9ecef;
}

.stat-value {
  font-weight: bold;
  font-size: 1.2rem;
  color: #8B5CF6;
  flex-shrink: 0;
  min-width: 80px;
  text-align: left;
}

.stat-label {
  color: #666;
  text-align: left;
  flex: 1;
  word-break: break-word;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
  font-style: italic;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
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

.ads-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.empty-state {
  text-align: center;
  padding: 2rem 0;
  color: #666;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.empty-state p {
  margin-bottom: 1.5rem;
  font-size: 1rem;
  color: #666;
}

.create-btn {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0.75rem 1.5rem;
  min-height: 44px;
}

.responses-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0.5rem 0;
}

.responses-info {
  margin-bottom: 1.5rem;
}

.responses-count {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
}

.count-number {
  font-size: 2.5rem;
  font-weight: bold;
  color: #8B5CF6;
  margin-right: 0.75rem;
  line-height: 1;
}

.count-text {
  font-size: 1.1rem;
  color: #333;
  font-weight: 600;
  line-height: 1.2;
}

.responses-hint {
  color: #666;
  font-size: 0.95rem;
  line-height: 1.4;
  margin: 0;
}

.view-btn,
.view-all-btn {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0.75rem 1.5rem;
  min-height: 44px;
  width: 100%;
  margin-bottom: 0.75rem;
  font-size: 0.95rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.view-btn {
  background: #8B5CF6;
  color: white;
  border-color: #8B5CF6;
}

.view-btn:hover {
  background: #7c3aed;
  border-color: #7c3aed;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(139, 92, 246, 0.3);
}

.view-all-btn {
  background: #f8f9fa;
  color: #666;
  border-color: #e9ecef;
}

.view-all-btn:hover {
  background: #6c757d;
  color: white;
  border-color: #6c757d;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(108, 117, 125, 0.3);
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.action-btn {
  padding: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
  text-align: left;
  min-height: 44px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.action-btn.primary {
  background: #8B5CF6;
  color: white;
  border: 2px solid #8B5CF6;
}

.action-btn.primary:hover {
  background: #7c3aed;
  border-color: #7c3aed;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(139, 92, 246, 0.3);
}

.action-btn.gray {
  background: #f8f9fa;
  color: #666;
  border: 2px solid #e9ecef;
}

.action-btn.gray:hover {
  background: #8B5CF6;
  color: white;
  border-color: #8B5CF6;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(139, 92, 246, 0.3);
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 1rem;
  }

  .dashboard-header h2 {
    font-size: 2rem;
  }

  .dashboard-header p {
    font-size: 1rem;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .dashboard-card {
    padding: 1.5rem;
    min-height: auto;
  }

  .stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
    padding: 0.75rem;
    min-height: auto;
  }

  .stat-value {
    text-align: left;
    margin-right: 0;
    min-width: auto;
    font-size: 1.1rem;
  }

  .stat-label {
    font-size: 0.9rem;
  }

  .action-buttons {
    gap: 0.75rem;
  }

  .action-btn {
    padding: 0.85rem;
    font-size: 0.95rem;
  }

  .count-number {
    font-size: 2rem;
  }

  .count-text {
    font-size: 1rem;
  }

  .create-btn,
  .view-btn,
  .view-all-btn {
    padding: 0.75rem;
    font-size: 0.95rem;
  }

  .responses-hint {
    font-size: 0.9rem;
  }
}
</style>