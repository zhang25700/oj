CREATE TABLE IF NOT EXISTS oj_user
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    userAccount      VARCHAR(128)                           NOT NULL,
    userName         VARCHAR(128)                           NOT NULL,
    userPassword     VARCHAR(255)                           NOT NULL,
    userRole         VARCHAR(32)  DEFAULT 'user'            NOT NULL,
    solvedCount      INT          DEFAULT 0                 NOT NULL,
    lastAcceptedTime DATETIME     NULL,
    createTime       DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime       DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    isDelete         TINYINT      DEFAULT 0                 NOT NULL,
    UNIQUE KEY uk_user_account (userAccount)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    title         VARCHAR(255)                           NOT NULL,
    content       TEXT                                   NOT NULL,
    tags          VARCHAR(255)                           NOT NULL,
    answer        TEXT                                   NOT NULL,
    judgeCases    JSON                                   NOT NULL,
    timeLimit     INT                                    NOT NULL,
    memoryLimit   INT                                    NOT NULL,
    difficulty    VARCHAR(32)                            NOT NULL,
    submitCount   INT          DEFAULT 0                 NOT NULL,
    acceptedCount INT          DEFAULT 0                 NOT NULL,
    createTime    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    isDelete      TINYINT      DEFAULT 0                 NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_submit
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    questionId  BIGINT                                 NOT NULL,
    userId      BIGINT                                 NOT NULL,
    language    VARCHAR(32)                            NOT NULL,
    code        LONGTEXT                               NOT NULL,
    status      INT                                    NOT NULL,
    judgeInfo   VARCHAR(255)                           NULL,
    timeUsed    INT                                    NULL,
    memoryUsed  INT                                    NULL,
    createTime  DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime  DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    isDelete    TINYINT      DEFAULT 0                 NOT NULL,
    KEY idx_question_id (questionId),
    KEY idx_user_id (userId)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO oj_user (id, userAccount, userName, userPassword, userRole, solvedCount, lastAcceptedTime, isDelete)
VALUES
    (1, 'admin', '管理员', '$2a$10$pgNqoB.edtH0bAeHkPaEMO3bYOUw9468aUEtd74kVUbXu.GjGblgy', 'admin', 2, NOW(), 0),
    (2, 'alice', 'Alice', '$2a$10$pgNqoB.edtH0bAeHkPaEMO3bYOUw9468aUEtd74kVUbXu.GjGblgy', 'user', 1, NOW(), 0),
    (3, 'bob', 'Bob', '$2a$10$pgNqoB.edtH0bAeHkPaEMO3bYOUw9468aUEtd74kVUbXu.GjGblgy', 'user', 0, NULL, 0)
ON DUPLICATE KEY UPDATE
    userName = VALUES(userName),
    userPassword = VALUES(userPassword),
    userRole = VALUES(userRole),
    solvedCount = VALUES(solvedCount),
    lastAcceptedTime = VALUES(lastAcceptedTime);

INSERT INTO question (id, title, content, tags, answer, judgeCases, timeLimit, memoryLimit, difficulty, submitCount, acceptedCount, isDelete)
VALUES
    (
        1,
        'A + B Problem',
        '输入两个整数 a 和 b，输出它们的和。',
        'math,simulation',
        '读取两个整数并输出 a + b。',
        JSON_ARRAY(
            JSON_OBJECT('input', '1 2', 'output', '3'),
            JSON_OBJECT('input', '5 7', 'output', '12')
        ),
        1000,
        128,
        'easy',
        10,
        8,
        0
    ),
    (
        2,
        '字符串反转',
        '输入一个字符串，输出反转后的字符串。',
        'string',
        '从后向前遍历字符串输出即可。',
        JSON_ARRAY(
            JSON_OBJECT('input', 'hello', 'output', 'olleh'),
            JSON_OBJECT('input', 'abcde', 'output', 'edcba')
        ),
        1000,
        128,
        'easy',
        6,
        4,
        0
    ),
    (
        3,
        '最长递增子序列',
        '给定一个整数序列，求其最长严格递增子序列长度。',
        'dp,binary-search',
        '可用 O(n log n) 的贪心加二分做法。',
        JSON_ARRAY(
            JSON_OBJECT('input', '6\n10 9 2 5 3 7', 'output', '3'),
            JSON_OBJECT('input', '8\n1 3 6 7 9 4 10 5', 'output', '6')
        ),
        2000,
        256,
        'medium',
        12,
        5,
        0
    )
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    content = VALUES(content),
    tags = VALUES(tags),
    answer = VALUES(answer),
    judgeCases = VALUES(judgeCases),
    timeLimit = VALUES(timeLimit),
    memoryLimit = VALUES(memoryLimit),
    difficulty = VALUES(difficulty),
    submitCount = VALUES(submitCount),
    acceptedCount = VALUES(acceptedCount);

INSERT INTO question_submit (id, questionId, userId, language, code, status, judgeInfo, timeUsed, memoryUsed, isDelete)
VALUES
    (1, 1, 1, 'java', 'public class Main { public static void main(String[] args) {} }', 2, 'Accepted', 32, 64, 0),
    (2, 2, 2, 'cpp', '#include <bits/stdc++.h>\nusing namespace std;\nint main() { return 0; }', 2, 'Accepted', 21, 48, 0),
    (3, 3, 3, 'java', 'public class Main { public static void main(String[] args) {} }', 3, '输出与预期不一致', 120, 80, 0)
ON DUPLICATE KEY UPDATE
    status = VALUES(status),
    judgeInfo = VALUES(judgeInfo),
    timeUsed = VALUES(timeUsed),
    memoryUsed = VALUES(memoryUsed);
