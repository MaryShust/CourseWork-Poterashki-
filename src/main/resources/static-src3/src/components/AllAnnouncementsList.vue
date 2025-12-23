<template>
  <div class="announcements-list">
    <div v-if="showFilters" class="filters">
      <div class="filter-row">
        <div class="filter-group">
          <label>Название:</label>
          <input
            type="text"
            v-model="filters.title"
            placeholder="Введите название..."
            @keyup.enter="applyFilters"
          />
        </div>

        <div class="filter-group">
          <label>Город:</label>
          <input
            type="text"
            v-model="filters.city"
            placeholder="Введите город..."
            @keyup.enter="applyFilters"
          />
        </div>

        <div class="filter-group">
          <label>Статус:</label>
          <select v-model="filters.status">
            <option value="all">Все</option>
            <option value="active">Активные</option>
            <option value="completed">Завершенные</option>
          </select>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-group">
          <label>Дата потери:</label>
          <div class="date-input-wrapper">
            <input
              type="date"
              v-model="filters.lostDate"
              :max="today"
            />
            <span class="calendar-icon">📅</span>
          </div>
        </div>
      </div>

      <div class="filter-actions">
        <button class="btn-clear" @click="clearAllFilters">
          ✕ Сбросить все
        </button>
        <button class="btn-apply" @click="applyFilters">
          ✅ Применить фильтры
        </button>
      </div>
    </div>

    <div class="list-stats" v-if="showStats">
      <span>Найдено объявлений: {{ totalCount }}</span>
      <span v-if="hasActiveFilters" class="active-filters-hint">
        (применены фильтры)
      </span>
    </div>

    <div class="announcements-grid">
      <div v-if="loading" class="loading">Загрузка объявлений...</div>

      <div v-else-if="announcements.length === 0" class="empty-state">
        <p>😔 Объявления не найдены</p>
        <p v-if="hasActiveFilters" class="hint">Попробуйте изменить параметры фильтров</p>
        <button v-if="showCreateButton" class="btn-create" @click="goToCreate">
          📝 Создать первое объявление
        </button>
      </div>

      <div v-else class="cards-container">
        <div v-for="announcement in announcements" :key="announcement.id" class="announcement-card">
          <div class="card-status" :class="announcement.completedAt ? 'completed' : (announcement.type === 'FOUND' ? 'found' : 'active')">
            {{ getStatusText(announcement) }}
          </div>

          <div class="card-image" @click="viewDetails(announcement.id)">
            <img v-if="announcement.photos && announcement.photos.length > 0"
                 :src="announcement.photos[0]"
                 :alt="announcement.title">
            <div v-else class="no-image">
              📷 Нет фото
            </div>
          </div>

          <div class="card-content">
            <h3 class="card-title" @click="viewDetails(announcement.id)">
              {{ announcement.title }}
            </h3>

            <div class="card-meta">
              <span class="meta-item" v-if="announcement.place && announcement.place.city">
                🏙️ {{ announcement.place.city }}
              </span>
              <span class="meta-item" v-if="announcement.place && announcement.place.street">
                📍 {{ announcement.place.street }}
              </span>
              <span class="meta-item" v-if="announcement.date">
                📅 {{ formatDate(announcement.date) }}
              </span>
              <span class="meta-item" v-if="announcement.type">
                {{ announcement.type === 'LOST' ? '🔍 Потеряно' : '🔍 Найдено' }}
              </span>
            </div>

            <div class="card-description">
              {{ truncateText(announcement.description, 100) }}
            </div>

            <div class="card-footer">
              <div class="user-info">
                <span class="created-at" v-if="announcement.createdAt">
                  Создано: {{ formatDateTime(announcement.createdAt) }}
                </span>
                <span v-if="announcement.completedAt" class="completed-at">
                  Завершено: {{ formatDateTime(announcement.completedAt) }}
                </span>
              </div>

              <div class="card-actions">
                <button v-if="announcement.fee > 0" class="btn-reward">
                  💰 {{ formatCurrency(announcement.fee) }}
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
    <div v-if="showPagination && announcements.length > 0" class="pagination">
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
    totalPages: {
      type: Number,
      default: 0
    },
    totalCount: {
        type: Number,
        default: 0
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
      default: 10
    }
  },
  data() {
    return {
      filters: {
        title: 'зонт',
        city: 'Москва',
        status: 'all',
        lostDate: ''
      },
      currentPage: 1,
      currentUserId: localStorage.getItem('currentUserId')
    }
  },
  computed: {
    today() {
      return new Date().toISOString().split('T')[0]
    },

    hasActiveFilters() {
      return Object.entries(this.filters).some(([key, value]) => {
        if (key === 'status') return value !== 'all'
        return value !== ''
      })
    },

    paginatedAnnouncements() {
      const start = (this.currentPage - 1) * this.itemsPerPage
      const end = start + this.itemsPerPage
      return this.announcements.slice(start, end)
    }
  },
  methods: {
    getStatusText(announcement) {
      if (announcement.completedAt) {
        return '✅ Завершено'
      }
      return announcement.type === 'FOUND' ? '🔍 Нашёл' : '🔍 Ищут'
    },

    applyFilters() {
      // Формируем объект фильтров для отправки на сервер
      const filterData = {}

      // Добавляем только заполненные поля
      if (this.filters.title) filterData.title = this.filters.title
      if (this.filters.city) {
        filterData.place = { city: this.filters.city }
      }
      if (this.filters.status === 'completed') {
        filterData.completed = true
      } else if (this.filters.status === 'active') {
        filterData.completed = false
      }

      if (this.filters.lostDate) {
        filterData.date = this.filters.lostDate
      }

      console.log('Фильтры для сервера:', filterData)
      this.$emit('apply-filters', filterData)
    },

    clearAllFilters() {
      this.filters = {
        title: '',
        city: '',
        status: 'all',
        lostDate: ''
      }
      this.currentPage = 1
      this.$emit('clear-filters')
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

    goToCreate() {
      this.$router.push('/create')
    },

    formatDate(dateString) {
      if (!dateString) return ''
      try {
        const date = new Date(dateString)
        return date.toLocaleDateString('ru-RU')
      } catch (err) {
        return dateString
      }
    },

    formatDateTime(dateTimeString) {
      if (!dateTimeString) return ''
      try {
        const date = new Date(dateTimeString)
        return date.toLocaleString('ru-RU', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        })
      } catch (err) {
        return dateTimeString
      }
    },

    formatCurrency(amount) {
      if (!amount || amount === 0) return '0 ₽'
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
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 10px;
  border: 1px solid #e9ecef;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1rem;
}

.filter-group {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #333;
  font-size: 0.9rem;
}

.filter-group input[type="text"],
.filter-group input[type="date"],
.filter-group select {
  padding: 0.5rem;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 0.9rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s;
}

.filter-group input[type="text"]:focus,
.filter-group input[type="date"]:focus,
.filter-group select:focus {
  outline: none;
  border-color: #8B5CF6;
}

.filter-group input[type="text"] {
  cursor: text;
}

.filter-group input[type="text"]::placeholder {
  color: #999;
}

.date-input-wrapper {
  position: relative;
}

.date-input-wrapper input[type="date"] {
  width: 100%;
  padding-right: 2rem;
}

.date-input-wrapper .calendar-icon {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: #666;
}

/* Скрыть иконку календаря в Safari */
.date-input-wrapper input[type="date"]::-webkit-calendar-picker-indicator {
  opacity: 0;
  position: absolute;
  right: 0;
  top: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
}

.btn-clear {
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

.btn-apply {
  padding: 0.5rem 1.5rem;
  background: #8B5CF6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-apply:hover {
  background: #7c3aed;
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
  align-items: center;
}

.active-filters-hint {
  color: #8B5CF6;
  font-weight: 600;
  margin-left: auto;
  font-size: 0.8rem;
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

.card-status.completed {
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

.created-at, .completed-at {
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
  .filter-row {
    flex-direction: column;
    gap: 1rem;
  }

  .filter-group {
    min-width: 100%;
  }

  .filter-actions {
    flex-direction: column;
  }

  .filter-actions button {
    width: 100%;
  }

  .cards-container {
    grid-template-columns: 1fr;
  }

  .list-stats {
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
  }

  .active-filters-hint {
    margin-left: 0;
  }

  .pagination {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>