<template>
  <div class="my-announcements-page">
    <div class="page-header">
      <h2>Мои объявления</h2>
      <p v-if="totalItems > 0" class="page-subtitle">
        Всего найдено {{ totalItems }} {{ getAnnouncementsText(totalItems) }}
      </p>
      <p v-else-if="!loading" class="page-subtitle">
        У вас пока нет объявлений
      </p>
    </div>

    <MyAnnouncementsList
      ref="announcementsList"
      :announcements="announcements"
      :loading="loading"
      :showCreateButton="true"
      :totalItems="totalItems"
    />
  </div>
</template>

<script>
import MyAnnouncementsList from '@/components/MyAnnouncementsList.vue'

export default {
  name: 'MyAnnouncements',
  components: {
    MyAnnouncementsList
  },
  data() {
    return {
      announcements: [],
      loading: false,
      totalItems: 0
    }
  },
  created() {
    this.loadAnnouncements()
  },
  watch: {
    currentPage() {
      this.loadAnnouncements()
    }
  },
  methods: {

    async loadAnnouncements() {
      this.loading = true
      try {
        const userId = localStorage.getItem('currentUserId')
        if (!userId) {
          this.$router.push('/')
          return
        }

        // Строим query параметры
        const queryParams = new URLSearchParams()
        queryParams.append('page', 0)
        queryParams.append('size', 100)

        // Добавляем сортировку по дате создания (новые сначала)
        queryParams.append('sort', 'createdAt,desc')

        const url = `/api/things/by-owner?${queryParams.toString()}`

        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': userId
          }
        })

        if (!response.ok) {
          throw new Error(`Ошибка загрузки: ${response.status}`)
        }

        const data = await response.json()
        console.log('Загружены объявления:', data)

        this.announcements = this.transformAnnouncements(data.items)
        this.totalItems = data.total
        console.log('Объявления загружены:', this.announcements)
      } catch (error) {
        console.error('Ошибка загрузки объявлений:', error)
        this.errorMessage = 'Не удалось загрузить объявления. Пожалуйста, попробуйте позже.'
        this.announcements = []
        this.totalItems = 0
      } finally {
        this.loading = false
      }
    },

    transformAnnouncements(announcements) {
      if (!Array.isArray(announcements)) return []

      return announcements.map(ann => {
        // Извлекаем информацию из описания
        const description = ann.description || ''

        // Определяем, активно ли объявление (если completedAt null или в будущем)
        const isActive = !ann.completedAt || new Date(ann.completedAt) > new Date()

        // Определяем тип (LOST или FOUND)
        const isFound = ann.type === 'FOUND'

        return {
          id: ann.id,
          owner: ann.owner,
          type: ann.type,
          date: ann.date,
          place: ann.place || {},
          description: description,
          photos: ann.photos || [],
          createdAt: ann.createdAt,
          completedAt: ann.completedAt,
          version: ann.version || 0,
          title: ann.title,
          city: ann.place?.city || 'Не указан',
          address: ann.place?.street || '',
          lostDate: ann.date,
          fee: ann.fee,
          isActive: isActive,
          isFound: isFound,
          responseCount: 0,
          userName: 'Вы',
          // Для фильтров
          hasResponse: false
        }
      })
    },

    getAnnouncementsText(count) {
      if (count === 1) return 'объявление'
      if (count >= 2 && count <= 4) return 'объявления'
      return 'объявлений'
    }
  }
}
</script>

<style scoped>
.my-announcements-page {
  padding: 1rem;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 2rem;
  text-align: center;
}

.page-header h2 {
  color: #8B5CF6;
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.page-subtitle {
  color: #666;
  font-size: 1.1rem;
  margin: 0;
}

.filter-notice {
  color: #8B5CF6;
  font-weight: 600;
  background: #f3e8ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  margin-left: 0.5rem;
}

@media (max-width: 768px) {
  .my-announcements-page {
    padding: 0.5rem;
  }

  .page-header h2 {
    font-size: 2rem;
  }

  .page-subtitle {
    font-size: 1rem;
  }
}
</style>