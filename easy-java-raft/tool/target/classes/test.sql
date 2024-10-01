USE `easy-java-raft`;

SELECT * FROM kv_store WHERE `key` = "test"

SELECT * FROM log;
DELETE FROM log 
WHERE log_index >= 5;

INSERT INTO `log` (log_index,`log_term`, command) VALUES (5,3,"hfuioahef asdbcv")

SELECT * FROM log WHERE log_index=
            (SELECT MAX(index) FROM log);
        
SELECT MAX(log_index) FROM log;
