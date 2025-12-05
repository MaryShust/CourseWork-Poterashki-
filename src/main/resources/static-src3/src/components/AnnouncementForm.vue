<template>
  <div class="announcement-form-page">
    <div class="form-header">
      <h2>{{ isEditMode ? 'Редактирование объявления' : 'Создание нового объявления' }}</h2>
      <p>{{ isEditMode ? 'Внесите изменения в объявление' : 'Расскажите о потерянной вещи' }}</p>
    </div>

    <form @submit.prevent="handleSubmit" class="announcement-form">
      <div class="form-section type-section">
        <h3>Тип объявления</h3>

        <div class="radio-group">
          <label class="radio-option" :class="{ 'selected': formData.announcementType === 'lost' }">
            <input
              type="radio"
              v-model="formData.announcementType"
              value="lost"
              @change="handleTypeChange"
            >
            <div class="radio-content">
              <span class="radio-icon">😔</span>
              <div>
                <span class="radio-title">Вы потеряли</span>
                <span class="radio-description">Ищете свою вещь</span>
              </div>
            </div>
          </label>

          <label class="radio-option" :class="{ 'selected': formData.announcementType === 'found' }">
            <input
              type="radio"
              v-model="formData.announcementType"
              value="found"
              @change="handleTypeChange"
            >
            <div class="radio-content">
              <span class="radio-icon">🔍</span>
              <div>
                <span class="radio-title">Вы нашли</span>
                <span class="radio-description">Нашли чужую вещь</span>
              </div>
            </div>
          </label>
        </div>

        <div v-if="errors.announcementType" class="field-error">{{ errors.announcementType }}</div>
      </div>

      <div class="form-section">
        <h3>Основная информация</h3>

        <div class="form-group">
          <label for="title">Название вещи *</label>
          <input
            type="text"
            id="title"
            v-model="formData.title"
            required
            :placeholder="formData.announcementType === 'found' ? 'Например: Найденные ключи, Найденный телефон, Найденный кошелек' : 'Например: Ключи от квартиры, iPhone 13, Кошелек'"
            :class="{ 'error': errors.title }"
            maxlength="100"
          >
          <div v-if="errors.title" class="field-error">{{ errors.title }}</div>
          <div class="field-hint">Максимум 100 символов</div>
        </div>

        <div class="form-group">
          <label for="category">Категория</label>
          <select
            id="category"
            v-model="formData.category"
            :class="{ 'error': errors.category }"
          >
            <option value="">Выберите категорию</option>
            <option value="ELECTRONICS">Электроника</option>
            <option value="DOCUMENTS">Документы</option>
            <option value="KEYS">Ключи</option>
            <option value="WALLET">Кошелек/Деньги</option>
            <option value="JEWELRY">Украшения</option>
            <option value="CLOTHES">Одежда</option>
            <option value="ANIMALS">Животные</option>
            <option value="BAGS">Сумки/Рюкзаки</option>
            <option value="OTHER">Другое</option>
          </select>
          <div v-if="errors.category" class="field-error">{{ errors.category }}</div>
        </div>

        <div class="form-group">
          <label for="description">Подробное описание</label>
          <textarea
            id="description"
            v-model="formData.description"
            rows="4"
            :placeholder="formData.announcementType === 'found' ? 'Опишите найденную вещь подробно: где нашли, состояние, отличительные признаки...' : 'Опишите вещь подробно: отличительные признаки, особенности, состояние...'"
            maxlength="1000"
          ></textarea>
          <div class="field-hint">{{ formData.description.length }}/1000 символов</div>
        </div>
      </div>

      <div class="form-section">
        <h3>{{ formData.announcementType === 'found' ? 'Место и время находки' : 'Место и время потери' }}</h3>

        <div class="form-row">
          <div class="form-group half">
            <label for="city">Город *</label>
            <input
              type="text"
              id="city"
              v-model="formData.city"
              required
              placeholder="Например: Москва"
              :class="{ 'error': errors.city }"
            >
            <div v-if="errors.city" class="field-error">{{ errors.city }}</div>
          </div>

          <div class="form-group half">
            <label for="address">Адрес</label>
            <input
              type="text"
              id="address"
              v-model="formData.address"
              :placeholder="formData.announcementType === 'found' ? 'Где именно нашли вещь' : 'Улица, дом, квартира'"
            >
          </div>
        </div>

        <div class="form-group">
          <label for="lostDate">{{ formData.announcementType === 'found' ? 'Дата находки *' : 'Дата потери *' }}</label>
          <input
            type="date"
            id="lostDate"
            v-model="formData.lostDate"
            required
            :class="{ 'error': errors.lostDate }"
            :max="today"
          >
          <div v-if="errors.lostDate" class="field-error">{{ errors.lostDate }}</div>
        </div>

        <div class="form-group">
          <label for="color">Цвет вещи</label>
          <input
            type="text"
            id="color"
            v-model="formData.color"
            placeholder="Например: черный, красный, синий с белым"
          >
        </div>
      </div>

      <div class="form-section">
        <h3>{{ formData.announcementType === 'found' ? 'Вознаграждение и фотография находки' : 'Вознаграждение и фотография' }}</h3>

        <div class="form-group">
          <label for="reward">Вознаграждение (руб.)</label>
          <input
            type="number"
            id="reward"
            v-model.number="formData.reward"
            placeholder="0"
            min="0"
            step="100"
          >
          <div class="field-hint" v-if="formData.announcementType === 'found'">
            Укажите сумму, которую хотите получить за возврат находки (по желанию)
          </div>
          <div class="field-hint" v-else>
            Укажите сумму, которую готовы заплатить за возврат
          </div>
        </div>

        <div class="form-group">
          <label for="photo">Фотография вещи</label>
          <div class="file-upload">
            <input
              type="file"
              id="photo"
              ref="fileInput"
              @change="handleFileUpload"
              accept="image/*"
              class="file-input"
            >
            <label for="photo" class="file-label">
              <span v-if="!formData.photo">Выберите файл</span>
              <span v-else>Файл выбран: {{ formData.photo.name }}</span>
            </label>
            <button
              type="button"
              class="btn-remove"
              @click="removePhoto"
              v-if="formData.photo"
            >
              ×
            </button>
          </div>
          <div class="preview" v-if="photoPreview">
            <img :src="photoPreview" alt="Предпросмотр">
          </div>
          <div class="field-hint">Максимальный размер: 5MB. Форматы: JPG, PNG</div>
        </div>
      </div>

      <div class="form-actions">
        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Сохранение...' : (isEditMode ? 'Сохранить изменения' : 'Создать объявление') }}
        </button>
        <button type="button" class="btn-secondary" @click="cancel">
          Отмена
        </button>
        <button type="button" class="btn-text" @click="resetForm">
          Очистить форму
        </button>
      </div>

      <div v-if="successMessage" class="success-message">
        ✅ {{ successMessage }}
      </div>

      <div v-if="errorMessage" class="error-message">
        ❌ {{ errorMessage }}
      </div>
    </form>
  </div>
