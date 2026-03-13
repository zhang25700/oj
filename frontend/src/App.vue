<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand" @click="$router.push('/')">Online Judge</div>
      <nav class="nav">
        <router-link to="/">&#39064;&#24211;</router-link>
        <router-link to="/submissions">&#25105;&#30340;&#25552;&#20132;</router-link>
        <router-link to="/ranking">&#25490;&#34892;&#27036;</router-link>
      </nav>
      <div class="topbar-actions">
        <template v-if="user">
          <span class="user-pill">{{ user.userName }}</span>
        </template>
        <router-link v-else to="/login" class="login-link">&#30331;&#24405;</router-link>
      </div>
    </header>
    <main class="page-wrap">
      <router-view @refresh-user="loadUser" />
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { getCurrentUser } from "./api";

const user = ref(null);

async function loadUser() {
  try {
    user.value = await getCurrentUser();
  } catch {
    user.value = null;
  }
}

onMounted(loadUser);
</script>
