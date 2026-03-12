package com.lenovo.oj.service.impl;

import com.lenovo.oj.constant.RedisConstant;
import com.lenovo.oj.model.dto.rank.RankVO;
import com.lenovo.oj.service.RankingService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private static final double SCORE_BASE = 10_000_000_000_000D;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void recordAccepted(Long userId, String userName, long solvedCount) {
        String member = userId + ":" + userName;
        double score = solvedCount * SCORE_BASE - System.currentTimeMillis();
        stringRedisTemplate.opsForZSet().add(RedisConstant.RANKING_KEY, member, score);
    }

    @Override
    public List<RankVO> topN(int limit) {
        Set<ZSetOperations.TypedTuple<String>> tuples =
                stringRedisTemplate.opsForZSet().reverseRangeWithScores(RedisConstant.RANKING_KEY, 0, limit - 1);
        List<RankVO> result = new ArrayList<>();
        if (tuples == null) {
            return result;
        }
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            if (tuple.getValue() == null || tuple.getScore() == null) {
                continue;
            }
            String[] parts = tuple.getValue().split(":", 2);
            long solvedCount = (long) Math.floor(tuple.getScore() / SCORE_BASE);
            result.add(new RankVO(Long.parseLong(parts[0]), parts.length > 1 ? parts[1] : "unknown", solvedCount));
        }
        return result;
    }
}
