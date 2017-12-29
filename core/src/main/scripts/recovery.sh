#!/bin/sh

program=RecoveryMain

# 以下如有空值，注意以null代替；strcPath为表结构（sql结尾文件）；dataPath为表数据（xml结尾文件）；注意strcPath与dataPath必须同时有值或null
# directoryPath为文件目录，此项不为null时，表示导入此目录下所有表，并使得strcPath与dataPath无效

ip=localhost
port=3306
username=root
password=root
database=dbup
strcPath=null
dataPath=null
directoryPath=E:\DbBackupTool\backup\localhost-3306\dbup\20151221181254

java -Djava.ext.dirs=lib -cp . $program  $ip $port $username $password $database $strcPath $dataPath $directoryPath >> ./manual-recovery.log