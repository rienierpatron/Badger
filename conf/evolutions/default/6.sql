# --- !Ups

CREATE TABLE comments (
	comment_id serial PRIMARY KEY,
  user_id int NOT NULL REFERENCES users,
  leaf_id int NOT NULL REFERENCES leafs,
  comment_content text NOT NULL,
  comment_timestamp timestamp NOT NULL DEFAULT NOW()
);;

INSERT INTO comments (comment_id, user_id, leaf_id, comment_content, comment_timestamp) VALUES
	(DEFAULT, 1, 5, '1 5 1', DEFAULT),
	(DEFAULT, 1, 6, '1 6 2', DEFAULT),
	(DEFAULT, 1, 7, '1 7 2', DEFAULT),
	(DEFAULT, 2, 8, '2 8 5', DEFAULT),
	(DEFAULT, 2, 1, '2 1 4', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT),
	(DEFAULT, 3, 8, '3 8 3', DEFAULT)
;;
# --- !Downs

DROP TABLE IF EXISTS comments;;
