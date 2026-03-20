package com.lenovo.oj.controller;

import com.lenovo.oj.common.BaseResponse;
import com.lenovo.oj.common.ResultUtils;
import com.lenovo.oj.model.dto.rank.RankVO;
import com.lenovo.oj.service.RankingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
/**
 * 排行榜接口。
 *
 * 当前榜单数据来自 Redis ZSet，适合高频读取 Top N 场景。
 */
public class RankController {

    private final RankingService rankingService;

    // 返回排行榜前 N 名，默认取前 10。
    @GetMapping("/top")
    public BaseResponse<List<RankVO>> topN(@RequestParam(defaultValue = "10") int limit) {
        return ResultUtils.success(rankingService.topN(limit));
    }
}
