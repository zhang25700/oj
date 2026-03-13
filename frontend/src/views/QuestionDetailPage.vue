<template>
  <section class="detail-layout" v-if="question">
    <article class="panel detail-card">
      <div class="panel-header">
        <div>
          <p class="eyebrow">{{ question.difficulty }}</p>
          <h1>{{ question.title }}</h1>
        </div>
        <div class="stat-row">
          <span>{{ text.timeLimit }} {{ question.timeLimit }} ms</span>
          <span>{{ text.memoryLimit }} {{ question.memoryLimit }} MB</span>
        </div>
      </div>
      <div class="tags">{{ question.tags }}</div>
      <div class="content-box">{{ question.content }}</div>

      <section class="sample-list" v-if="question.samples?.length">
        <h2>{{ text.sampleTitle }}</h2>
        <article v-for="(sample, index) in question.samples" :key="index" class="sample-card">
          <h3>{{ text.sampleLabel }} {{ index + 1 }}</h3>
          <div class="sample-block">
            <strong>{{ text.input }}</strong>
            <pre>{{ sample.input }}</pre>
          </div>
          <div class="sample-block">
            <strong>{{ text.output }}</strong>
            <pre>{{ sample.output }}</pre>
          </div>
        </article>
      </section>
    </article>

    <aside class="panel submit-card">
      <h2>{{ text.submitTitle }}</h2>
      <select v-model="submitForm.language">
        <option value="cpp">C++23</option>
        <option value="c">C</option>
        <option value="java">Java</option>
      </select>
      <CodeEditor
        v-model="submitForm.code"
        :language="submitForm.language"
        @submit="handleSubmit"
      />
      <p class="editor-tip">{{ text.shortcutTip }}</p>
      <button @click="handleSubmit" :disabled="loading">{{ loading ? text.submitting : text.submitButton }}</button>
      <p v-if="submitMessage" class="message">{{ submitMessage }}</p>
      <div v-if="latestResult" class="judge-result">
        <h3>{{ text.judgeTitle }}</h3>
        <p>{{ text.status }}{{ statusMap[latestResult.status] || latestResult.status }}</p>
        <p>{{ text.info }}{{ latestResult.judgeInfo }}</p>
        <p>{{ text.time }}{{ latestResult.timeUsed ?? "-" }} ms</p>
        <p>{{ text.memory }}{{ latestResult.memoryUsed ?? "-" }} MB</p>
        <template v-if="latestResult.failedInput">
          <p>{{ text.failedInput }}</p>
          <pre>{{ latestResult.failedInput }}</pre>
          <p>{{ text.expectedOutput }}</p>
          <pre>{{ latestResult.expectedOutput }}</pre>
          <p>{{ text.actualOutput }}</p>
          <pre>{{ latestResult.actualOutput }}</pre>
        </template>
      </div>

      <div class="custom-run">
        <h2>{{ text.customRunTitle }}</h2>
        <textarea v-model="customInput" rows="8" :placeholder="text.customInputPlaceholder"></textarea>
        <button @click="handleCustomRun" :disabled="runningCustom">{{ runningCustom ? text.running : text.runButton }}</button>
        <div v-if="customRunResult" class="judge-result">
          <h3>{{ text.runResultTitle }}</h3>
          <p>{{ text.info }}{{ customRunResult.message }}</p>
          <p>{{ text.output }}{{ customRunResult.output || "-" }}</p>
          <p>{{ text.time }}{{ customRunResult.timeUsed ?? "-" }} ms</p>
          <p>{{ text.memory }}{{ customRunResult.memoryUsed ?? "-" }} MB</p>
        </div>
      </div>
    </aside>
  </section>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { getQuestionDetail, getSubmissionDetail, runCode, submitCode } from "../api";
import CodeEditor from "../components/CodeEditor.vue";

const text = {
  timeLimit: "\u65f6\u95f4\u9650\u5236",
  memoryLimit: "\u5185\u5b58\u9650\u5236",
  sampleTitle: "\u6837\u4f8b",
  sampleLabel: "\u6837\u4f8b",
  input: "\u8f93\u5165\uff1a",
  output: "\u8f93\u51fa\uff1a",
  submitTitle: "\u5728\u7ebf\u63d0\u4ea4",
  shortcutTip: "\u5feb\u6377\u952e\uff1aCtrl + Enter \u63d0\u4ea4\u4ee3\u7801",
  submitting: "\u63d0\u4ea4\u4e2d...",
  submitButton: "\u63d0\u4ea4\u4ee3\u7801",
  judgeTitle: "\u8bc4\u5224\u7ed3\u679c",
  status: "\u72b6\u6001\uff1a",
  info: "\u4fe1\u606f\uff1a",
  time: "\u8017\u65f6\uff1a",
  memory: "\u5185\u5b58\uff1a",
  submitSuccess: "\u63d0\u4ea4\u6210\u529f\uff0c\u6b63\u5728\u7b49\u5f85\u8bc4\u5224\u7ed3\u679c",
  submitFailed: "\u63d0\u4ea4\u5931\u8d25",
  finished: "\u8bc4\u5224\u5b8c\u6210\uff1a",
  pollFailed: "\u83b7\u53d6\u8bc4\u5224\u7ed3\u679c\u5931\u8d25",
  customRunTitle: "\u81ea\u5b9a\u4e49\u8f93\u5165\u8bc4\u6d4b",
  customInputPlaceholder: "\u5728\u8fd9\u91cc\u8f93\u5165\u4f60\u60f3\u8981\u6d4b\u8bd5\u7684\u8f93\u5165\u6570\u636e",
  runButton: "\u8fd0\u884c\u81ea\u5b9a\u4e49\u8f93\u5165",
  running: "\u8fd0\u884c\u4e2d...",
  runResultTitle: "\u8fd0\u884c\u7ed3\u679c",
  failedInput: "\u51fa\u9519\u6837\u4f8b\u8f93\u5165\uff1a",
  expectedOutput: "\u671f\u671b\u8f93\u51fa\uff1a",
  actualOutput: "\u5b9e\u9645\u8f93\u51fa\uff1a"
};

