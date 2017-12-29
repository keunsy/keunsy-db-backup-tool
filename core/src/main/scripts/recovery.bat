@echo off
set program=RecoveryMain

rem �������п�ֵ��ע����null���棻strcPathΪ��ṹ��sql��β�ļ�����dataPathΪ�����ݣ�xml��β�ļ�����ע��strcPath��dataPath����ͬʱ��ֵ��null
rem directoryPathΪ�ļ�Ŀ¼�����Ϊnullʱ����ʾ�����Ŀ¼�����б���ʹ��strcPath��dataPath��Ч

set ip=localhost
set port=3306
set username=root
set password=root
set database=dbup
set strcPath=null
set dataPath=null
set directoryPath=E:\DbBackupTool\backup\localhost-3306\dbup\20151221181254

java -Djava.ext.dirs=lib -cp . %program%  %ip% %port% %username% %password% %database% %strcPath% %dataPath% %directoryPath% >> ./manual-recovery.log