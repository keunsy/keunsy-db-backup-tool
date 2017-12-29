#!/bin/sh

program="MainServer"

pid=$(ps -ef| grep "$program" | grep -v grep|awk '{print $2}')
datetime=$(date +'%Y %m %d %H:%M:%S')


if [ -f nohup.out ];then
    rm -f nohup.out
fi
touch nohup.out && chmod  o+r nohup.out

nohup java -Djava.ext.dirs=lib -cp . $program &>nohup.out &
echo "$datetime $program start" >> restart.log
