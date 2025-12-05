<template>
  <div class="all-announcements-page">
    <div class="page-header">
      <h2>Все объявления</h2>
      <p>Ищите потерянные вещи или помогите другим их найти</p>
    </div>

    <div class="page-content">
      <AnnouncementsList
        :announcements="allAnnouncements"
        :loading="loading"
        :show-filters="true"
        :show-stats="true"
        :show-create-button="false"
        :show-edit-button="false"
        :show-pagination="true"
        @filters-changed="handleFiltersChanged"
      />
    </div>
  </div>
</template>

<script>
import AnnouncementsList from '@/components/AllAnnouncementsList.vue'

export default {
  name: 'AllAnnouncements',
  components: {
    AnnouncementsList
  },
  data() {
    return {
      loading: false,
      allAnnouncements: [],
      filters: {}
    }
  },
  mounted() {
    this.loadAllAnnouncements()
  },
  methods: {
    async loadAllAnnouncements() {
      this.loading = true
      try {
        const response = await fetch('/announcements/all')

        if (response.ok) {
          this.allAnnouncements = await response.json()
        } else {
          // Если API нет, используем временные данные
          await this.loadMockData()
        }

        console.log('📋 Загружено всех объявлений:', this.allAnnouncements.length)
      } catch (error) {
        console.error('Ошибка загрузки:', error)
        // Используем мок данные при ошибке
        await this.loadMockData()
      } finally {
        this.loading = false
      }
    },

    async loadMockData() {
      // Временные данные для демонстрации
      this.allAnnouncements = [
        {
          id: '1',
          title: 'Ключи от квартиры',
          category: 'KEYS',
          description: 'Связка ключей с брелоком в виде медведя. Потерял у метро.',
          city: 'Москва',
          address: 'м. Проспект Мира',
          lostDate: '2024-01-15',
          color: 'Серебристый',
          reward: 1000,
          photoUrl: null,
          userId: 'user1',
          userName: 'Иван Иванов',
          createdAt: '2024-01-15T10:30:00',
          updatedAt: '2024-01-15T10:30:00',
          isActive: true
        },
        {
          id: '2',
          title: 'iPhone 13 черный',
          category: 'ELECTRONICS',
          description: 'Телефон в черном чехле. Потерял в парке Горького.',
          city: 'Москва',
          address: 'Парк Горького',
          lostDate: '2024-01-14',
          color: 'Черный',
          reward: 5000,
          photoUrl: null,
          userId: 'user2',
          userName: 'Мария Петрова',
          createdAt: '2024-01-14T15:45:00',
          updatedAt: '2024-01-14T15:45:00',
          isActive: true
        },
        {
          id: '3',
          title: 'Красный кошелек',
          category: 'WALLET',
          description: 'Кожаный кошелек красного цвета с документами внутри.',
          city: 'Санкт-Петербург',
          address: 'Невский проспект',
          lostDate: '2024-01-13',
          color: 'Красный',
          reward: 2000,
          photoUrl: null,
          userId: 'user3',
          userName: 'Алексей Смирнов',
          createdAt: '2024-01-13T12:20:00',
          updatedAt: '2024-01-13T12:20:00',
          isActive: false
        }
      ]
    },
    handleFiltersChanged(filters) {
      this.filters = filters
      console.log('Фильтры изменены:', filters)
    }
  }
}
</script>

<style scoped>
.all-announcements-page {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-header h2 {
  color: #8B5CF6;
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.page-header p {
  color: #666;
  font-size: 1.1rem;
}

.page-content {
  background: white;
  border-radius: 15px;
  padding: 2rem;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}

@media (max-width: 768px) {
  .all-announcements-page {
    padding: 1rem;
  }

  .page-header h2 {
    font-size: 2rem;
  }

  .page-content {
    padding: 1rem;
  }
}
</style>