</template>

<script>
export default {
  name: 'AnnouncementForm',
  props: {
    announcementId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      loading: false,
      successMessage: '',
      errorMessage: '',
      errors: {},
      photoPreview: null,
      formData: {
        announcementType: 'lost', // По умолчанию "Вы потеряли"
        title: '',
        category: '',
        description: '',
        city: '',
        address: '',
        lostDate: '',
        color: '',
        reward: 0,
        photo: null
      }
    }
  },
  computed: {
    isEditMode() {
      return !!this.announcementId
    },
    today() {
      return new Date().toISOString().split('T')[0]
    }
  },
  mounted() {
    this.checkAuth()
    if (this.isEditMode) {
      this.loadAnnouncement()
    }
  },
  methods: {
    checkAuth() {
      const userId = localStorage.getItem('currentUserId')
      if (!userId) {
        this.$router.push('/')
      }
    },

    async loadAnnouncement() {
      if (!this.announcementId) return

      this.loading = true
      try {
        const response = await fetch(`/announcements/${this.announcementId}`)
        if (!response.ok) throw new Error('Ошибка загрузки')

        const data = await response.json()

        const announcementType = data.isFound ? 'found' : 'lost'

        this.formData = {
          announcementType: announcementType,
          title: data.title || '',
          category: data.category || '',
          description: data.description || '',
          city: data.city || '',
          address: data.address || '',
          lostDate: data.lostDate ? data.lostDate.split('T')[0] : '',
          color: data.color || '',
          reward: data.reward || 0,
          photo: null // Фото нужно загружать отдельно или в base64
        }

        if (data.photoUrl) {
          this.photoPreview = data.photoUrl
        }
      } catch (error) {
        console.error('Ошибка загрузки объявления:', error)
        this.errorMessage = 'Не удалось загрузить объявление'
      } finally {
        this.loading = false
      }
    },

    handleTypeChange() {
      console.log(this.formData.announcementType)
      //this.formData.announcementType = type
      console.log(this.formData.announcementType)
    },

    handleFileUpload(event) {
      const file = event.target.files[0]
      if (!file) return

      // Проверка размера (5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage = 'Файл слишком большой (макс. 5MB)'
        return
      }

      if (!file.type.match('image.*')) {
        this.errorMessage = 'Можно загружать только изображения'
        return
      }

      this.formData.photo = file

      const reader = new FileReader()
      reader.onload = (e) => {
        this.photoPreview = e.target.result
      }
      reader.readAsDataURL(file)
    },

    removePhoto() {
      this.formData.photo = null
      this.photoPreview = null
      this.$refs.fileInput.value = ''
    },

    validateForm() {
      this.errors = {}
      let isValid = true

      if (!this.formData.announcementType) {
        this.errors.announcementType = 'Выберите тип объявления'
        isValid = false
      }

      if (!this.formData.title.trim()) {
        this.errors.title = 'Название обязательно'
        isValid = false
      }

      if (!this.formData.city.trim()) {
        this.errors.city = 'Город обязателен'
        isValid = false
      }

      if (!this.formData.lostDate) {
        this.errors.lostDate = 'Дата обязательна'
        isValid = false
      }

      return isValid
    },


    async handleSubmit() {
      if (!this.validateForm()) return

      this.loading = true
      this.successMessage = ''
      this.errorMessage = ''

      console.log(this.formData.announcementType)
      console.log(this.formData.announcementType === 'found')
      console.log(this.formData.announcementType == 'found')

      try {
        const userId = localStorage.getItem('currentUserId')
        const userName = localStorage.getItem('currentUser')
        if (!userId) throw new Error('Пользователь не авторизован')

        const payload = {
          title: this.formData.title,
          category: this.formData.category || '',
          description: this.formData.description || '',
          city: this.formData.city,
          address: this.formData.address || '',
          lostDate: this.formData.lostDate,
          color: this.formData.color || '',
          reward: this.formData.reward || 0,
          userId: userId,
          userName: userName,
          isFound: this.formData.announcementType === 'found'
        }

        // Если есть фото, конвертируем в base64
        if (this.formData.photo) {
          const base64Photo = await this.convertFileToBase64(this.formData.photo)
          payload.photoBase64 = base64Photo.split(',')[1] // Убираем префикс data:image/...
          payload.photoName = this.formData.photo.name
          payload.photoContentType = this.formData.photo.type
        }

        const url = this.isEditMode
          ? `/announcements/${this.announcementId}`
          : '/announcements'

        const method = this.isEditMode ? 'PUT' : 'POST'

        const response = await fetch(url, {
          method: method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(payload)
        })

        if (!response.ok) {
          const error = await response.text()
          throw new Error(error || 'Ошибка сервера')
        }

        const result = await response.json()

        this.successMessage = this.isEditMode
          ? 'Объявление успешно обновлено!'
          : 'Объявление успешно создано!'

        // Перенаправляем через 2 секунды
        setTimeout(() => {
          this.$router.push('/dashboard')
        }, 2000)

      } catch (error) {
        console.error('Ошибка сохранения:', error)
        this.errorMessage = error.message || 'Ошибка сохранения. Попробуйте снова.'
      } finally {
        this.loading = false
      }
    },

    resetForm() {
      this.formData = {
        announcementType: 'lost', // Сбрасываем к значению по умолчанию
        title: '',
        category: '',
        description: '',
        city: '',
        address: '',
        lostDate: '',
        color: '',
        reward: 0,
        photo: null
      }
      this.photoPreview = null
      this.errors = {}
      this.successMessage = ''
      this.errorMessage = ''
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = ''
      }
    },

    cancel() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.announcement-form-page {
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
  min-height: 100vh;
}

