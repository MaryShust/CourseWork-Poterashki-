<template>
  <div class="my-announcements-page">
    <div class="page-header">
      <h2>Мои объявления</h2>
      <p v-if="totalItems > 0" class="page-subtitle">
        Всего найдено {{ totalItems }} {{ getAnnouncementsText(totalItems) }}
        <span v-if="hasResponsesFilter" class="filter-notice">(с откликами)</span>
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
      :initialFilters="initialFilters"
      :totalPages="totalPages"
      :currentPage="currentPage"
      :totalItems="totalItems"
      @page-changed="handlePageChange"
      @filters-changed="handleFiltersChange"
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
      initialFilters: {},
      currentFilters: {},
      currentPage: 0, // API использует нумерацию с 0
      pageSize: 12,
      totalPages: 0,
      totalItems: 0
    }
  },
  computed: {
    hasResponsesFilter() {
      return this.currentFilters.hasResponse === true
    }
  },
  created() {
    this.checkUrlFilters()
    this.loadAnnouncements()
  },
  watch: {
    '$route.query'(newQuery) {
      this.checkUrlFilters()
    },
    currentPage() {
      this.loadAnnouncements()
    }
  },
  methods: {
    checkUrlFilters() {
      if (this.$route.query.filter === 'has_response') {
        this.initialFilters = {
          hasResponse: true
        }
      } else {
        this.initialFilters = {}
      }
    },

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
        queryParams.append('page', this.currentPage)
        queryParams.append('size', this.pageSize)

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

        // Предполагаем, что сервер возвращает структуру с пагинацией
        // Если сервер возвращает просто массив, адаптируем под него
        if (Array.isArray(data)) {
          this.announcements = this.transformAnnouncements(data)
          this.totalItems = data.length
          this.totalPages = Math.ceil(data.length / this.pageSize)
        } else if (data.content) {
          // Spring Data Page format
          this.announcements = this.transformAnnouncements(data.content)
          this.totalItems = data.totalElements
          this.totalPages = data.totalPages
          this.currentPage = data.number
        } else {
          // Другой формат
          this.announcements = this.transformAnnouncements(data)
          this.totalItems = this.announcements.length
          this.totalPages = Math.ceil(this.announcements.length / this.pageSize)
        }

        console.log('Объявления загружены:', this.announcements)
      } catch (error) {
        console.error('Ошибка загрузки объявлений:', error)
        this.errorMessage = 'Не удалось загрузить объявления. Пожалуйста, попробуйте позже.'
        this.announcements = []
        this.totalItems = 0
        this.totalPages = 0
      } finally {
        this.loading = false
      }
    },

    transformAnnouncements(announcements) {
      if (!Array.isArray(announcements)) return []

      return announcements.map(ann => {
        // Извлекаем информацию из описания
        const description = ann.description || ''
        const title = this.extractTitleFromDescription(description)
        const color = this.extractColorFromDescription(description)
        const reward = this.extractRewardFromDescription(description)
        const category = this.extractCategoryFromDescription(description)

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
          // Добавляем совместимые поля
          title: title,
          city: ann.place?.city || 'Не указан',
          address: ann.place?.street || '',
          lostDate: ann.date,
          color: color,
          reward: reward,
          category: category,
          isActive: isActive,
          isFound: isFound,
          responseCount: 0, // Пока нет информации об откликах
          userName: 'Вы', // Для моих объявлений
          // Для фильтров
          hasResponse: false
        }
      })
    },

    extractTitleFromDescription(description) {
      if (!description) return 'Без названия'
      const firstSentence = description.split('.')[0]
      return firstSentence.length > 100 ? firstSentence.substring(0, 100) : firstSentence
    },

    extractColorFromDescription(description) {
      if (!description) return ''
      const colors = ['черный', 'белый', 'красный', 'синий', 'зеленый', 'желтый', 'коричневый', 'серый']
      for (const color of colors) {
        if (description.toLowerCase().includes(color)) {
          return color
        }
      }
      return ''
    },

    extractRewardFromDescription(description) {
      if (!description) return 0
      const rewardMatch = description.match(/(\d+)[\s]*руб/)
      return rewardMatch ? parseInt(rewardMatch[1]) : 0
    },

    extractCategoryFromDescription(description) {
      if (!description) return 'Другое'

      const categories = {
        'электроник': 'ELECTRONICS',
        'телефон': 'ELECTRONICS',
        'ноутбук': 'ELECTRONICS',
        'документ': 'DOCUMENTS',
        'паспорт': 'DOCUMENTS',
        'права': 'DOCUMENTS',
        'ключ': 'KEYS',
        'кошелек': 'WALLET',
        'деньги': 'WALLET',
        'украшен': 'JEWELRY',
        'кольцо': 'JEWELRY',
        'цепочка': 'JEWELRY',
        'одежд': 'CLOTHES',
        'куртк': 'CLOTHES',
        'животн': 'ANIMALS',
        'кот': 'ANIMALS',
        'собак': 'ANIMALS',
        'сумк': 'BAGS',
        'рюкзак': 'BAGS'
      }

      const descLower = description.toLowerCase()
      for (const [keyword, category] of Object.entries(categories)) {
        if (descLower.includes(keyword)) {
          return category
        }
      }

      return 'Другое'
    },

    handleFiltersChange(filters) {
      this.currentFilters = filters
      this.currentPage = 0 // Сбрасываем на первую страницу при изменении фильтров
      this.loadAnnouncements()
    },

    handlePageChange(page) {
      this.currentPage = page
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