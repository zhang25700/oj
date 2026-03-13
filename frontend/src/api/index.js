import http from "./http";

export const login = (data) => http.post("/user/login", data);
export const register = (data) => http.post("/user/register", data);
export const getCurrentUser = () => http.get("/user/me");
export const pageQuestions = (data) => http.post("/question/page", data);
export const getQuestionDetail = (id) => http.get(`/question/${id}`);
export const submitCode = (data) => http.post("/submit", data);
export const runCode = (data) => http.post("/submit/run", data);
export const getMySubmissions = (params) => http.get("/submit/my/page", { params });
export const getSubmissionDetail = (id) => http.get(`/submit/${id}`);
export const getRanking = (params) => http.get("/rank/top", { params });
