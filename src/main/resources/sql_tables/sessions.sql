CREATE TABLE IF NOT EXISTS sessions(
  session_id varchar(32) NOT NULL,
  user_id    varchar(18) NOT NULL,
PRIMARY KEY(session_id)
);
