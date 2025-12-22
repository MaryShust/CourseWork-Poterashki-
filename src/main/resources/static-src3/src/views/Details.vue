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
            <div class="status-badge" :class="announcement.isActive ? (announcement.isFound ? 'found' : 'active') : 'inactive'">
              {{ announcement.isActive ? (announcement.isFound ? '🔍 Нашли' : '🔍 Ищут') : '🔍 Найдено' }}
            </div>
          </div>

          <div class="meta-info">
            <span class="created-date">
              Создано: {{ formatDateTime(announcement.createdAt) }}
            </span>
            <span v-if="announcement.updatedAt !== announcement.createdAt" class="updated-date">
              Обновлено: {{ formatDateTime(announcement.updatedAt) }}
            </span>
          </div>
        </div>

        <div class="details-grid">
          <div class="left-column">
            <div class="photo-section">
              <div v-if="announcement.photoUrl" class="photo-container">
                <img
                  :src="announcement.photoUrl"
                  :alt="announcement.title"
                  class="main-photo"
                  @click="openPhotoModal"
                />
              </div>
              <div v-else class="no-photo">
                <div class="no-photo-icon">📷</div>
                <p>Фотография отсутствует</p>
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
              <h3>Детали потери</h3>
              <div class="details-list">
                <div class="detail-item">
                  <span class="detail-label">🗺️ Город:</span>
                  <span class="detail-value">{{ announcement.city }}</span>
                </div>
                <div v-if="announcement.address" class="detail-item">
                  <span class="detail-label">📍 Адрес:</span>
                  <span class="detail-value">{{ announcement.address }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">📅 Дата потери:</span>
                  <span class="detail-value">{{ formatDate(announcement.lostDate) }}</span>
                </div>
                <div v-if="announcement.color" class="detail-item">
                  <span class="detail-label">🎨 Цвет:</span>
                  <span class="detail-value">{{ announcement.color }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">📋 Категория:</span>
                  <span class="detail-value">{{ getCategoryName(announcement.category) }}</span>
                </div>
              </div>
            </div>

            <div v-if="announcement.reward > 0" class="reward-section">
              <h3>Вознаграждение</h3>
              <div class="reward-amount">
                <span class="reward-icon">💰</span>
                <span class="reward-value">{{ formatCurrency(announcement.reward) }}</span>
              </div>
            </div>

            <div class="author-section">
              <h3>Автор объявления</h3>
              <div class="author-info">
                <div class="author-name">👤 {{ announcement.userName }}</div>
                <div v-if="isCurrentUserAuthor" class="author-note">
                  <span class="your-announcement">(Это ваше объявление)</span>
                </div>
              </div>
            </div>

            <div class="action-buttons">
              <button v-if="isCurrentUserAuthor" class="btn-edit" @click="editAnnouncement">
                ✏️ Редактировать объявление
              </button>

              <div v-if="!isCurrentUserAuthor && hasCurrentUserResponded && announcement.isActive && announcement.isFound" class="already-responded">
                <div class="response-status">
                  <span class="response-icon">✅</span>
                  <span class="response-text">Вы уже откликнулись на это объявление</span>
                </div>
                <div class="response-info">
                  <p>Автор свяжется с вами, если захочет вернуть вещь именно вам</p>
                </div>
              </div>

              <div v-if="!isCurrentUserAuthor && hasCurrentUserResponded && announcement.isActive && !announcement.isFound" class="already-responded">
                <div class="response-status">
                  <span class="response-icon">✅</span>
                  <span class="response-text">Вы уже откликнулись на это объявление</span>
                </div>
                <div class="response-info">
                  <p>Автор свяжется с вами, как только сможет</p>
                </div>
              </div>

              <button v-if="!isCurrentUserAuthor && announcement.isActive && !hasCurrentUserResponded" class="btn-contact" @click="contactAuthor" :disabled="isResponding">
                📞 Откликнуться
              </button>

              <button class="btn-share" @click="shareAnnouncement">
                🔗 Поделиться
              </button>

              <button v-if="isCurrentUserAuthor && announcement.isActive" class="btn-close" @click="close" :disabled="announcement.isActive">
                Закрыть
              </button>

            </div>
          </div>
        </div>

        <div v-if="isCurrentUserAuthor && announcement.respondedUsers && announcement.respondedUsers.length > 0"
             class="responses-section author-responses">
          <div class="responses-header">
            <h3>Люди, откликнувшиеся на ваше объявление</h3>
            <div class="responses-count">
              💬 {{ announcement.respondedUsers.length }}
              {{ getResponsesText(announcement.respondedUsers.length) }} откликнулось
            </div>
          </div>

          <div class="responders-list">
            <div v-for="user in announcement.respondedUsers" :key="user.id" class="responder-card">
              <div class="responder-avatar">
                {{ getUserInitials(user.name) }}
              </div>
              <div class="responder-info">
                <div class="responder-name">
                  {{ user.name }}
                  <span v-if="user.city" class="responder-city">📍 {{ user.city }}</span>
                </div>

                <div class="responder-contacts">
                  <div v-if="user.phone" class="contact-item">
                    <span class="contact-icon">📱</span>
                    <a :href="'tel:' + user.phone" class="contact-link">{{ user.phone }}</a>
                  </div>
                  <div v-if="user.email" class="contact-item">
                    <span class="contact-icon">📧</span>
                    <a :href="'mailto:' + user.email" class="contact-link">{{ user.email }}</a>
                  </div>
                </div>

                <div v-if="user.dateCreating" class="responder-date">
                  Откликнулся: {{ formatDateTime(user.dateCreating) }}
                </div>
              </div>

              <div class="responder-actions">
                <button class="btn-call" @click="callUser(user.phone)" v-if="user.phone">
                  Позвонить
                </button>
                <button class="btn-email" @click="emailUser(user.email)" v-if="user.email">
                  Написать
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!isCurrentUserAuthor && announcement.respondedUsers && announcement.respondedUsers.length > 0"
             class="responses-section public-responses">
          <h3>Отклики на объявление</h3>
          <div class="responses-count">
            💬 На это объявление уже откликнулось {{ announcement.respondedUsers.length }}
            {{ getResponsesText(announcement.respondedUsers.length) }}
          </div>
          <p class="responses-note">
            Контактная информация откликнувшихся видна только автору объявления
          </p>
        </div>

        <div v-if="!announcement.isActive" class="closure-notice">
          <div class="notice-icon">ℹ️</div>
          <div class="notice-content">
            <h4>Объявление закрыто</h4>
            <p>Это объявление больше не актуально. Вещь найдена или объявление удалено автором.</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showPhotoModal && announcement.photoUrl" class="photo-modal" @click="closePhotoModal">
      <div class="modal-content" @click.stop>
        <button class="modal-close" @click="closePhotoModal">×</button>
        <img :src="announcement.photoUrl" :alt="announcement.title" class="modal-photo">
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Details',
  data() {
    return {
      announcement: null,
      loading: false,
      error: '',
      showPhotoModal: false,
      currentUserId: localStorage.getItem('currentUserId') || null,
      isResponding: false
    }
  },
  computed: {
    isCurrentUserAuthor() {
      return this.currentUserId && this.announcement && this.announcement.userId === this.currentUserId
    },

    hasCurrentUserResponded() {
      if (!this.currentUserId || !this.announcement || !this.announcement.respondedUsers) {
        return false
      }
      return this.announcement.respondedUsers.some(user => user.id === this.currentUserId)
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

      } catch (err) {
        console.error('Ошибка загрузки объявления:', err)
        this.error = err.message || 'Не удалось загрузить информацию об объявлении'
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

    getCategoryName(category) {
      const categories = {
        'ELECTRONICS': 'Электроника',
        'DOCUMENTS': 'Документы',
        'KEYS': 'Ключи',
        'WALLET': 'Кошелек/Деньги',
        'JEWELRY': 'Украшения',
        'CLOTHES': 'Одежда',
        'ANIMALS': 'Животные',
        'BAGS': 'Сумки/Рюкзаки',
        'OTHER': 'Другое'
      }
      return categories[category] || category || 'Не указана'
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

    async contactAuthor() {
      if (this.announcement && this.announcement.userId) {
        try {
          const response = await fetch(`/announcements/close?id=${this.currentUserId}`, {
            method: 'POST'
          })

          if (response.ok && (await response.json()) === true) {
            await this.loadAnnouncement()
          } else {
            throw new Error('Ошибка сервера')
          }
        } catch (error) {
          alert('Не удалось отправить отклик')
        }
      }
    },

    async close() {
      if (this.announcement && this.announcement.userId) {
        this.isResponding = true

        try {
          const response = await fetch(`/announcements/respond?id=${this.currentUserId}`, {
            method: 'POST'
          })

          if (response.ok && (await response.json()) === true) {
            await this.loadAnnouncement()
          } else {
            throw new Error('Ошибка сервера')
          }
        } catch (error) {
          alert('Не удалось отправить отклик')
        } finally {
          this.isResponding = false
        }
      }
    },

    shareAnnouncement() {
      if (navigator.share) {
        navigator.share({
          title: `Найдено: ${this.announcement.title}`,
          text: `Помогите найти: ${this.announcement.title}. Город: ${this.announcement.city}`,
          url: window.location.href
        })
      } else {
        navigator.clipboard.writeText(window.location.href)
        alert('Ссылка скопирована в буфер обмена!')
      }
    },

    openPhotoModal() {
      if (this.announcement.photoUrl) {
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

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.found {
  background: #cce5ff;
  color: #004085;
}

.status-badge.inactive {
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

.author-responses {
  padding: 1.5rem 2rem;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

.responses-header {
  margin-bottom: 1.5rem;
}

.author-responses h3 {
  color: #8B5CF6;
  margin-bottom: 0.5rem;
  font-size: 1.3rem;
}

.responses-count {
  color: #666;
  font-size: 0.95rem;
  margin-bottom: 1rem;
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
}

.contact-link {
  color: #8B5CF6;
  text-decoration: none;
  font-size: 0.95rem;
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

.public-responses {
  padding: 1.5rem 2rem;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

.public-responses h3 {
  color: #333;
  margin-bottom: 0.5rem;
  font-size: 1.2rem;
}

.responses-note {
  color: #666;
  font-size: 0.9rem;
  font-style: italic;
  margin-top: 0.5rem;
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

  .responder-card {
    flex-direction: column;
    gap: 1rem;
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