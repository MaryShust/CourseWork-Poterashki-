<template>
  <div class="announcements-list">
    <div v-if="showFilters" class="filters">
      <div class="filter-group">
        <label>Город:</label>
        <select v-model="filters.city" @change="applyFilters">
          <option value="">Все города</option>
          <option v-for="city in availableCities" :key="city" :value="city">{{ city }}</option>
        </select>
      </div>

      <div class="filter-group">
        <label>Статус:</label>
        <select v-model="filters.status" @change="applyFilters">
          <option value="all">Все</option>
          <option value="active">Активные</option>
          <option value="inactive">Неактивные</option>
        </select>
      </div>

      <div class="filter-group">
        <label>Дата потери:</label>
        <select v-model="filters.dateRange" @change="applyFilters">
          <option value="all">За всё время</option>
          <option value="today">Сегодня</option>
          <option value="week">За неделю</option>
          <option value="month">За месяц</option>
        </select>
      </div>

      <button class="btn-clear" @click="clearFilters" v-if="hasActiveFilters">
        ✕ Сбросить фильтры
      </button>
    </div>

    <div class="list-stats" v-if="showStats">
      <span>Найдено объявлений: {{ filteredAnnouncements.length }}</span>
      <span v-if="filters.status === 'active'">Активных: {{ activeCount }}</span>
      <span v-if="filters.status === 'inactive'">Неактивных: {{ inactiveCount }}</span>
    </div>

    <div class="announcements-grid">
      <div v-if="loading" class="loading">Загрузка объявлений...</div>

      <div v-else-if="filteredAnnouncements.length === 0" class="empty-state">
        <p>😔 Объявления не найдены</p>
        <p v-if="hasActiveFilters" class="hint">Попробуйте изменить параметры фильтров</p>
        <button v-if="showCreateButton" class="btn-create" @click="goToCreate">
          📝 Создать первое объявление
        </button>
      </div>

      <div v-else class="cards-container">
        <div v-for="announcement in filteredAnnouncements" :key="announcement.id" class="announcement-card">
          <div class="card-status" :class="announcement.isActive ? (announcement.isFound ? 'found' : 'active') : 'inactive'">
            {{ announcement.isActive ? (announcement.isFound ? '🔍 Нашли' : '🔍 Ищут') : '✅ Найдено' }}
          </div>

          <div class="card-image" @click="viewDetails(announcement.id)">
            <img v-if="announcement.photoUrl" :src="announcement.photoUrl" :alt="announcement.title">
            <div v-else class="no-image">
              📷 Нет фото
            </div>
          </div>

          <div class="card-content">
            <h3 class="card-title" @click="viewDetails(announcement.id)">
              {{ announcement.title }}
            </h3>

            <div class="card-meta">
              <span class="meta-item">🏙️ {{ announcement.city }}</span>
              <span v-if="announcement.address" class="meta-item">📍 {{ announcement.address }}</span>
              <span class="meta-item">📅 {{ formatDate(announcement.lostDate) }}</span>
              <span v-if="announcement.color" class="meta-item">🎨 {{ announcement.color }}</span>
            </div>

            <div class="card-description">
              {{ truncateText(announcement.description, 100) }}
            </div>

            <div class="card-footer">
              <div class="user-info">
                <span class="user-name">👤 {{ announcement.userName }}</span>
                <span class="created-at">Создано: {{ formatDateTime(announcement.createdAt) }}</span>
                <span v-if="announcement.updatedAt !== announcement.createdAt" class="updated-at">
                  Обновлено: {{ formatDateTime(announcement.updatedAt) }}
                </span>
              </div>

              <div class="card-actions">
                <button v-if="announcement.reward > 0" class="btn-reward">
                  💰 {{ formatCurrency(announcement.reward) }}
                </button>

                <button v-if="announcement.userId === currentUserId"
                        class="btn-edit" @click="editAnnouncement(announcement.id)">
                  ✏️ Редактировать
                </button>

                <button class="btn-view" @click="viewDetails(announcement.id)">
                  👁️ Подробнее
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Пагинация -->
    <div v-if="showPagination && filteredAnnouncements.length > 0" class="pagination">
      <button :disabled="currentPage === 1" @click="prevPage">← Назад</button>
      <span>Страница {{ currentPage }} из {{ totalPages }}</span>
      <button :disabled="currentPage === totalPages" @click="nextPage">Вперед →</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AnnouncementsList',
  props: {
    announcements: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    showFilters: {
      type: Boolean,
      default: true
    },
    showStats: {
      type: Boolean,
      default: true
    },
    showCreateButton: {
      type: Boolean,
      default: false
    },
    showPagination: {
      type: Boolean,
      default: true
    },
    itemsPerPage: {
      type: Number,
      default: 12
    }
  },
  data() {
    return {
      filters: {
        city: '',
        category: '',
        status: 'all',
        dateRange: 'all'
      },
      currentPage: 1,
      currentUserId: localStorage.getItem('currentUserId')
    }
  },
  computed: {
    availableCities() {
      const cities = new Set()
      this.announcements.forEach(item => {
        if (item.city) cities.add(item.city)
      })
      return Array.from(cities).sort()
    },

    availableCategories() {
      const categories = new Set()
      this.announcements.forEach(item => {
        if (item.category) categories.add(item.category)
      })
      return Array.from(categories).sort()
    },

    hasActiveFilters() {
      return Object.values(this.filters).some(value => value !== 'all' && value !== '')
    },

    filteredAnnouncements() {
      let filtered = [...this.announcements]

      if (this.filters.city) {
        filtered = filtered.filter(item => item.city === this.filters.city)
      }

      if (this.filters.category) {
        filtered = filtered.filter(item => item.category === this.filters.category)
      }

      if (this.filters.status === 'active') {
        filtered = filtered.filter(item => item.isActive === true)
      } else if (this.filters.status === 'inactive') {
        filtered = filtered.filter(item => item.isActive === false)
      }

      if (this.filters.dateRange !== 'all') {
        const now = new Date()
        let startDate

        switch (this.filters.dateRange) {
          case 'today':
            startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate())
            break
          case 'week':
            startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
            break
          case 'month':
            startDate = new Date(now.getFullYear(), now.getMonth() - 1, now.getDate())
            break
        }

        filtered = filtered.filter(item => {
          const lostDate = new Date(item.lostDate)
          return lostDate >= startDate
        })
      }

      return filtered
    },

    paginatedAnnouncements() {
      const start = (this.currentPage - 1) * this.itemsPerPage
      const end = start + this.itemsPerPage
      return this.filteredAnnouncements.slice(start, end)
    },

    totalPages() {
      return Math.ceil(this.filteredAnnouncements.length / this.itemsPerPage)
    },

    activeCount() {
      return this.announcements.filter(item => item.isActive === true).length
    },

    inactiveCount() {
      return this.announcements.filter(item => item.isActive === false).length
    }
  },
  watch: {
    filters: {
      handler() {
        this.currentPage = 1
        this.$emit('filters-changed', this.filters)
      },
      deep: true
    }
  },
  methods: {
    applyFilters() {
      this.$emit('filters-applied', this.filters)
    },

    clearFilters() {
      this.filters = {
        city: '',
        category: '',
        status: 'all',
        dateRange: 'all'
      }
      this.$emit('filters-cleared')
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
      }
    },

    viewDetails(id) {
      localStorage.setItem('isMyDetails', false)
      this.$router.push({
        path: '/details',
        query: {
          id: id
        }
      })
    },

    editAnnouncement(id) {
      this.$router.push(`/edit/${id}`)
    },

    goToCreate() {
      this.$router.push('/create')
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('ru-RU')
    },

    formatDateTime(dateTimeString) {
      if (!dateTimeString) return ''
      const date = new Date(dateTimeString)
      return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    formatCurrency(amount) {
      if (!amount) return '0 ₽'
      return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        minimumFractionDigits: 0
      }).format(amount)
    },

    truncateText(text, maxLength) {
      if (!text) return ''
      if (text.length <= maxLength) return text
      return text.substring(0, maxLength) + '...'
    }
  }
}
</script>

