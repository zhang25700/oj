import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../views/HomePage.vue";
import LoginPage from "../views/LoginPage.vue";
import QuestionDetailPage from "../views/QuestionDetailPage.vue";
import SubmissionPage from "../views/SubmissionPage.vue";
import RankingPage from "../views/RankingPage.vue";

const routes = [
  { path: "/", name: "home", component: HomePage },
  { path: "/login", name: "login", component: LoginPage },
  { path: "/question/:id", name: "question-detail", component: QuestionDetailPage, props: true },
  { path: "/submissions", name: "submissions", component: SubmissionPage },
  { path: "/ranking", name: "ranking", component: RankingPage }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
