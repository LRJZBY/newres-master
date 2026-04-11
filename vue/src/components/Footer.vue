<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-info">
        <p>&copy; {{ currentYear }} 新闻推荐系统. weiyan.top</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref } from "vue";

const currentYear = ref(new Date().getFullYear());
</script>

<style scoped>
.footer {
  background-color: #f5f5f5;
  padding: 20px 0;
  margin-top: 40px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.footer-info {
  color: #666;
  font-size: 14px;
}
</style>
