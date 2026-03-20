package com.lenovo.oj.service;

import com.lenovo.oj.model.dto.rank.RankVO;
import java.util.List;

public interface RankingService {

    /**
     * 记录一次 AC 后的排行榜变更。
     */
    void recordAccepted(Long userId, String userName, long solvedCount);

    /**
     * 查询排行榜前 N 名。
     */
    List<RankVO> topN(int limit);
}