<style scoped>
.announcements-list {
  padding: 1rem;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 10px;
  border: 1px solid #e9ecef;
}

.filter-group {
  display: flex;
  flex-direction: column;
  min-width: 180px;
}

.filter-group label {
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #333;
  font-size: 0.9rem;
}

.filter-group select {
  padding: 0.5rem;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 0.9rem;
  background: white;
  cursor: pointer;
}

.filter-group select:focus {
  outline: none;
  border-color: #8B5CF6;
}

.btn-clear {
  align-self: flex-end;
  padding: 0.5rem 1rem;
  background: #ff4757;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.btn-clear:hover {
  background: #ff3742;
  transform: translateY(-1px);
}

.list-stats {
  display: flex;
  gap: 2rem;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  font-size: 0.9rem;
  color: #666;
}

.announcements-grid {
  min-height: 400px;
}

.loading {
  text-align: center;
  padding: 4rem;
  color: #666;
  font-size: 1.1rem;
}

.empty-state {
  text-align: center;
  padding: 4rem;
  color: #666;
}

.empty-state p {
  margin-bottom: 1rem;
}

.hint {
  font-size: 0.9rem;
  color: #999;
}

.btn-create {
  padding: 0.75rem 1.5rem;
  background: #8B5CF6;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
}

.btn-create:hover {
  background: #7c3aed;
  transform: translateY(-1px);
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
}

