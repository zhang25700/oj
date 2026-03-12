import axios from "axios";

const http = axios.create({
  baseURL: "http://localhost:8101",
  timeout: 10000
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem("oj_token");
  if (token) {
    config.headers.Authorization = token;
  }
  return config;
});

http.interceptors.response.use(
  (response) => {
    const payload = response.data;
    if (payload.code !== 0) {
      return Promise.reject(new Error(payload.message || "请求失败"));
    }
    return payload.data;
  },
  (error) => Promise.reject(error)
);

export default http;
