<template>
  <section class="auth-layout">
    <div class="auth-card">
      <h1>登录 OJ</h1>
      <p>输入账号密码后即可提交代码、查看我的记录和参与排行榜。</p>
      <form @submit.prevent="handleLogin" class="auth-form">
        <input v-model="form.userAccount" placeholder="账号" />
        <input v-model="form.userPassword" type="password" placeholder="密码" />
        <button type="submit" :disabled="loading">{{ loading ? "登录中..." : "登录" }}</button>
      </form>
      <p class="auth-hint">没有账号？先使用默认注册接口创建账号。</p>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "../api";

const router = useRouter();
const form = ref({
  userAccount: "",
  userPassword: ""
});
const loading = ref(false);
const message = ref("");

async function handleLogin() {
  loading.value = true;
  message.value = "";
  try {
    const data = await login(form.value);
    localStorage.setItem("oj_token", data.token);
    message.value = "登录成功，正在跳转";
    router.push("/");
    window.location.reload();
  } catch (error) {
    message.value = error.message || "登录失败";
  } finally {
    loading.value = false;
  }
}
</script>
