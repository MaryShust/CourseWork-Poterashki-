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

        const requestBody = {
          place: {
            city: "Москва"
          },
          completed: false,
          description: "test"
        }

        // Строим query параметры
        const queryParams = new URLSearchParams()
        queryParams.append('page', 0)
        queryParams.append('size', 20)
        // Добавляем сортировку по дате создания (новые сначала)
        queryParams.append('sort', 'createdAt,desc')

        const url = `/api/things?${queryParams.toString()}`
        const userId = localStorage.getItem('currentUserId')

        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': userId
          },
          body: JSON.stringify(requestBody)
        })

        if (response.ok) {
          this.allAnnouncements = await response.json()
        }

        console.log('📋 Загружено всех объявлений:', this.allAnnouncements.length)
      } catch (error) {
        console.error('Ошибка загрузки:', error)
      } finally {
        this.loading = false
      }
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