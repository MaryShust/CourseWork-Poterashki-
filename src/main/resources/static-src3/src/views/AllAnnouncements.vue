<template>
  <div class="all-announcements-page">
    <div class="page-header">
      <h2>Все объявления</h2>
      <p>Ищите потерянные вещи или помогите другим их найти</p>
    </div>

    <div class="page-content">
      <AnnouncementsList
        :announcements="allAnnouncements"
        :totalPages="totalPages"
        :totalCount="totalCount"
        :loading="loading"
        :show-filters="true"
        :show-stats="true"
        :show-create-button="false"
        :show-edit-button="false"
        :show-pagination="true"
        @apply-filters="handleApplyFilters"
        @clear-filters="handleClearFilters"
        @page-changed="handlePageChange"
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
      totalPages: 0,
      totalCount: 0,
      filters: {},
      currentPage: 0,
      pageSize: 9
    }
  },
  mounted() {
    this.filters = {
      title: 'зонт',
      place: {
        city: 'Москва'
      }
    }
    this.loadAllAnnouncements(this.currentPage, this.filters)
  },
  methods: {
    async loadAllAnnouncements(currentPage, filters = {}) {
      this.loading = true
      this.currentPage = currentPage

      try {
        // Базовый requestBody
        const requestBody = {}

        // Добавляем только заполненные фильтры
        if (filters.title) {
          requestBody.title = filters.title
          requestBody.description = filters.title
        }

        if (filters.place && filters.place.city) {
          requestBody.place = {
            city: filters.place.city
          }
        }

        if (filters.completed !== undefined) {
          requestBody.completed = filters.completed
        }

        if (filters.type !== undefined) {
          requestBody.type = filters.type
        }

        if (filters.date) {
          requestBody.date = filters.date
        }

        if (filters.createdAt) {
          requestBody.createdAt = filters.createdAt
        }

        if (filters.hasFee !== undefined) {
          requestBody.hasFee = filters.hasFee
        }

        // Строим query параметры
        const queryParams = new URLSearchParams()
        queryParams.append('page', currentPage)
        queryParams.append('size', this.pageSize)
        queryParams.append('sort', 'createdAt,desc')

        const url = `/api/things?${queryParams.toString()}`
        const userId = localStorage.getItem('currentUserId')

        console.log('Отправляем запрос с фильтрами:', requestBody)

        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': userId
          },
          body: JSON.stringify(requestBody)
        })

        if (response.ok) {
          const data = await response.json()
          this.allAnnouncements = data.items
          this.totalCount = data.total
          this.totalPages = Math.ceil(this.totalCount / this.pageSize)
          console.log(`Загружена страница ${currentPage + 1}, объявлений: ${this.allAnnouncements.length}`)
        } else {
          console.error('Ошибка загрузки:', response.status)
        }
      } catch (error) {
        console.error('Ошибка загрузки:', error)
      } finally {
        this.loading = false
      }
    },

    handleApplyFilters(filters) {
      this.filters = filters
      this.currentPage = 0
      console.log('Применяем фильтры:', filters)
      this.loadAllAnnouncements(this.currentPage, filters)
    },

    handleClearFilters() {
      this.filters = {}
      this.currentPage = 0
      console.log('Сбрасываем фильтры')
    },

    handlePageChange(page) {
      console.log('Изменение страницы на:', page)
      // Преобразуем номер страницы для API (в API страницы обычно начинаются с 0)
      const apiPage = page - 1
      this.loadAllAnnouncements(apiPage, this.filters)
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