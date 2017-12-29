@echo off
set program=BackupMain

rem �������п�ֵ��ע����null���棻tablesΪ������������Ÿ�����directoryPathΪ�洢·��������null��ϵͳ����Ĭ��·��

set ip=localhost
set port=3306
set username=root
set password=root
set database=dbup
set tables=role,test
set directoryPath=null

java -Djava.ext.dirs=lib -cp . %program%  %ip% %port% %username% %password% %database% %tables% %directoryPath% >> ./manual-backup.log