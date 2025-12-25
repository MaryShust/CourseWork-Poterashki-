<template>
  <div class="announcements-list">

    <div class="list-stats" v-if="showStats">
      <span>Всего: {{ totalItems }}</span>
      <span>Активных: {{ activeCount }}</span>
    </div>

    <div class="announcements-grid">
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Загрузка объявлений...</p>
      </div>

      <div v-else-if="announcements.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>Объявления не найдены</p>
        <p class="hint" v-else>У вас еще нет созданных объявлений</p>

        <button v-if="showCreateButton" class="btn-create" @click="goToCreate">
          📝 Создать первое объявление
        </button>
      </div>

      <div v-else class="cards-container">
        <div v-for="announcement in announcements" :key="announcement.id" class="announcement-card">
          <div class="card-status" :class="getStatusClass(announcement)">
            {{ getStatusText(announcement) }}
          </div>

          <div v-if="announcement.responseCount > 0" class="response-badge">
            💬 {{ announcement.responseCount }}
          </div>

          <div class="card-image" @click="viewDetails(announcement.id)">
            <img v-if="announcement.photos && announcement.photos.length > 0"
                 :src="getFirstPhoto(announcement)"
                 :alt="announcement.title"
                 @error="handleImageError">
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
              <span v-if="announcement.place?.street" class="meta-item">
                📍 {{ announcement.place.street }}{{ announcement.place.house ? ', ' + announcement.place.house : '' }}
              </span>
              <span class="meta-item">📅 {{ formatDate(announcement.date) }}</span>
              <span v-if="announcement.color" class="meta-item">🎨 {{ announcement.color }}</span>
            </div>

            <div class="card-description">
              {{ truncateText(announcement.description, 120) }}
            </div>

            <div class="card-footer">
              <div class="user-info">
                <span class="user-name">👤 {{ announcement.userName }}</span>
                <span class="created-at">Создано: {{ formatDateTime(announcement.createdAt) }}</span>
                <span v-if="announcement.completedAt" class="completed-at">
                  Завершено: {{ formatDateTime(announcement.completedAt) }}
                </span>
              </div>

              <div class="card-actions">
                <button v-if="announcement.fee > 0" class="btn-reward">
                  💰 {{ formatCurrency(announcement.fee) }}
                </button>

                <button v-if="announcement.isActive" class="btn-edit" @click="editAnnouncement(announcement.id)">
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
    showStats: {
      type: Boolean,
      default: true
    },
    showCreateButton: {
      type: Boolean,
      default: false
    },
    totalItems: {
      type: Number,
      default: 0
    }
  },
  computed: {
    availableCities() {
      const cities = new Set()
      this.announcements.forEach(item => {
        console.log("items")
        console.log(item)
        if (item.city && item.city !== 'Не указан') {
          cities.add(item.city)
        }
      })
      return Array.from(cities).sort()
    },

    activeCount() {
      return this.announcements.filter(item => item.isActive).length
    },

    lostCount() {
      return this.announcements.filter(item => item.type === 'LOST').length
    },

    foundCount() {
      return this.announcements.filter(item => item.type === 'FOUND').length
    }
  },
  methods: {
    getStatusClass(announcement) {
      if (!announcement.isActive) return 'completed'
      return announcement.type === 'FOUND' ? 'found' : 'lost'
    },

    getStatusText(announcement) {
      if (!announcement.isActive) return '✅ Завершено'
      return announcement.type === 'FOUND' ? '🔍 Нашёл' : '😔 Потерял'
    },

    getFirstPhoto(announcement) {
      if (!announcement.photos || announcement.photos.length === 0) return ''

      const firstPhoto = announcement.photos[0]
      // Если фото - это URL или base64
      if (firstPhoto.startsWith('http') || firstPhoto.startsWith('data:')) {
        return firstPhoto
      }
      // Иначе предполагаем, что это имя файла
      return `/api/thing/${announcement.id}/photo/${firstPhoto}`
    },

    handleImageError(event) {
      event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjIwMCIgaGVpZ2h0PSIyMDAiIGZpbGw9IiNlZWVlZWUiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiBmaWxsPSIjOTk5OTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+PGZvbnQgc2l6ZT0iMTQiPlBob3RvPC9mb250PjwvdGV4dD48L3N2Zz4='
    },

    viewDetails(id) {
      localStorage.setItem('isMyDetails', true)
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
  gap: 1rem;
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
  white-space: nowrap;
}

.announcements-grid {
  min-height: 400px;
}

.loading {
  text-align: center;
  padding: 4rem;
  color: #666;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #8B5CF6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.5;
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

.card-status.lost {
  background: #ffeaa7;
  color: #856404;
}

.card-status.found {
  background: #a3e4d7;
  color: #0d6251;
}

.card-status.completed {
  background: #d5dbdb;
  color: #424949;
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

.btn-edit, .btn-view, .btn-complete {
  padding: 0.5rem 0.75rem;
  border: none;
  border-radius: 6px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s;
  flex: 1;
  min-width: 120px;
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

.btn-complete {
  background: #20c997;
  color: white;
}

.btn-complete:hover {
  background: #1ba87e;
}

/* Пагинация */
.pagination {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 3rem;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.btn-pagination {
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
  min-width: 80px;
}

.btn-pagination:hover:not(:disabled) {
  background: #8B5CF6;
  color: white;
  border-color: #8B5CF6;
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-pagination.first,
.btn-pagination.last {
  min-width: 90px;
}

.page-numbers {
  display: flex;
  gap: 0.25rem;
  margin: 0 0.5rem;
}

.page-number {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
}

.page-number:hover:not(.active) {
  background: #f8f9fa;
  border-color: #8B5CF6;
}

.page-number.active {
  background: #8B5CF6;
  color: white;
  border-color: #8B5CF6;
  font-weight: 600;
}

.ellipsis {
  display: flex;
  align-items: center;
  padding: 0 0.5rem;
  color: #666;
}

.page-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  margin-left: 1rem;
  font-size: 0.85rem;
  color: #666;
  min-width: 150px;
}

@media (max-width: 768px) {

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

  .empty-state {
    padding: 2rem;
    min-height: 250px;
  }

  .pagination {
    flex-direction: column;
    gap: 1rem;
  }

  .btn-pagination,
  .page-number {
    min-width: auto;
    flex: 1;
  }

  .page-numbers {
    order: -1;
    width: 100%;
    justify-content: center;
  }

  .page-info {
    margin-left: 0;
    margin-top: 0.5rem;
  }

  .card-actions {
    flex-direction: column;
  }

  .btn-edit, .btn-view, .btn-complete {
    min-width: 100%;
  }
}

@media (max-width: 480px) {
  .page-numbers {
    flex-wrap: wrap;
    justify-content: center;
  }

  .btn-pagination.first,
  .btn-pagination.last {
    display: none;
  }
}
</style>