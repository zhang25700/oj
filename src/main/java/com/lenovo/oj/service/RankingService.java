package com.lenovo.oj.service;

import com.lenovo.oj.model.dto.rank.RankVO;
import java.util.List;

public interface RankingService {

    void recordAccepted(Long userId, String userName, long solvedCount);

    List<RankVO> topN(int limit);
}
