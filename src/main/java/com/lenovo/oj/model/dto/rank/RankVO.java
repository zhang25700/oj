package com.lenovo.oj.model.dto.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankVO {

    private Long userId;

    private String userName;

    private Long solvedCount;
}
