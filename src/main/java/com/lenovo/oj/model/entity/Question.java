package com.lenovo.oj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("question")
/**
 * 题目实体。
 *
 * 对应数据库中的 question 表，保存题面、测试点、时空限制和统计信息。
 */
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String tags;

    private String answer;

    @TableField("judgeCases")
    private String judgeCases;

    @TableField("timeLimit")
    private Integer timeLimit;

    @TableField("memoryLimit")
    private Integer memoryLimit;

    private String difficulty;

    @TableField("submitCount")
    private Integer submitCount;

    @TableField("acceptedCount")
    private Integer acceptedCount;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;
}
