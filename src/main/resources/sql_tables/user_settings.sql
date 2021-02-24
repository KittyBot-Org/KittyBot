CREATE TABLE IF NOT EXISTS user_settings(
  user_id BIGINT        PRIMARY KEY NOT NULL,
  level_card_color      INT NOT NULL,
  level_card_font_color INT NOT NULL
);