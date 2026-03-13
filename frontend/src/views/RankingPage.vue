<template>
  <section class="ranking-layout">
    <div class="panel ranking-board">
      <div class="panel-header">
        <h1>&#25490;&#34892;&#27036;</h1>
        <button @click="loadRanking">&#21047;&#26032;&#27036;&#21333;</button>
      </div>
      <div class="rank-list">
        <article v-for="(item, index) in ranking" :key="item.userId" class="rank-item">
          <div class="rank-index">{{ index + 1 }}</div>
          <div>
            <h3>{{ item.userName }}</h3>
            <p>&#29992;&#25143; ID&#xff1a;{{ item.userId }}</p>
          </div>
          <div class="rank-score">&#24050;&#36890;&#36807; {{ item.solvedCount }} &#39064;</div>
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
