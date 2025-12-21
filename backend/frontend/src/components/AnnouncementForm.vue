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
          <label class="radio-option" :class="{ 'selected': formData.type === 'LOST' }">
            <input
              type="radio"
              v-model="formData.type"
              value="LOST"
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

          <label class="radio-option" :class="{ 'selected': formData.type === 'FOUND' }">
            <input
              type="radio"
              v-model="formData.type"
              value="FOUND"
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

        <div v-if="errors.type" class="field-error">{{ errors.type }}</div>
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
            :placeholder="formData.type === 'FOUND' ? 'Например: Найденные ключи, Найденный телефон, Найденный кошелек' : 'Например: Ключи от квартиры, iPhone 13, Кошелек'"
            :class="{ 'error': errors.title }"
            maxlength="100"
          >
          <div v-if="errors.title" class="field-error">{{ errors.title }}</div>
          <div class="field-hint">Максимум 100 символов</div>
        </div>

        <div class="form-group">
          <label for="description">Подробное описание *</label>
          <textarea
            id="description"
            v-model="formData.description"
            rows="4"
            required
            :placeholder="formData.type === 'FOUND' ? 'Опишите найденную вещь подробно: где нашли, состояние, отличительные признаки...' : 'Опишите вещь подробно: отличительные признаки, особенности, состояние...'"
            maxlength="1000"
            :class="{ 'error': errors.description }"
          ></textarea>
          <div v-if="errors.description" class="field-error">{{ errors.description }}</div>
          <div class="field-hint">{{ formData.description.length }}/1000 символов</div>
        </div>
      </div>

      <div class="form-section">
        <h3>{{ formData.type === 'FOUND' ? 'Место и время находки' : 'Место и время потери' }}</h3>

        <div class="form-row">
          <div class="form-group half">
            <label for="city">Город *</label>
            <input
              type="text"
              id="city"
              v-model="formData.place.city"
              required
              placeholder="Например: Москва"
              :class="{ 'error': errors.city }"
            >
            <div v-if="errors.city" class="field-error">{{ errors.city }}</div>
          </div>

          <div class="form-group half">
            <label for="street">Улица</label>
            <input
              type="text"
              id="street"
              v-model="formData.place.street"
              :placeholder="formData.type === 'FOUND' ? 'Где именно нашли вещь' : 'Улица'"
            >
          </div>
        </div>

        <div class="form-row">
          <div class="form-group half">
            <label for="house">Дом</label>
            <input
              type="text"
              id="house"
              v-model="formData.place.house"
              placeholder="Номер дома"
            >
          </div>

          <div class="form-group half">
            <label for="placeName">Название места</label>
            <input
              type="text"
              id="placeName"
              v-model="formData.place.placeName"
              placeholder="Например: ТЦ 'Мега', Парк 'Сокольники'"
            >
          </div>
        </div>

        <div class="form-group">
          <label for="extraDescription">Дополнительное описание места</label>
          <textarea
            id="extraDescription"
            v-model="formData.place.extraDescription"
            rows="3"
            placeholder="Дополнительные детали о месте, ориентиры и т.д."
            maxlength="500"
          ></textarea>
          <div class="field-hint">{{ formData.place.extraDescription.length }}/500 символов</div>
        </div>

        <div class="form-group">
          <label for="date">{{ formData.type === 'FOUND' ? 'Дата находки *' : 'Дата потери *' }}</label>
          <input
            type="datetime-local"
            id="date"
            v-model="formData.date"
            required
            :class="{ 'error': errors.date }"
            :max="currentDateTime"
          >
          <div v-if="errors.date" class="field-error">{{ errors.date }}</div>
          <div class="field-hint">Укажите дату и время</div>
        </div>

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
          <div class="field-hint" v-if="formData.type === 'FOUND'">
            Укажите сумму, которую хотите получить за возврат находки (по желанию)
          </div>
          <div class="field-hint" v-else>
            Укажите сумму, которую готовы заплатить за возврат
          </div>
        </div>
      </div>

      <div class="form-section">
        <h3>{{ formData.type === 'FOUND' ? 'Фотография находки' : 'Фотография вещи' }}</h3>

        <div class="form-group">
          <label for="photo">Фотография</label>
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
              <span v-if="!selectedFile">Выберите файл</span>
              <span v-else>Файл выбран: {{ selectedFile.name }}</span>
            </label>
            <button
              type="button"
              class="btn-remove"
              @click="removePhoto"
              v-if="selectedFile"
            >
              ×
            </button>
          </div>

          <!-- Превью существующих фото -->
          <div class="existing-photos" v-if="existingPhotos.length > 0">
            <h4>Текущие фотографии:</h4>
            <div class="photos-grid">
              <div class="photo-item" v-for="(photo, index) in existingPhotos" :key="index">
                <img :src="getPhotoUrl(photo)" alt="Фото" @error="handleImageError">
                <button
                  type="button"
                  class="btn-remove-small"
                  @click="removeExistingPhoto(index)"
                  v-if="isEditMode"
                >
                  ×
                </button>
              </div>
            </div>
          </div>

          <!-- Превью новой фото -->
          <div class="preview" v-if="photoPreview">
            <h4>Новое фото:</h4>
            <img :src="photoPreview" alt="Предпросмотр">
          </div>

          <div class="field-hint">Максимальный размер: 5MB. Форматы: JPG, PNG, GIF</div>
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
    const currentDate = new Date()
    // Форматируем дату для input[type="datetime-local"]
    const formattedDate = new Date(currentDate.getTime() - currentDate.getTimezoneOffset() * 60000)
      .toISOString()
      .slice(0, 16)

    return {
      loading: false,
      successMessage: '',
      errorMessage: '',
      errors: {},
      photoPreview: null,
      selectedFile: null,
      existingPhotos: [],
      editingData: null,
      formData: {
        type: 'LOST',
        title: '',
        description: '',
        date: formattedDate,
        place: {
          city: '',
          street: '',
          house: '',
          placeName: '',
          extraDescription: ''
        },
        reward: 0
      }
    }
  },
  computed: {
    isEditMode() {
      return !!this.announcementId
    },
    currentDateTime() {
      const now = new Date()
      // Получаем смещение временной зоны в минутах
      const timezoneOffset = now.getTimezoneOffset()
      // Преобразуем в миллисекунды и корректируем
      const localTime = new Date(now.getTime() - (timezoneOffset * 60000))
      return localTime.toISOString().slice(0, 16)
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
        const userId = localStorage.getItem('currentUserId')
        const response = await fetch(`/api/thing/${this.announcementId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': userId,
                'id': this.announcementId
            }
        })

        if (!response.ok) throw new Error('Ошибка загрузки объявления')

        const data = await response.json()
        this.editingData = { ...data }

        // Сохраняем существующие фото
        this.existingPhotos = data.photos || []

        // Преобразуем данные для формы
        this.formData = {
          type: data.type || 'LOST',
          title: this.extractTitleFromDescription(data.description) || '',
          description: data.description || '',
          date: data.date ? new Date(data.date).toISOString().slice(0, 16) : this.formData.date,
          place: {
            city: data.place?.city || '',
            street: data.place?.street || '',
            house: data.place?.house || '',
            placeName: data.place?.placeName || '',
            extraDescription: data.place?.extraDescription || ''
          },
          reward: this.extractRewardFromDescription(data.description) || 0
        }

      } catch (error) {
        console.error('Ошибка загрузки объявления:', error)
        this.errorMessage = 'Не удалось загрузить объявление'
      } finally {
        this.loading = false
      }
    },

    extractTitleFromDescription(description) {
      // Простая логика извлечения заголовка из описания
      if (!description) return ''
      const firstSentence = description.split('.')[0]
      return firstSentence.length > 100 ? firstSentence.substring(0, 100) : firstSentence
    },

    extractRewardFromDescription(description) {
      if (!description) return 0
      const rewardMatch = description.match(/(\d+)[\s]*руб/)
      return rewardMatch ? parseInt(rewardMatch[1]) : 0
    },

    getPhotoUrl(photo) {
      // Если фото - это URL или base64 строка
      if (photo.startsWith('http') || photo.startsWith('data:')) {
        return photo
      }
      // Иначе предполагаем, что это имя файла
      return `/api/thing/${this.announcementId}/photo/${photo}`
    },

    handleImageError(event) {
      // Заменяем сломанное изображение на placeholder
      event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjIwMCIgaGVpZ2h0PSIyMDAiIGZpbGw9IiNlZWVlZWUiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiBmaWxsPSIjOTk5OTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+PGZvbnQgc2l6ZT0iMTQiPlBob3RvPC9mb250PjwvdGV4dD48L3N2Zz4='
    },

    handleTypeChange() {
      // Можно добавить логику при изменении типа
      console.log('Тип объявления изменен на:', this.formData.type)
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

      this.selectedFile = file

      const reader = new FileReader()
      reader.onload = (e) => {
        this.photoPreview = e.target.result
      }
      reader.readAsDataURL(file)
    },

    removePhoto() {
      this.selectedFile = null
      this.photoPreview = null
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = ''
      }
    },

    removeExistingPhoto(index) {
      this.existingPhotos.splice(index, 1)
    },

    validateForm() {
      this.errors = {}
      let isValid = true

      if (!this.formData.type) {
        this.errors.type = 'Выберите тип объявления'
        isValid = false
      }

      if (!this.formData.title.trim()) {
        this.errors.title = 'Название обязательно'
        isValid = false
      }

      if (!this.formData.place.city.trim()) {
        this.errors.city = 'Город обязателен'
        isValid = false
      }

      if (!this.formData.date) {
        this.errors.date = 'Дата и время обязательны'
        isValid = false
      }

      if (!this.formData.description.trim()) {
        this.errors.description = 'Описание обязательно'
        isValid = false
      }

      return isValid
    },

    async handleSubmit() {
      if (!this.validateForm()) return

      this.loading = true
      this.successMessage = ''
      this.errorMessage = ''

      try {
        let thingId = this.announcementId
        const userId = localStorage.getItem('currentUserId')
        let version

        // Создаем или обновляем вещь
        const thingPayload = this.buildThingPayload()

        if (this.isEditMode) {
          const result = await this.updateThing(userId, thingId, thingPayload)
          version = result.version
        } else {
          const result = await this.createThing(userId, thingPayload)
          thingId = result.id
          version = result.version
        }

        // Загружаем фото если есть
        if (this.selectedFile && thingId) {
          await this.uploadPhoto(userId, thingId, this.selectedFile, version)
        }


        // Показываем успешное сообщение
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

    buildThingPayload() {
      const currentDate = new Date().toISOString()

      if (this.isEditMode) {
        // Режим редактирования
        return {
          title: this.formData.title,
          fee: this.formData.reward,
          owner: this.editingData.owner,
          type: this.formData.type,
          date: new Date(this.formData.date).toISOString(),
          place: {
            city: this.formData.place.city,
            street: this.formData.place.street || '',
            house: this.formData.place.house || '',
            placeName: this.formData.place.placeName || '',
            extraDescription: this.formData.place.extraDescription || ''
          },
          description: this.formData.description,
          photos: this.existingPhotos,
          id: this.announcementId,
          createdAt: this.editingData.createdAt,
          completedAt: this.editingData.completedAt || null,
          version: this.editingData.version || 0
        }
      } else {
        // Режим создания
        return {
          title: this.formData.title,
          fee: this.formData.reward,
          type: this.formData.type,
          date: new Date(this.formData.date).toISOString(),
          place: {
            city: this.formData.place.city,
            street: this.formData.place.street || '',
            house: this.formData.place.house || '',
            placeName: this.formData.place.placeName || '',
            extraDescription: this.formData.place.extraDescription || ''
          },
          description: this.formData.description
        }
      }
    },

    async createThing(userId, payload) {
      const response = await fetch('/api/things/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': userId
        },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || 'Ошибка создания объявления')
      }

      return await response.json()
    },

    async updateThing(userId, thingId, payload) {
      const response = await fetch(`/api/thing/${thingId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': userId
        },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || 'Ошибка обновления объявления')
      }

      return await response.json()
    },

    async uploadPhoto(userId, thingId, file, version) {
      const formData = new FormData()
      formData.append('file', file)

      const response = await fetch(`/api/thing/${thingId}/upload-photo?version=${version}`, {
        method: 'POST',
        headers: {
            'Authorization': userId
        },
        body: formData
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || 'Ошибка загрузки фото')
      }

    },

    resetForm() {
      this.formData = {
        type: 'LOST',
        title: '',
        description: '',
        date: new Date().toISOString().slice(0, 16),
        place: {
          city: '',
          street: '',
          house: '',
          placeName: '',
          extraDescription: ''
        },
        reward: 0
      }
      this.selectedFile = null
      this.photoPreview = null
      this.existingPhotos = []
      this.editingData = null
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
  max-width: 900px;
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
.form-group select.error,
.form-group textarea.error {
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

.btn-remove-small {
  background: #ff4757;
  color: white;
  border: none;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: -8px;
  right: -8px;
  z-index: 1;
}

.btn-remove-small:hover {
  background: #ff3742;
}

.existing-photos {
  margin-top: 1rem;
}

.existing-photos h4 {
  margin-bottom: 0.5rem;
  font-size: 1rem;
  color: #666;
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 1rem;
  margin-bottom: 1rem;
}

.photo-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e9ecef;
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview {
  margin-top: 1rem;
  max-width: 300px;
}

.preview h4 {
  margin-bottom: 0.5rem;
  font-size: 1rem;
  color: #666;
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

  .photos-grid {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  }

  .photo-item {
    width: 80px;
    height: 80px;
  }
}
</style>