<template>
  <div class="announcements-list">
    <div v-if="showFilters" class="filters">

      <div class="filter-group toggle-filter">
        <label class="toggle-label">
          <input type="checkbox" v-model="filters.hasResponse" @change="applyFilters" class="toggle-input">
          <span class="toggle-slider"></span>
          <span class="toggle-text">Есть ответ</span>
        </label>
      </div>

      <div class="filter-group">
        <label>Город:</label>
        <select v-model="filters.city" @change="applyFilters">
          <option value="">Все города</option>
          <option v-for="city in availableCities" :key="city" :value="city">{{ city }}</option>
        </select>
      </div>

      <div class="filter-group">
        <label>Категория:</label>
        <select v-model="filters.category" @change="applyFilters">
          <option value="">Все категории</option>
          <option v-for="cat in availableCategories" :key="cat" :value="cat">{{ cat }}</option>
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
      <span v-if="filters.hasResponse">С ответами: {{ hasResponseCount }}</span>
    </div>

    <div class="announcements-grid">
      <div v-if="loading" class="loading">Загрузка объявлений...</div>

      <div v-else-if="filteredAnnouncements.length === 0" class="empty-state">
        <p v-if="filters.hasResponse">😔 Нет объявлений с ответами</p>
        <p v-else>😔 Объявления не найдены</p>

        <p v-if="hasActiveFilters && !filters.hasResponse" class="hint">Попробуйте изменить параметры фильтров</p>
        <p v-else-if="filters.hasResponse" class="hint">Пока что на ваши объявления никто не откликнулся</p>
        <p v-else class="hint">У вас еще нет созданных объявлений</p>

        <button v-if="showCreateButton && !filters.hasResponse" class="btn-create" @click="goToCreate">
          📝 Создать первое объявление
        </button>
      </div>

      <div v-else class="cards-container">
        <div v-for="announcement in paginatedAnnouncements" :key="announcement.id" class="announcement-card">
          <div class="card-status" :class="announcement.isActive ? (announcement.isFound ? 'found' : 'active') : 'inactive'">
            {{ announcement.isActive ? (announcement.isFound ? '🔍 Нашли' : '🔍 Ищут') : '✅ Найдено' }}
          </div>

          <div v-if="announcement.responseCount > 0" class="response-badge">
            💬 {{ announcement.responseCount }}
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

                <button class="btn-edit" @click="editAnnouncement(announcement.id)">
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
    },
    initialFilters: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      filters: {
        city: '',
        category: '',
        status: 'all',
        dateRange: 'all',
        hasResponse: false
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
      return Object.values(this.filters).some(value =>
        (value !== 'all' && value !== '' && value !== false)
      )
    },

    hasResponseFilterActive() {
      return this.filters.hasResponse === true
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

      if (this.filters.hasResponse) {
        filtered = filtered.filter(item => item.responseCount > 0)
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
    },

    hasResponseCount() {
      return this.announcements.filter(item => item.responseCount > 0).length
    }
  },
  watch: {
    initialFilters: {
      immediate: true,
      handler(newFilters) {
        if (newFilters && Object.keys(newFilters).length > 0) {
          this.filters = { ...this.filters, ...newFilters }
          this.applyFilters()
        }
      }
    },

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
        dateRange: 'all',
        hasResponse: false
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

.toggle-filter {
  min-width: 180px;
}

.toggle-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
  margin-top: 1.6rem;
}

.toggle-input {
  display: none;
}

.toggle-slider {
  position: relative;
  width: 50px;
  height: 26px;
  background-color: #ccc;
  border-radius: 34px;
  transition: .4s;
  margin-right: 10px;
  flex-shrink: 0;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  border-radius: 50%;
  transition: .4s;
}

.toggle-input:checked + .toggle-slider {
  background-color: #8B5CF6;
}

.toggle-input:checked + .toggle-slider:before {
  transform: translateX(24px);
}

.toggle-text {
  font-size: 0.9rem;
  color: #333;
  font-weight: 500;
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
  margin-top: auto;
  height: fit-content;
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
  flex-wrap: wrap;
}

.list-stats span {
  padding: 0.25rem 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.empty-state p {
  margin-bottom: 1rem;
  max-width: 500px;
}

.hint {
  font-size: 0.9rem;
  color: #999;
  margin-bottom: 1.5rem;
  max-width: 400px;
  line-height: 1.4;
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
  margin-top: 0.5rem;
}

.btn-create:hover {
  background: #7c3aed;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(139, 92, 246, 0.3);
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
  position: relative;
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

.response-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #8B5CF6;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  z-index: 1;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
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

  .toggle-label {
    margin-top: 0;
  }

  .btn-clear {
    align-self: stretch;
    margin-top: 0;
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

  .empty-state {
    padding: 2rem;
    min-height: 250px;
  }
}
</style>