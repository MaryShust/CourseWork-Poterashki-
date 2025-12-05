<template>
  <div class="my-announcements-page">
    <div class="page-header">
      <h2>Мои объявления</h2>
      <p v-if="filteredAnnouncementsCount > 0" class="page-subtitle">
        У вас {{ filteredAnnouncementsCount }} {{ getAnnouncementsText(filteredAnnouncementsCount) }}
        <span v-if="hasResponsesFilter" class="filter-notice">(с откликами)</span>
      </p>
    </div>

    <MyAnnouncementsList
      ref="announcementsList"
      :announcements="announcements"
      :loading="loading"
      :showCreateButton="true"
      :initialFilters="initialFilters"
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
      currentFilters: {}
    }
  },
  computed: {
    filteredAnnouncementsCount() {
      return this.announcements.filter(ann => {
        if (this.currentFilters.hasResponse && ann.responseCount === 0) {
          return false
        }
        if (this.currentFilters.status === 'active' && !ann.isActive) {
          return false
        }
        if (this.currentFilters.status === 'inactive' && ann.isActive) {
          return false
        }
        if (this.currentFilters.city && ann.city !== this.currentFilters.city) {
          return false
        }
        if (this.currentFilters.category && ann.category !== this.currentFilters.category) {
          return false
        }
        return true
      }).length
    },

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

        const response = await fetch(`/announcements/user/${userId}`)
        if (!response.ok) {
          throw new Error(`Ошибка загрузки: ${response.status}`)
        }

        const data = await response.json()
        console.log('Загружены объявления:', data)

        // Добавляем поле responseCount к каждому объявлению
        this.announcements = data.map(ann => ({
          ...ann,
          responseCount: ann.responses ? ann.responses.length : 0,
          // Обеспечиваем наличие обязательных полей
          city: ann.city || 'Не указан',
          category: ann.category || 'Другое',
          userName: ann.userName || 'Аноним',
          isActive: ann.isActive !== undefined ? ann.isActive : true
        }))

        console.log('Объявления с responseCount:', this.announcements)
      } catch (error) {
        console.error('Ошибка загрузки объявлений:', error)
        alert('Не удалось загрузить объявления. Пожалуйста, попробуйте позже.')
      } finally {
        this.loading = false
      }
    },

    handleFiltersChange(filters) {
      this.currentFilters = filters
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