<template>
  <div class="details-page">
    <div class="details-header">
      <button class="btn-back" @click="goBack">
        ← Назад
      </button>
      <h2>Детали объявления</h2>
    </div>

    <div class="details-content">
      <div v-if="loading" class="loading-container">
        <div class="loading">Загрузка информации...</div>
      </div>

      <div v-else-if="error" class="error-container">
        <div class="error-message">
          ❌ {{ error }}
        </div>
        <button class="btn-back-to-list" @click="goToAnnouncements">
          Вернуться к списку объявлений
        </button>
      </div>

      <div v-else-if="announcement" class="announcement-details">
        <div class="details-header-info">
          <div class="title-section">
            <h1 class="announcement-title">{{ announcement.title }}</h1>
            <div class="status-badge" :class="getStatusClass()">
              {{ getStatusText() }}
            </div>
          </div>

          <div class="meta-info">
            <span class="created-date">
              Создано: {{ formatDateTime(announcement.createdAt) }}
            </span>
            <span v-if="announcement.completedAt" class="updated-date">
              Завершено: {{ formatDateTime(announcement.completedAt) }}
            </span>
          </div>
        </div>

        <div class="details-grid">
          <div class="left-column">
            <div class="photo-section">
              <div v-if="hasPhotos" class="photo-container">
                <img
                  :src="announcement.photos[0]"
                  :alt="announcement.title"
                  class="main-photo"
                  @click="openPhotoModal"
                />
              </div>
              <div v-else class="no-photo">
                <div class="no-photo-icon">📷</div>
                <p>Фотографии отсутствуют</p>
              </div>
            </div>

            <div class="description-section">
              <h3>Описание</h3>
              <div class="description-content">
                <p v-if="announcement.description">{{ announcement.description }}</p>
                <p v-else class="no-description">Описание отсутствует</p>
              </div>
            </div>
          </div>

          <div class="right-column">
            <div class="details-section">
              <h3>Детали {{ announcement.type === 'LOST' ? 'потери' : 'находки' }}</h3>
              <div class="details-list">
                <div v-if="announcement.place" class="detail-item">
                  <span class="detail-label">🗺️ Город:</span>
                  <span class="detail-value">{{ announcement.place.city || 'Не указан' }}</span>
                </div>
                <div v-if="getFullAddress()" class="detail-item">
                  <span class="detail-label">📍 Адрес:</span>
                  <span class="detail-value">{{ getFullAddress() }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">📅 Дата:</span>
                  <span class="detail-value">{{ formatDate(announcement.date) }}</span>
                </div>
                <div v-if="announcement.type" class="detail-item">
                  <span class="detail-label">📋 Тип:</span>
                  <span class="detail-value">{{ getTypeText(announcement.type) }}</span>
                </div>
              </div>
            </div>

            <div v-if="announcement.fee > 0" class="reward-section">
              <h3>Вознаграждение</h3>
              <div class="reward-amount">
                <span class="reward-icon">💰</span>
                <span class="reward-value">{{ formatCurrency(announcement.fee) }}</span>
              </div>
            </div>

            <div class="author-section">
              <h3>Автор объявления</h3>
              <div class="author-info">
                <div class="author-name">👤 {{ this.ownerName || 'Аноним' }}</div>
                <div v-if="isCurrentUserAuthor" class="author-note">
                  <span class="your-announcement">(Это ваше объявление)</span>
                </div>
              </div>
            </div>

            <div class="action-buttons">
              <button v-if="isCurrentUserAuthor" class="btn-edit" @click="editAnnouncement">
                ✏️ Редактировать объявление
              </button>

              <div v-if="!isCurrentUserAuthor && hasCurrentUserResponded && !isCompleted" class="already-responded">
                <div class="response-status">
                  <span class="response-icon">✅</span>
                  <span class="response-text">Вы уже откликнулись на это объявление</span>
                </div>
                <div class="response-info">
                  <p>Автор свяжется с вами, как только сможет</p>
                </div>
              </div>

              <button v-if="!isCurrentUserAuthor && !isCompleted && !hasCurrentUserResponded"
                      class="btn-contact"
                      @click="respondToAnnouncement"
                      :disabled="isResponding">
                📞 Откликнуться
              </button>

              <button v-if="isCurrentUserAuthor && !isCompleted" class="btn-close" @click="completeAnnouncement">
                ✅ Завершить объявление
              </button>

              <button class="btn-share" @click="shareAnnouncement">
                🔗 Поделиться
              </button>
            </div>
          </div>
        </div>

        <div v-if="announcement.responses && announcement.responses.length > 0"
             class="responses-section">
          <div class="responses-header">
            <h3>{{ isCurrentUserAuthor ? 'Отклики на объявление' : 'Информация об откликах' }}</h3>
            <div class="responses-count">
              💬 {{ announcement.responses.length }}
              {{ getResponsesText(announcement.responses.length) }}
            </div>
          </div>

          <div v-if="isCurrentUserAuthor" class="responders-list">
            <div v-for="response in announcement.responses" :key="response.id" class="responder-card">
              <div class="responder-avatar">
                {{ getUserInitials(response.username) }}
              </div>
              <div class="responder-info">
                <div class="responder-name">
                  {{ response.username || 'Пользователь' }}
                  <span v-if="response.userCity" class="responder-city">📍 {{ response.userCity }}</span>
                </div>

                <div class="responder-contacts">
                  <div v-if="response.phone" class="contact-item">
                    <span class="contact-icon">📱</span>
                    <a :href="'tel:' + response.phone" class="contact-link">{{ response.phone }}</a>
                  </div>
                  <div v-if="response.email" class="contact-item">
                    <span class="contact-icon">📧</span>
                    <a :href="'mailto:' + response.email" class="contact-link">{{ response.email }}</a>
                  </div>
                </div>

                <div v-if="response.respondedAt" class="responder-date">
                  Откликнулся: {{ formatDateTime(response.respondedAt) }}
                </div>
              </div>

              <div class="responder-actions">
                <button class="btn-call" @click="callUser(response.phone)" v-if="response.phone">
                  Позвонить
                </button>
                <button class="btn-email" @click="emailUser(response.email)" v-if="response.email">
                  Написать
                </button>
              </div>
            </div>
          </div>

          <div v-else class="responses-note">
            <p>На это объявление уже откликнулось {{ announcement.responses.length }} {{ getResponsesText(announcement.responses.length) }}</p>
            <p v-if="announcement.type === 'LOST'">Если вы нашли эту вещь, нажмите "Откликнуться" чтобы связаться с автором</p>
            <p v-else>Если вы потеряли эту вещь, нажмите "Откликнуться" чтобы связаться с автором</p>
          </div>
        </div>

        <div v-if="isCompleted" class="closure-notice">
          <div class="notice-icon">ℹ️</div>
          <div class="notice-content">
            <h4>Объявление завершено</h4>
            <p>Это объявление больше не актуально. Вещь {{ announcement.type === 'LOST' ? 'найдена' : 'возвращена владельцу' }}.</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showPhotoModal && hasPhotos" class="photo-modal" @click="closePhotoModal">
      <div class="modal-content" @click.stop>
        <button class="modal-close" @click="closePhotoModal">×</button>
        <img :src="announcement.photos[0]" :alt="announcement.title" class="modal-photo">
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Details',
  data() {
    return {
      ownerName: null,
      announcement: null,
      loading: false,
      error: '',
      showPhotoModal: false,
      currentUserId: localStorage.getItem('currentUserId') || null,
      isResponding: false,
      respondedInCurrentSession: false
    }
  },
  computed: {
    isCurrentUserAuthor() {
      console.log("isCurrentUserAuthor")
      console.log(localStorage.getItem('isMyDetails'))
      return localStorage.getItem('isMyDetails') === 'true'
    },

    isCompleted() {
      return this.announcement && this.announcement.completedAt !== null
    },

    hasCurrentUserResponded() {
      if (!this.currentUserId || !this.announcement || !this.announcement.responses) {
        return false
      }
      // Предполагаем, что responses содержат ID пользователей
      return this.announcement.alreadyResponded || this.respondedInCurrentSession
    },

    hasPhotos() {
      return this.announcement &&
             this.announcement.photos &&
             this.announcement.photos.length > 0 &&
             this.announcement.photos[0]
    }
  },
  created() {
    window.scrollTo(0, 0)
    this.loadAnnouncement()
  },
  methods: {
    async loadAnnouncement() {
      this.loading = true
      this.error = ''

      try {
        const announcementId = this.$route.query.id

        if (!announcementId) {
          throw new Error('ID объявления не указан')
        }

        const response = await fetch(`/api/thing/${announcementId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': this.currentUserId
          }
        })

        if (!response.ok) {
          if (response.status === 404) {
            throw new Error('Объявление не найдено')
          }
          throw new Error('Ошибка загрузки объявления')
        }

        this.announcement = await response.json()
        console.log('Загружено объявление:', this.announcement)

        await this.loadOwner()
      } catch (err) {
        console.error('Ошибка загрузки объявления:', err)
        this.error = err.message || 'Не удалось загрузить информацию об объявлении'
      } finally {
        this.loading = false
      }
    },

    async loadOwner() {
      this.loading = true
      this.error = ''

      try {
        if (!this.announcement.owner) {
          throw new Error('ID пользователя не указан')
        }

        const response = await fetch(`/api/public/user/${this.announcement.owner}/profile`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': this.currentUserId
          }
        })

        if (response.ok) {
          this.ownerName = (await response.json()).name
        }
      } catch (err) {
        console.error('Ошибка загрузки объявления:', err)
      } finally {
        this.loading = false
      }
    },

    formatDate(dateString) {
      if (!dateString) return ''
      try {
        const date = new Date(dateString)
        return date.toLocaleDateString('ru-RU', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric'
        })
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
      if (!amount) return '0 ₽'
      return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        minimumFractionDigits: 0
      }).format(amount)
    },

    getFullAddress() {
      if (!this.announcement.place) return ''

      const place = this.announcement.place
      const parts = []

      if (place.street) parts.push(`ул. ${place.street}`)
      if (place.house) parts.push(`д. ${place.house}`)
      if (place.placeName) parts.push(place.placeName)
      if (place.extraDescription) parts.push(place.extraDescription)

      return parts.join(', ')
    },

    getTypeText(type) {
      const types = {
        'LOST': 'Потерял',
        'FOUND': 'Нашел'
      }
      return types[type] || type
    },

    getStatusClass() {
      if (this.isCompleted) return 'completed'
      return this.announcement.type === 'LOST' ? 'lost' : 'found'
    },

    getStatusText() {
      if (this.isCompleted) {
        return this.announcement.type === 'LOST' ? '✅ Найдено' : '✅ Возвращено'
      }
      return this.announcement.type === 'LOST' ? '🔍 Ищут' : '🔍 Нашёл'
    },

    getResponsesText(count) {
      if (count === 1) return 'человек'
      if (count >= 2 && count <= 4) return 'человека'
      return 'человек'
    },

    getUserInitials(name) {
      if (!name) return '?'
      const parts = name.split(' ')
      if (parts.length >= 2) {
        return (parts[0][0] + parts[1][0]).toUpperCase()
      }
      return name[0].toUpperCase()
    },

    goBack() {
      this.$router.go(-1)
    },

    goToAnnouncements() {
      this.$router.push('/my_announcements')
    },

    editAnnouncement() {
      if (this.announcement && this.announcement.id) {
        this.$router.push(`/edit/${this.announcement.id}`)
      }
    },

    async respondToAnnouncement() {
      if (!this.announcement || !this.announcement.id || this.isCompleted) {
        return
      }

      this.isResponding = true

      try {
        const response = await fetch(`/api/thing/${this.announcement.id}/reponse`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': this.currentUserId
          }
        })

        if (response.ok) {
          alert('Вы успешно откликнулись на объявление!')
          this.respondedInCurrentSession = true
        } else {
          throw new Error('Не удалось отправить отклик')
          this.respondedInCurrentSession = false
        }
      } catch (error) {
        console.error('Ошибка при отклике:', error)
        alert('Не удалось отправить отклик. Попробуйте позже.')
      } finally {
        this.isResponding = false
      }
    },

    async completeAnnouncement() {
      if (!this.announcement || !this.announcement.id || this.isCompleted) {
        return
      }

      if (!confirm('Вы уверены, что хотите завершить это объявление?')) {
        return
      }

      try {
        const response = await fetch(`/api/thing/${this.announcement.id}/complete`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': this.currentUserId
          }
        })

        if (response.ok) {
          this.announcement.completedAt = new Date().toISOString()
          alert('Объявление успешно завершено!')
        } else {
          throw new Error('Не удалось завершить объявление')
        }
      } catch (error) {
        console.error('Ошибка при завершении объявления:', error)
        alert('Не удалось завершить объявление. Попробуйте позже.')
      }
    },

    shareAnnouncement() {
      if (navigator.share) {
        navigator.share({
          title: `${this.announcement.type === 'LOST' ? 'Потеряно' : 'Найдено'}: ${this.announcement.title}`,
          text: `Помогите ${this.announcement.type === 'LOST' ? 'найти' : 'вернуть'} ${this.announcement.description?.substring(0, 100)}...`,
          url: window.location.href
        })
      } else {
        navigator.clipboard.writeText(window.location.href)
        alert('Ссылка скопирована в буфер обмена!')
      }
    },

    openPhotoModal() {
      if (this.hasPhotos) {
        this.showPhotoModal = true
      }
    },

    closePhotoModal() {
      this.showPhotoModal = false
    },

    callUser(phone) {
      if (phone) {
        window.open(`tel:${phone}`, '_self')
      }
    },

    emailUser(email) {
      if (email) {
        window.open(`mailto:${email}`, '_self')
      }
    }
  }
}
</script>

<style scoped>
.details-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 1rem;
}

.details-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.btn-back {
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  color: #333;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.details-header h2 {
  color: #333;
  font-size: 1.5rem;
  margin: 0;
}

.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  background: white;
  border-radius: 10px;
  padding: 2rem;
  text-align: center;
}

.loading {
  color: #666;
  font-size: 1.1rem;
  font-style: italic;
}

.error-message {
  color: #ff4757;
  font-size: 1.1rem;
  margin-bottom: 2rem;
  max-width: 500px;
}

.btn-back-to-list {
  padding: 0.75rem 1.5rem;
  background: #8B5CF6;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
}

.btn-back-to-list:hover {
  background: #7c3aed;
  transform: translateY(-1px);
}

.announcement-details {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  overflow: hidden;
}

.details-header-info {
  padding: 1.5rem;
  border-bottom: 1px solid #e9ecef;
  background: #f8f9fa;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.announcement-title {
  font-size: 1.8rem;
  color: #333;
  margin: 0;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.status-badge.lost {
  background: #d4edda;
  color: #155724;
}

.status-badge.found {
  background: #cce5ff;
  color: #004085;
}

.status-badge.completed {
  background: #fff3cd;
  color: #856404;
}

.meta-info {
  display: flex;
  gap: 1.5rem;
  font-size: 0.85rem;
  color: #666;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  padding: 2rem;
}

@media (max-width: 992px) {
  .details-grid {
    grid-template-columns: 1fr;
  }
}

.photo-section {
  margin-bottom: 2rem;
}

.photo-container {
  width: 100%;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #e9ecef;
  cursor: pointer;
  transition: transform 0.3s;
}

.photo-container:hover {
  transform: scale(1.02);
}

.main-photo {
  width: 100%;
  height: auto;
  display: block;
  max-height: 400px;
  object-fit: contain;
}

.no-photo {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  background: #f8f9fa;
  border: 2px dashed #e9ecef;
  border-radius: 10px;
  color: #666;
}

.no-photo-icon {
  font-size: 3rem;
  margin-bottom: 0.5rem;
}

.description-section h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

.description-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  line-height: 1.6;
  color: #555;
}

.no-description {
  color: #999;
  font-style: italic;
}

.details-section, .reward-section, .author-section {
  margin-bottom: 1.5rem;
}

.details-section h3, .reward-section h3, .author-section h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

.details-list {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e9ecef;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 600;
  color: #666;
  min-width: 120px;
}

.detail-value {
  color: #333;
  text-align: right;
  flex: 1;
}

.reward-amount {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: linear-gradient(135deg, #fff3cd, #ffeaa7);
  border: 1px solid #ffeaa7;
  border-radius: 8px;
}

.reward-icon {
  font-size: 1.5rem;
}

.reward-value {
  font-size: 1.3rem;
  font-weight: bold;
  color: #856404;
}

.author-info {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.author-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.author-note {
  color: #666;
  font-size: 0.9rem;
}

.your-announcement {
  color: #8B5CF6;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.btn-edit, .btn-contact, .btn-share, .btn-close {
  padding: 0.75rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 600;
  transition: all 0.3s;
  text-align: center;
}

.btn-edit {
  background: #8B5CF6;
  color: white;
}

.btn-edit:hover {
  background: #7c3aed;
  transform: translateY(-1px);
}

.btn-contact {
  background: #28a745;
  color: white;
}

.btn-contact:hover {
  background: #218838;
  transform: translateY(-1px);
}

.btn-close {
  background: #c82333;
  color: white;
}

.btn-close:hover {
  background: #bd2130;
  transform: translateY(-1px);
}

.btn-share {
  background: #6c757d;
  color: white;
}

.btn-share:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.already-responded {
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 0.75rem;
}

.response-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.response-icon {
  font-size: 1.2rem;
}

.response-text {
  font-weight: 600;
  color: #155724;
}

.response-info {
  font-size: 0.9rem;
  color: #155724;
  opacity: 0.9;
}

.responses-section {
  margin-top: 2rem;
  padding: 1.5rem 2rem;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
  border-radius: 10px;
}

.responses-header {
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.responses-header h3 {
  color: #8B5CF6;
  margin: 0;
  font-size: 1.3rem;
}

.responses-count {
  color: #666;
  font-size: 0.95rem;
  font-weight: 600;
  background: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  border: 1px solid #e9ecef;
}

.responders-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.responder-card {
  display: flex;
  align-items: flex-start;
  gap: 1.5rem;
  padding: 1.5rem;
  background: white;
  border-radius: 10px;
  border: 1px solid #e9ecef;
  transition: transform 0.2s;
  margin: 0 0.5rem;
}

.responder-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.responder-avatar {
  width: 50px;
  height: 50px;
  background: #8B5CF6;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.responder-info {
  flex: 1;
  min-width: 0;
}

.responder-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.responder-city {
  font-size: 0.85rem;
  color: #666;
  font-weight: normal;
  background: #f0f0f0;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.responder-contacts {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.contact-icon {
  color: #666;
  width: 20px;
  flex-shrink: 0;
}

.contact-link {
  color: #8B5CF6;
  text-decoration: none;
  font-size: 0.95rem;
  word-break: break-all;
}

.contact-link:hover {
  text-decoration: underline;
}

.responder-date {
  font-size: 0.8rem;
  color: #888;
  font-style: italic;
}

.responder-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 100px;
}

.btn-call, .btn-email {
  padding: 0.5rem 0.75rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-call {
  background: #28a745;
  color: white;
}

.btn-call:hover {
  background: #218838;
}

.btn-email {
  background: #6c757d;
  color: white;
}

.btn-email:hover {
  background: #5a6268;
}

.responses-note {
  color: #666;
  font-size: 0.9rem;
  font-style: italic;
  margin-top: 0.5rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  line-height: 1.6;
}

.closure-notice {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 8px;
  margin: 2rem;
}

.notice-icon {
  font-size: 1.5rem;
}

.notice-content h4 {
  color: #856404;
  margin: 0 0 0.5rem 0;
}

.notice-content p {
  color: #856404;
  margin: 0;
  opacity: 0.9;
}

.photo-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

.modal-content {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.modal-close {
  position: absolute;
  top: -40px;
  right: 0;
  background: white;
  color: #333;
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-photo {
  max-width: 100%;
  max-height: 90vh;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 768px) {
  .details-page {
    padding: 0.5rem;
  }

  .details-grid {
    padding: 1rem;
  }

  .details-header-info {
    padding: 1rem;
  }

  .announcement-title {
    font-size: 1.4rem;
  }

  .meta-info {
    flex-direction: column;
    gap: 0.5rem;
  }

  .action-buttons {
    gap: 0.5rem;
  }

  .btn-edit, .btn-contact, .btn-share, .btn-close {
    padding: 0.5rem;
    font-size: 0.9rem;
  }

  .responses-section {
    padding: 1rem;
    margin: 1.5rem 0;
  }

  .responses-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .responder-card {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
    margin: 0;
  }

  .responder-avatar {
    align-self: center;
  }

  .responder-actions {
    width: 100%;
    flex-direction: row;
    justify-content: flex-start;
  }

  .btn-call, .btn-email {
    flex: 1;
  }

  .responder-name {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }

  .already-responded {
    padding: 0.75rem;
  }

  .response-status {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
}
</style>