<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="contact-info">
        <p>📧 Email: pomogi@poteryashki.ru</p>
        <p>📞 Телефон: +7 (800) 555-35-35</p>
        <p>⏰ Время работы: круглосуточно</p>
      </div>
      <div class="footer-links">
        <a href="#">Политика конфиденциальности</a>
        <a href="#">Пользовательское соглашение</a>
        <a href="#">Помощь</a>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'AppFooter'
}
</script>

<style scoped>
.footer {
  background-color: #6c757d;
  color: white;
  padding: 2rem 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contact-info p {
  margin: 0.5rem 0;
}

.footer-links {
  display: flex;
  gap: 2rem;
}

.footer-links a {
  color: white;
  text-decoration: none;
}

.footer-links a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}
</style>