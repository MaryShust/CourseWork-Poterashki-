<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-content">
        <h2>Найди то, что потерял</h2>
        <p>Верни радость владельцу потерянной вещи</p>
        <button class="cta-button" @click="handleStartSearch">
          Начать поиск
        </button>
      </div>
    </section>

    <section class="info-section">
      <h2>О нас</h2>
      <div class="content-card">
        <p>"Потеряшки" - это уникальный сервис, созданный для помощи людям в поиске потерянных вещей и возвращении найденных предметов их владельцам.</p>
        <p>Мы объединяем людей, которые потеряли что-то важное, и тех, кто нашел чужую вещь и хочет вернуть ее владельцу.</p>
      </div>
    </section>

    <section class="info-section">
      <h2>Цель проекта</h2>
      <div class="content-card">
        <p>Наша главная цель - создать сообщество взаимопомощи, где каждый может рассчитывать на помощь других в поиске утерянных ценностей.</p>
        <p>Мы стремимся сократить количество безвозвратно потерянных вещей и восстановить связь между людьми через добрые поступки.</p>
      </div>
    </section>

    <section class="info-section">
      <h2>Что можно сделать в сервисе</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">📝</div>
          <h3>Разместить объявление о потере</h3>
          <p>Расскажите о потерянной вещи, добавьте фото и описание</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🔍</div>
          <h3>Найти потерянные вещи</h3>
          <p>Просматривайте объявления о найденных вещах в вашем городе</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">💰</div>
          <h3>Установить вознаграждение</h3>
          <p>Назначьте денежное вознаграждение за возврат ценной вещи</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📱</div>
          <h3>Быстрый поиск</h3>
          <p>Ищите вещи по категориям, местоположению и дате</p>
        </div>
      </div>
    </section>

    <section class="stats-section">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-number">1,000+</div>
          <div class="stat-label">вернутых вещей</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">500+</div>
          <div class="stat-label">активных пользователей</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">50+</div>
          <div class="stat-label">городов</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">95%</div>
          <div class="stat-label">довольных пользователей</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      currentUserId: null
    }
  },
  mounted() {
    this.checkAuth()
    window.addEventListener('storage', this.checkAuth)
    this.$root.$on('auth-changed', this.checkAuth)
  },
  beforeDestroy() {
    window.removeEventListener('storage', this.checkAuth)
    this.$root.$off('auth-changed', this.checkAuth)
  },
  methods: {
    checkAuth() {
      this.currentUserId = localStorage.getItem('currentUserId')
    },
    handleStartSearch() {
        if (this.currentUserId == null) {
            this.$root.$emit('show-login-modal')
        } else {
            this.$router.push('/dashboard')
        }
    }
  }
}
</script>

<style scoped>
.home-page {
  padding: 0;
}

.hero-section {
  background: linear-gradient(135deg, #8B5CF6 0%, #7c3aed 100%);
  color: white;
  padding: 4rem 2rem;
  text-align: center;
}

.hero-content h2 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.cta-button {
  background: white;
  color: #8B5CF6;
  border: none;
  padding: 1rem 2rem;
  font-size: 1.1rem;
  border-radius: 30px;
  cursor: pointer;
  font-weight: bold;
  transition: transform 0.3s;
}

.cta-button:hover {
  transform: translateY(-2px);
}

.info-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 3rem 2rem;
}

.info-section h2 {
  color: #8B5CF6;
  font-size: 2.5rem;
  text-align: center;
  margin-bottom: 2rem;
}

.content-card {
  background: #f8f9fa;
  padding: 2rem;
  border-radius: 15px;
  border-left: 5px solid #8B5CF6;
  font-size: 1.1rem;
  line-height: 1.8;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s;
  border: 1px solid #e9ecef;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  color: #8B5CF6;
  margin-bottom: 1rem;
  font-size: 1.3rem;
}

.stats-section {
  background: #f8f9fa;
  padding: 4rem 2rem;
}

.stats-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  text-align: center;
}

.stat-item {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.1);
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  color: #8B5CF6;
  display: block;
}

.stat-label {
  color: #666;
  margin-top: 0.5rem;
}

@media (max-width: 768px) {
  .hero-content h2 {
    font-size: 2rem;
  }

  .info-section {
    padding: 2rem 1rem;
  }
}
</style>