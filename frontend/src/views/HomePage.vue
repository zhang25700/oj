<template>
  <section class="hero">
    <div>
      <p class="eyebrow">Algorithm Arena</p>
      <h1>在线判题平台</h1>
      <p class="hero-text">支持题目检索、在线提交、异步判题、提交查询与实时排行榜。</p>
    </div>
    <div class="hero-card">
      <p>快速筛选</p>
      <div class="filters">
        <input v-model="query.keyword" placeholder="搜索题目关键词" />
        <select v-model="query.difficulty">
          <option value="">全部难度</option>
          <option value="easy">简单</option>
          <option value="medium">中等</option>
          <option value="hard">困难</option>
        </select>
        <input v-model="query.tag" placeholder="标签，如 dp" />
      </div>
      <button @click="loadQuestions">搜索题目</button>
    </div>
  </section>

  <section class="panel">
    <div class="panel-header">
      <h2>题目列表</h2>
      <span>{{ total }} 道题</span>
    </div>
    <div class="question-grid">
      <article v-for="item in questions" :key="item.id" class="question-card">
        <div class="question-meta">
          <span class="difficulty" :data-level="item.difficulty">{{ item.difficulty }}</span>
          <span>{{ item.acceptedCount }}/{{ item.submitCount }}</span>
        </div>
        <h3>{{ item.title }}</h3>
        <p class="tags">{{ item.tags }}</p>
        <button @click="$router.push(`/question/${item.id}`)">查看题目</button>
      </article>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { pageQuestions } from "../api";

const questions = ref([]);
const total = ref(0);
const query = ref({
  keyword: "",
  difficulty: "",
  tag: "",
  current: 1,
  pageSize: 12
});

async function loadQuestions() {
  const data = await pageQuestions(query.value);
  questions.value = data.records || [];
  total.value = data.total || 0;
}

onMounted(loadQuestions);
</script>
