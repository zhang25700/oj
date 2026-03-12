CREATE TABLE IF NOT EXISTS oj_user
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    userAccount        VARCHAR(128)                           NOT NULL,
    userName           VARCHAR(128)                           NOT NULL,
    userPassword       VARCHAR(255)                           NOT NULL,
    userRole           VARCHAR(32)  DEFAULT 'user'            NOT NULL,
    solvedCount        INT          DEFAULT 0                 NOT NULL,
    lastAcceptedTime   DATETIME     NULL,
    createTime         DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime         DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    isDelete           TINYINT      DEFAULT 0                 NOT NULL,
    UNIQUE KEY uk_user_account (userAccount)
);

CREATE TABLE IF NOT EXISTS question
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    title              VARCHAR(255)                           NOT NULL,
    content            TEXT                                   NOT NULL,
    tags               VARCHAR(255)                           NOT NULL,
    answer             TEXT                                   NOT NULL,
    judgeCases         JSON                                   NOT NULL,
    timeLimit          INT                                    NOT NULL,
    memoryLimit        INT                                    NOT NULL,
    difficulty         VARCHAR(32)                            NOT NULL,
    submitCount        INT          DEFAULT 0                 NOT NULL,
    acceptedCount      INT          DEFAULT 0                 NOT NULL,
    createTime         DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime         DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    isDelete           TINYINT      DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS question_submit
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    questionId         BIGINT                                 NOT NULL,
    userId             BIGINT                                 NOT NULL,
    language           VARCHAR(32)                            NOT NULL,
    code               LONGTEXT                               NOT NULL,
    status             INT                                    NOT NULL,
    judgeInfo          VARCHAR(255)                           NULL,
    timeUsed           INT                                    NULL,
    memoryUsed         INT                                    NULL,
    createTime         DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime         DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    isDelete           TINYINT      DEFAULT 0                 NOT NULL,
    KEY idx_question_id (questionId),
    KEY idx_user_id (userId)
);
