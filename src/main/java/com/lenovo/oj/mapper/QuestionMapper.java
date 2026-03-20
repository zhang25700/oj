package com.lenovo.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lenovo.oj.model.entity.Question;

/**
 * 题目表 Mapper。
 *
 * 当前主要使用 MyBatis-Plus 提供的通用 CRUD 能力，因此不额外声明自定义 SQL。
 */
public interface QuestionMapper extends BaseMapper<Question> {
}
