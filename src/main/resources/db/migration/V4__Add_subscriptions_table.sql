CREATE TABLE user_subscriptions(
    channel_id bigint NOT NULL REFERENCES usr,
    subscriber_id bigint NOT NULL REFERENCES usr,
    PRIMARY KEY (channel_id, subscriber_id)
);