package com.lenovo.oj.constant;

public final class RedisConstant {

    /**
     * 登录 token -> userId 映射前缀。
     */
    public static final String LOGIN_USER_PREFIX = "oj:user:token:";
    /**
     * 热门题目详情缓存 key 前缀。
     */
    public static final String HOT_QUESTION_PREFIX = "oj:question:hot:";
    /**
     * 实时排行榜 ZSet key。
     */
    public static final String RANKING_KEY = "oj:ranking:passed";

    private RedisConstant() {
    }
}