.announcement-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 3px 10px rgba(0,0,0,0.1);
  border: 1px solid #e9ecef;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}

.announcement-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
}

.card-status {
  padding: 0.5rem;
  text-align: center;
  font-size: 0.8rem;
  font-weight: 600;
}

.card-status.active {
  background: #d4edda;
  color: #155724;
}

.card-status.found {
  background: #cce5ff;
  color: #004085;
}

.card-status.inactive {
  background: #fff3cd;
  color: #856404;
}

.card-image {
  height: 180px;
  background: #f8f9fa;
  cursor: pointer;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.card-image:hover img {
  transform: scale(1.05);
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 1.1rem;
  background: #e9ecef;
}

.card-content {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  margin: 0 0 1rem 0;
  font-size: 1.2rem;
  color: #333;
  cursor: pointer;
  transition: color 0.3s;
}

.card-title:hover {
  color: #8B5CF6;
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-bottom: 1rem;
  font-size: 0.85rem;
  color: #666;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.card-description {
  flex: 1;
  color: #555;
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 1.5rem;
}

.card-footer {
  margin-top: auto;
}

.user-info {
  font-size: 0.8rem;
  color: #888;
  margin-bottom: 1rem;
  line-height: 1.4;
}

.user-name {
  display: block;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.created-at, .updated-at {
  display: block;
  font-size: 0.75rem;
}

.card-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn-reward {
  padding: 0.5rem 0.75rem;
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: default;
}

.btn-edit, .btn-view {
  padding: 0.5rem 0.75rem;
  border: none;
  border-radius: 6px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-edit {
  background: #8B5CF6;
  color: white;
}

.btn-edit:hover {
  background: #7c3aed;
}

.btn-view {
  background: #6c757d;
  color: white;
}

.btn-view:hover {
  background: #5a6268;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 2rem;
  margin-top: 3rem;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.pagination button {
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination button:hover:not(:disabled) {
  background: #8B5CF6;
  color: white;
  border-color: #8B5CF6;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination span {
  color: #666;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
    gap: 1rem;
  }

  .filter-group {
    min-width: 100%;
  }

  .cards-container {
    grid-template-columns: 1fr;
  }

  .list-stats {
    flex-direction: column;
    gap: 0.5rem;
  }

  .pagination {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>