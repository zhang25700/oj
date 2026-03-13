SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

ALTER DATABASE oj CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

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
    (1, 'admin', CONVERT(0xe7aea1e79086e59198 USING utf8mb4), '$2a$10$pgNqoB.edtH0bAeHkPaEMO3bYOUw9468aUEtd74kVUbXu.GjGblgy', 'admin', 2, NOW(), 0),
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
        CONVERT(0xe4b8a4e695b0e4b98be5928c USING utf8mb4),
        CONVERT(0xe7bb99e5ae9ae4b8a4e4b8aae695b4e695b0206120e5928c2062efbc8ce8be93e587bae5ae83e4bbace79a84e5928ce38082 USING utf8mb4),
        'math,simulation',
        CONVERT(0xe8afbbe58f96e4b8a4e4b8aae695b4e695b0e5b9b6e8be93e587ba2061202b2062e38082 USING utf8mb4),
        JSON_ARRAY(
            JSON_OBJECT('input', '1 2', 'output', '3'),
            JSON_OBJECT('input', '5 7', 'output', '12'),
            JSON_OBJECT('input', '100 200', 'output', '300')
        ),
        1000,
        128,
        'easy',
        18,
        15,
        0
    ),
    (
        2,
        CONVERT(0xe5ad97e7aca6e4b8b2e58f8de8bdac USING utf8mb4),
        CONVERT(0xe8be93e585a5e4b880e4b8aae5ad97e7aca6e4b8b2efbc8ce8be93e587bae58f8de8bdace5908ee79a84e5ad97e7aca6e4b8b2e38082 USING utf8mb4),
        'string',
        CONVERT(0xe4bb8ee5908ee59091e5898de9818de58e86e5ad97e7aca6e4b8b2e58db3e58fafe38082 USING utf8mb4),
        JSON_ARRAY(
            JSON_OBJECT('input', 'hello', 'output', 'olleh'),
            JSON_OBJECT('input', 'abcde', 'output', 'edcba'),
            JSON_OBJECT('input', 'level', 'output', 'level')
        ),
        1000,
        128,
        'easy',
        11,
        8,
        0
    ),
    (
        3,
        CONVERT(0xe69c80e995bfe98092e5a29ee5ad90e5ba8fe58897 USING utf8mb4),
        CONVERT(0xe7bb99e5ae9ae4b880e4b8aae695b4e695b0e5ba8fe58897efbc8ce6b182e585b6e69c80e995bfe4b8a5e6a0bce98092e5a29ee5ad90e5ba8fe58897e995bfe5baa6e38082 USING utf8mb4),
        'dp,binary-search',
        CONVERT(0xe58fafe794a8204f286e206c6f67206e2920e79a84e8b4aae5bf83e58aa0e4ba8ce58886e5819ae6b395e38082 USING utf8mb4),
        JSON_ARRAY(
            JSON_OBJECT('input', '6\n10 9 2 5 3 7', 'output', '3'),
            JSON_OBJECT('input', '8\n1 3 6 7 9 4 10 5', 'output', '6')
        ),
        2000,
        256,
        'medium',
        16,
        7,
        0
    ),
    (
        4,
        CONVERT(0xe68bace58fb7e58cb9e9858d USING utf8mb4),
        CONVERT(0xe7bb99e5ae9ae4b880e4b8aae58faae58c85e590abe5b08fe68bace58fb7e79a84e5ad97e7aca6e4b8b2efbc8ce588a4e696ade5ae83e698afe590a6e698afe59088e6b395e68bace58fb7e5ba8fe58897e38082e59088e6b395e8be93e587ba20596573efbc8ce590a6e58899e8be93e587ba204e6fe38082 USING utf8mb4),
        'stack,string',
        CONVERT(0xe4bdbfe794a8e6a088e68896e8aea1e695b0e599a8e7bbb4e68aa4e68bace58fb7e58cb9e9858de8bf87e7a88be38082 USING utf8mb4),
        JSON_ARRAY(
            JSON_OBJECT('input', '(()())', 'output', 'Yes'),
            JSON_OBJECT('input', '(()', 'output', 'No'),
            JSON_OBJECT('input', '()()()', 'output', 'Yes')
        ),
        1000,
        128,
        'easy',
        9,
        6,
        0
    ),
    (
        5,
        CONVERT(0xe5898de7bc80e5928ce69fa5e8afa2 USING utf8mb4),
        CONVERT(0xe7bb99e5ae9ae995bfe5baa6e4b8ba206e20e79a84e695b0e7bb84e5928c207120e6aca1e58cbae997b4e69fa5e8afa2efbc8ce6af8fe6aca1e69fa5e8afa2e8be93e587bae58cbae997b4e58583e7b4a0e5928ce38082 USING utf8mb4),
        'prefix-sum,array',
        CONVERT(0xe58588e9a284e5a484e79086e5898de7bc80e5928ce695b0e7bb84efbc8ce5868de794a8207072656669785b725d202d207072656669785b6c202d20315d20e59b9ee7ad94e69fa5e8afa2e38082 USING utf8mb4),
        JSON_ARRAY(
            JSON_OBJECT('input', '5 3\n1 2 3 4 5\n1 3\n2 5\n3 3', 'output', '6\n14\n3'),
            JSON_OBJECT('input', '4 2\n2 2 2 2\n1 4\n2 3', 'output', '8\n4')
        ),
        2000,
        256,
        'medium',
        7,
        4,
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
    (3, 3, 3, 'java', 'public class Main { public static void main(String[] args) {} }', 3, 'Output mismatch', 120, 80, 0)
ON DUPLICATE KEY UPDATE
    status = VALUES(status),
    judgeInfo = VALUES(judgeInfo),
    timeUsed = VALUES(timeUsed),
    memoryUsed = VALUES(memoryUsed);
