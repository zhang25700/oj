<template>
  <section class="detail-layout" v-if="question">
    <article class="panel detail-card">
      <div class="panel-header">
        <div>
          <p class="eyebrow">{{ question.difficulty }}</p>
          <h1>{{ question.title }}</h1>
        </div>
        <div class="stat-row">
          <span>时限 {{ question.timeLimit }} ms</span>
          <span>内存 {{ question.memoryLimit }} MB</span>
        </div>
      </div>
      <div class="tags">{{ question.tags }}</div>
      <div class="content-box">{{ question.content }}</div>
    </article>

    <aside class="panel submit-card">
      <h2>在线提交</h2>
      <select v-model="submitForm.language">
        <option value="java">Java</option>
        <option value="cpp">C++</option>
        <option value="c">C</option>
      </select>
      <textarea v-model="submitForm.code" rows="16" placeholder="输入你的代码"></textarea>
      <button @click="handleSubmit" :disabled="loading">{{ loading ? "提交中..." : "提交代码" }}</button>
      <p v-if="submitMessage" class="message">{{ submitMessage }}</p>
    </aside>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { getQuestionDetail, submitCode } from "../api";

const route = useRoute();
const question = ref(null);
const loading = ref(false);
const submitMessage = ref("");
const submitForm = ref({
  questionId: Number(route.params.id),
  language: "java",
  code: ""
});

async function loadQuestion() {
  question.value = await getQuestionDetail(route.params.id);
}

async function handleSubmit() {
  loading.value = true;
  submitMessage.value = "";
  try {
    const submitId = await submitCode(submitForm.value);
    submitMessage.value = `提交成功，提交编号 ${submitId}`;
  } catch (error) {
    submitMessage.value = error.message || "提交失败";
  } finally {
    loading.value = false;
  }
}

onMounted(loadQuestion);
</script>