.form-header {
  text-align: center;
  margin-bottom: 2rem;
}

.form-header h2 {
  color: #8B5CF6;
  font-size: 2.2rem;
  margin-bottom: 0.5rem;
}

.form-header p {
  color: #666;
  font-size: 1.1rem;
}

.announcement-form {
  background: white;
  border-radius: 15px;
  padding: 2rem;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}

.form-section {
  margin-bottom: 2.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 1.5rem;
}

.form-section h3 {
  color: #333;
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #f0f0f0;
}

.radio-group {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .radio-group {
    flex-direction: column;
  }
}

.radio-option {
  flex: 1;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.radio-option:hover {
  border-color: #8B5CF6;
  background-color: #f9f7ff;
  transform: translateY(-2px);
}

.radio-option.selected {
  border-color: #8B5CF6;
  background-color: #f9f7ff;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.15);
}

.radio-option input[type="radio"] {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.radio-content {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.radio-icon {
  font-size: 2rem;
}

.radio-title {
  display: block;
  font-weight: 600;
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 0.25rem;
}

.radio-description {
  display: block;
  font-size: 0.9rem;
  color: #666;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group.half {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
  font-size: 0.95rem;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #8B5CF6;
  outline: none;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.form-group input.error,
.form-group select.error {
  border-color: #ff4757;
  background-color: #fff5f5;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.field-error {
  color: #ff4757;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.field-hint {
  color: #666;
  font-size: 0.85rem;
  margin-top: 0.5rem;
  font-style: italic;
}

.file-upload {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.file-input {
  display: none;
}

.file-label {
  flex: 1;
  padding: 0.75rem 1rem;
  background: #f8f9fa;
  border: 2px dashed #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
}

.file-label:hover {
  background: #e9ecef;
  border-color: #8B5CF6;
}

.btn-remove {
  background: #ff4757;
  color: white;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove:hover {
  background: #ff3742;
}

.preview {
  margin-top: 1rem;
  max-width: 200px;
}

.preview img {
  width: 100%;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  flex-wrap: wrap;
}

.btn-primary, .btn-secondary, .btn-text {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s;
  border: none;
  min-width: 150px;
}

.btn-primary {
  background: #8B5CF6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #7c3aed;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.btn-text {
  background: transparent;
  color: #666;
  text-decoration: underline;
  min-width: auto;
}

.btn-text:hover {
  color: #8B5CF6;
}

.success-message {
  background: #d4edda;
  color: #155724;
  padding: 1rem;
  border-radius: 8px;
  margin-top: 1.5rem;
  border: 1px solid #c3e6cb;
  animation: fadeIn 0.3s ease;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border-radius: 8px;
  margin-top: 1.5rem;
  border: 1px solid #f5c6cb;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 768px) {
  .announcement-form-page {
    padding: 1rem;
  }

  .announcement-form {
    padding: 1.5rem;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary, .btn-secondary, .btn-text {
    width: 100%;
    min-width: auto;
  }
}
</style>