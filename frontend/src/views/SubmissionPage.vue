<template>
  <section class="panel">
    <div class="panel-header">
      <h1>&#25105;&#30340;&#25552;&#20132;</h1>
      <button @click="loadSubmissions">&#21047;&#26032;</button>
    </div>
    <div class="table-shell">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>&#39064;&#30446;</th>
            <th>&#35821;&#35328;</th>
            <th>&#29366;&#24577;</th>
            <th>&#32791;&#26102;</th>
            <th>&#20869;&#23384;</th>
            <th>&#32467;&#26524;</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in submissions" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.questionId }}</td>
            <td>{{ item.language }}</td>
            <td>{{ statusMap[item.status] || item.status }}</td>
            <td>{{ item.timeUsed || "-" }}</td>
            <td>{{ item.memoryUsed || "-" }}</td>
            <td>{{ item.judgeInfo }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { getMySubmissions } from "../api";

const submissions = ref([]);
const statusMap = {
  0: "\u7b49\u5f85\u5224\u9898",
  1: "\u5224\u9898\u4e2d",
  2: "\u901a\u8fc7",
  3: "\u7b54\u6848\u9519\u8bef",
  4: "\u7f16\u8bd1\u9519\u8bef",
  5: "\u8fd0\u884c\u9519\u8bef",
  6: "\u8d85\u65f6"
};

async function loadSubmissions() {
  const data = await getMySubmissions({ current: 1, pageSize: 20 });
  submissions.value = data.records || [];
}

onMounted(loadSubmissions);
</script>
