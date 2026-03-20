package com.lenovo.oj.model.dto.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/**
 * 排行榜返回对象。
 */
public class RankVO {

    private Long userId;

    private String userName;

    private Long solvedCount;
}
