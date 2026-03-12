<template>
  <section class="ranking-layout">
    <div class="panel ranking-board">
      <div class="panel-header">
        <h1>排行榜</h1>
        <button @click="loadRanking">刷新榜单</button>
      </div>
      <div class="rank-list">
        <article v-for="(item, index) in ranking" :key="item.userId" class="rank-item">
          <div class="rank-index">{{ index + 1 }}</div>
          <div>
            <h3>{{ item.userName }}</h3>
            <p>用户 ID: {{ item.userId }}</p>
          </div>
          <div class="rank-score">{{ item.solvedCount }} 题</div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { getRanking } from "../api";

const ranking = ref([]);

async function loadRanking() {
  ranking.value = await getRanking({ limit: 10 });
}

onMounted(loadRanking);
</script>
