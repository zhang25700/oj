<template>
  <section class="hero">
    <div>
      <p class="eyebrow">Algorithm Arena</p>
      <h1>&#22312;&#32447;&#21028;&#39064;&#24179;&#21488;</h1>
      <p class="hero-text">&#25903;&#25345;&#39064;&#30446;&#26816;&#32034;&#12289;&#22312;&#32447;&#25552;&#20132;&#12289;&#24322;&#27493;&#21028;&#39064;&#12289;&#25552;&#20132;&#26597;&#35810;&#19982;&#23454;&#26102;&#25490;&#34892;&#27036;&#12290;</p>
    </div>
    <div class="hero-card">
      <p>&#24555;&#36895;&#31579;&#36873;</p>
      <div class="filters">
        <input v-model="query.keyword" :placeholder="text.keywordPlaceholder" />
        <select v-model="query.difficulty">
          <option value="">{{ text.allDifficulty }}</option>
          <option value="easy">{{ text.easy }}</option>
          <option value="medium">{{ text.medium }}</option>
          <option value="hard">{{ text.hard }}</option>
        </select>
        <input v-model="query.tag" :placeholder="text.tagPlaceholder" />
      </div>
      <button @click="loadQuestions">{{ text.search }}</button>
    </div>
  </section>

  <section class="panel">
    <div class="panel-header">
      <h2>&#39064;&#30446;&#21015;&#34920;</h2>
      <span>{{ total }} {{ text.problemUnit }}</span>
    </div>
    <div class="question-grid">
      <article v-for="item in questions" :key="item.id" class="question-card">
        <div class="question-meta">
          <span class="difficulty" :data-level="item.difficulty">{{ item.difficulty }}</span>
          <span>{{ item.acceptedCount }}/{{ item.submitCount }}</span>
        </div>
        <h3>{{ item.title }}</h3>
        <p class="tags">{{ item.tags }}</p>
        <button @click="$router.push(`/question/${item.id}`)">{{ text.viewProblem }}</button>
      </article>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { pageQuestions } from "../api";

const text = {
  keywordPlaceholder: "\u6309\u5173\u952e\u8bcd\u641c\u7d22\u9898\u76ee",
  allDifficulty: "\u5168\u90e8\u96be\u5ea6",
  easy: "\u7b80\u5355",
  medium: "\u4e2d\u7b49",
  hard: "\u56f0\u96be",
  tagPlaceholder: "\u6807\u7b7e\uff0c\u4f8b\u5982 dp",
  search: "\u641c\u7d22\u9898\u76ee",
  problemUnit: "\u9053\u9898\u76ee",
  viewProblem: "\u67e5\u770b\u9898\u76ee"
};

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
