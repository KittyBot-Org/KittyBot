CREATE TABLE IF NOT EXISTS commands(
  command         varchar(18) NOT NULL,
  processing_time interval NOT NULL,
  time            timestamp NOT NULL
);
