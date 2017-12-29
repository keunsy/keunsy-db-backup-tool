#!/bin/sh

program=BackupMain

#以下如有空值，注意以null代替；tables为表名，多个逗号隔开；directoryPath为存储路径，输入null则系统生成默认路径

ip=localhost
port=3306
username=root
password=root
database=dbup
tables=role,test
directoryPath=null

java -Djava.ext.dirs=lib -cp . $program  $ip $port $username $password $database $tables $directoryPath >> ./manual-backup.log
