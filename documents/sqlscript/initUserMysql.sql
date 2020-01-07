
/**
 *this code by vsk
 *create 201610151510
 *lastModify 201610151510
 *任何人有字段修改需要在注释与本头部进行声明！
*/


CREATE USER 'bosspay'@'%' IDENTIFIED BY 'Y4yhl9tbf110'; 

CREATE DATABASE bosspay DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

grant all  on bosspay.* to 'bosspay'@'%' ;