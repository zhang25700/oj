package com.lenovo.oj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lenovo.oj.common.ErrorCode;
import com.lenovo.oj.constant.RedisConstant;
import com.lenovo.oj.exception.BusinessException;
import com.lenovo.oj.mapper.QuestionMapper;
import com.lenovo.oj.model.dto.question.QuestionAddRequest;
import com.lenovo.oj.model.dto.question.QuestionQueryRequest;
import com.lenovo.oj.model.entity.Question;
import com.lenovo.oj.model.vo.QuestionVO;
import com.lenovo.oj.service.QuestionService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addQuestion(QuestionAddRequest request) {
        Question question = BeanUtil.copyProperties(request, Question.class);
        question.setSubmitCount(0);
        question.setAcceptedCount(0);
        save(question);
        return question.getId();
    }

    @Override
    public IPage<QuestionVO> pageQuestions(QuestionQueryRequest request) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasText(request.getKeyword()),
                        query -> query.like(Question::getTitle, request.getKeyword())
                                .or()
                                .like(Question::getContent, request.getKeyword()))
                .eq(StringUtils.hasText(request.getDifficulty()), Question::getDifficulty, request.getDifficulty())
                .like(StringUtils.hasText(request.getTag()), Question::getTags, request.getTag())
                .orderByDesc(Question::getCreateTime);
        Page<Question> page = page(new Page<>(request.getCurrent(), request.getPageSize()), wrapper);
        Page<QuestionVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<QuestionVO> records = page.getRecords().stream().map(QuestionVO::fromEntity).toList();
        result.setRecords(records);
        return result;
    }

    @Override
    public QuestionVO getQuestionDetail(Long id) {
        String cacheKey = RedisConstant.HOT_QUESTION_PREFIX + id;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return JSONUtil.toBean(cached, QuestionVO.class);
        }
        Question question = getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Problem not found");
        }
        QuestionVO vo = QuestionVO.fromEntity(question);
        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(vo), 10, TimeUnit.MINUTES);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseSubmitCount(Long questionId, boolean accepted) {
        Question question = getById(questionId);
        if (question == null) {
            return;
        }
        question.setSubmitCount((question.getSubmitCount() == null ? 0 : question.getSubmitCount()) + 1);
        if (accepted) {
            question.setAcceptedCount((question.getAcceptedCount() == null ? 0 : question.getAcceptedCount()) + 1);
        }
        updateById(question);
        stringRedisTemplate.delete(RedisConstant.HOT_QUESTION_PREFIX + questionId);
    }
}
