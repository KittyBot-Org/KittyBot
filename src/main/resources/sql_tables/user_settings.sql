CREATE TABLE IF NOT EXISTS user_settings(
  user_id BIGINT              PRIMARY KEY NOT NULL,
  level_card_background_url   VARCHAR DEFAULT(NULL),
  level_card_background_color INT NOT NULL DEFAULT(3289650),
  level_card_primary_color    INT NOT NULL DEFAULT(6053866),
  level_card_border_color     INT NOT NULL DEFAULT(16711679),
  level_card_font_color       INT NOT NULL DEFAULT(16711679)
);