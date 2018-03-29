Following are the Valid commands from Project :

1. Show Databases;

|----------|
| Database |
|----------|
| catalog  |
|----------|
Query Successful


2. use catalog;

Database selected

3. show tables;

|-------------------|
| table_name        |
|-------------------|
| davisbase_tables  |
| davisbase_columns |
|-------------------|
Query Successful

4. select * from davisbase_columns;

|-------|---------------|-------------------|-----------------------|-----------|------------|------------------|-------------|
| rowid | database_name | table_name        | column_name           | data_type | column_key | ordinal_position | is_nullable |
|-------|---------------|-------------------|-----------------------|-----------|------------|------------------|-------------|
| 1     | catalog       | davisbase_tables  | rowid                 | INT       | NULL       | 1                | NO          |
| 2     | catalog       | davisbase_tables  | database_name         | TEXT      | NULL       | 2                | NO          |
| 3     | catalog       | davisbase_tables  | table_name            | TEXT      | NULL       | 3                | NO          |
| 4     | catalog       | davisbase_tables  | record_count          | INT       | NULL       | 4                | NO          |
| 5     | catalog       | davisbase_tables  | col_tbl_st_rowid      | INT       | NULL       | 5                | NO          |
| 6     | catalog       | davisbase_tables  | nxt_avl_col_tbl_rowid | INT       | NULL       | 6                | NO          |
| 7     | catalog       | davisbase_columns | rowid                 | INT       | NULL       | 1                | NO          |
| 8     | catalog       | davisbase_columns | database_name         | TEXT      | NULL       | 2                | NO          |
| 9     | catalog       | davisbase_columns | table_name            | TEXT      | NULL       | 3                | NO          |
| 10    | catalog       | davisbase_columns | column_name           | TEXT      | NULL       | 4                | NO          |
| 11    | catalog       | davisbase_columns | data_type             | TEXT      | NULL       | 5                | NO          |
| 12    | catalog       | davisbase_columns | column_key            | TEXT      | NULL       | 6                | NO          |
| 13    | catalog       | davisbase_columns | ordinal_position      | TINYINT   | NULL       | 7                | NO          |
| 14    | catalog       | davisbase_columns | is_nullable           | TEXT      | NULL       | 8                | NO          |
|-------|---------------|-------------------|-----------------------|-----------|------------|------------------|-------------|
Query Successful


5. create database demo;

Query Successful

6. use demo;

Database selected

7. create table test (ID int Primary Key, Name text, City text);

Query Successful

8. insert into test values (1, "Ankur", "Richardson");

Query Successful

9. Select * from test;

|----|-------|------------|
| id | name  | city       |
|----|-------|------------|
| 1  | ankur | richardson |
| 2  | manoj | richardson |
|----|-------|------------|
Query Successful

10. update test set city = "dallas" where id = 1;

Query Successful

select * from test;

|----|-------|------------|
| id | name  | city       |
|----|-------|------------|
| 1  | ankur | dallas     |
| 2  | manoj | richardson |
|----|-------|------------|
Query Successful

11. delete from test where id = 1;

Query Successful

select * from test;

|----|-------|------------|
| id | name  | city       |
|----|-------|------------|
| 2  | manoj | richardson |
|----|-------|------------|
Query Successful

12. Select name from test where id = 2;

|-------|
| name  |
|-------|
| manoj |
|-------|
Query Successful

13. Drop table test;
Query Successful

show tables;
Nothing to display

14. Drop database demo;

Query Successful

15. exit;
Exiting Database...