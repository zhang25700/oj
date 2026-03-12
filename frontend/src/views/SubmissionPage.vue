<template>
  <section class="panel">
    <div class="panel-header">
      <h1>我的提交</h1>
      <button @click="loadSubmissions">刷新</button>
    </div>
    <div class="table-shell">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>题目</th>
            <th>语言</th>
            <th>状态</th>
            <th>耗时</th>
            <th>内存</th>
            <th>结果</th>
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
  0: "等待判题",
  1: "判题中",
  2: "通过",
  3: "答案错误",
  4: "编译错误",
  5: "运行错误",
  6: "超时"
};

async function loadSubmissions() {
  const data = await getMySubmissions({ current: 1, pageSize: 20 });
  submissions.value = data.records || [];
}

onMounted(loadSubmissions);
</script>
