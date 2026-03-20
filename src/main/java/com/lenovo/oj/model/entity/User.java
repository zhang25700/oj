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
@TableName("oj_user")
/**
 * 用户实体。
 *
 * 保存账号、加密密码、角色、已解题数和最后通过时间等信息。
 */
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("userAccount")
    private String userAccount;

    @TableField("userName")
    private String userName;

    @TableField("userPassword")
    private String userPassword;

    @TableField("userRole")
    private String userRole;

    @TableField("solvedCount")
    private Integer solvedCount;

    @TableField("lastAcceptedTime")
    private LocalDateTime lastAcceptedTime;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;
}
