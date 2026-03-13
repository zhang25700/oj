<template>
  <section class="auth-layout">
    <div class="auth-card">
      <h1>&#30331;&#24405;</h1>
      <p>&#30331;&#24405;&#21518;&#21487;&#20197;&#25552;&#20132;&#20195;&#30721;&#12289;&#26597;&#30475;&#25552;&#20132;&#35760;&#24405;&#65292;&#24182;&#21442;&#19982;&#25490;&#34892;&#27036;&#12290;</p>
      <form @submit.prevent="handleLogin" class="auth-form">
        <input v-model="form.userAccount" :placeholder="text.account" />
        <input v-model="form.userPassword" type="password" :placeholder="text.password" />
        <button type="submit" :disabled="loading">{{ loading ? text.loggingIn : text.login }}</button>
      </form>
      <p class="auth-hint">&#22914;&#26524;&#27809;&#26377;&#36134;&#21495;&#65292;&#35831;&#20808;&#35843;&#29992;&#27880;&#20876;&#25509;&#21475;&#21019;&#24314;&#36134;&#21495;&#12290;</p>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "../api";

const text = {
  account: "\u8d26\u53f7",
  password: "\u5bc6\u7801",
  loggingIn: "\u767b\u5f55\u4e2d...",
  login: "\u767b\u5f55",
  success: "\u767b\u5f55\u6210\u529f\uff0c\u6b63\u5728\u8df3\u8f6c...",
  failed: "\u767b\u5f55\u5931\u8d25"
};

const emit = defineEmits(["refresh-user"]);
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
    emit("refresh-user");
    message.value = text.success;
    await router.replace("/");
  } catch (error) {
    message.value = error.message || text.failed;
  } finally {
    loading.value = false;
  }
}
</script>
