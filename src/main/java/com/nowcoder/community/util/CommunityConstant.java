package com.nowcoder.community.util;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 1;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT  = 2;
    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 0;

    /**
     * 默认的登录凭证超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 勾选记住我后的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 7;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型: 用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * Kafka主题：评论
     */

    String TOPIC_COMMENT = "comment";

    /**
     * Kafka主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * Kafka主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 系统用户Id
     */
    int SYSTEM_USER_ID = 1;
}