const codeTemplates = {
  cpp: `#include <bits/stdc++.h>
using namespace std;

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  return 0;
}
`,
  c: `#include <stdio.h>

int main() {
  return 0;
}
`,
  java: `import java.util.*;

public class Main {
  public static void main(String[] args) {
  }
}
`
};

const route = useRoute();
const question = ref(null);
const loading = ref(false);
const submitMessage = ref("");
const latestResult = ref(null);
const customInput = ref("");
const customRunResult = ref(null);
const runningCustom = ref(false);
const submitForm = ref({
  questionId: Number(route.params.id),
  language: "cpp",
  code: codeTemplates.cpp
});
const statusMap = {
  0: "\u7b49\u5f85\u5224\u9898",
  1: "\u5224\u9898\u4e2d",
  2: "\u901a\u8fc7",
  3: "\u7b54\u6848\u9519\u8bef",
  4: "\u7f16\u8bd1\u9519\u8bef",
  5: "\u8fd0\u884c\u9519\u8bef",
  6: "\u8d85\u65f6"
};

let pollTimer = null;
let previousLanguage = "cpp";

function codeStorageKey(questionId, language) {
  return `oj:code:${questionId}:${language}`;
}

function persistCode() {
  localStorage.setItem(
    codeStorageKey(submitForm.value.questionId, submitForm.value.language),
    submitForm.value.code
  );
}

function restoreCode(language) {
  const savedCode = localStorage.getItem(codeStorageKey(submitForm.value.questionId, language));
  if (savedCode) {
    submitForm.value.code = savedCode;
    return true;
  }
  return false;
}

watch(
  () => submitForm.value.language,
  (language) => {
    const previousTemplate = codeTemplates[previousLanguage];
    if (!restoreCode(language)) {
      if (!submitForm.value.code.trim() || submitForm.value.code === previousTemplate) {
        submitForm.value.code = codeTemplates[language];
      }
    }
    previousLanguage = language;
  }
);

watch(
  () => submitForm.value.code,
  () => {
    persistCode();
  }
);

async function loadQuestion() {
  question.value = await getQuestionDetail(route.params.id);
  restoreCode(submitForm.value.language);
  if (!customInput.value && question.value?.samples?.length) {
    customInput.value = question.value.samples[0].input;
  }
}

async function handleSubmit() {
  if (loading.value) {
    return;
  }
  loading.value = true;
  submitMessage.value = "";
  latestResult.value = null;
  try {
    const submitId = await submitCode(submitForm.value);
    submitMessage.value = text.submitSuccess;
    startPolling(submitId);
  } catch (error) {
    submitMessage.value = error.message || text.submitFailed;
    loading.value = false;
  }
}

async function handleCustomRun() {
  if (runningCustom.value) {
    return;
  }
  runningCustom.value = true;
  try {
    customRunResult.value = await runCode({
      questionId: submitForm.value.questionId,
      language: submitForm.value.language,
      code: submitForm.value.code,
      input: customInput.value
    });
  } catch (error) {
    customRunResult.value = {
      message: error.message || text.submitFailed,
      output: "",
      timeUsed: 0,
      memoryUsed: 0
    };
  } finally {
    runningCustom.value = false;
  }
}

async function pollResult(submitId) {
  const result = await getSubmissionDetail(submitId);
  latestResult.value = result;
  if (result.status === 3 && result.failedInput) {
    customInput.value = result.failedInput;
  }
  if (![0, 1].includes(result.status)) {
    submitMessage.value = `${text.finished}${statusMap[result.status] || result.status}`;
    loading.value = false;
    stopPolling();
  }
}

function startPolling(submitId) {
  stopPolling();
  pollResult(submitId).catch(() => {
    submitMessage.value = text.pollFailed;
    loading.value = false;
  });
  pollTimer = window.setInterval(async () => {
    try {
      await pollResult(submitId);
    } catch {
      submitMessage.value = text.pollFailed;
      loading.value = false;
      stopPolling();
    }
  }, 2000);
}

function stopPolling() {
  if (pollTimer) {
    window.clearInterval(pollTimer);
    pollTimer = null;
  }
}

onMounted(loadQuestion);
onBeforeUnmount(stopPolling);
</script>
