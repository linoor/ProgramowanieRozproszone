#!/bin/bash

cd src/
# compile all of the files
javac *.java

pid_file="terminals.pid"
# clear the pid file
> $pid_file

# run the server
gnome-terminal --geometry=80x16+21+6 -t "Server" -x bash -c "java PMO_Server; bash" &
echo $! >> $pid_file
sleep 1
# run my player
gnome-terminal --geometry=80x16+16+446 -t "my player" -x bash -c "java Start myplayer; bash" &
echo $! >> $pid_file
sleep 1
# run the first player
gnome-terminal --geometry=80x16+701-97 -t "do nothing player" -x bash -c "java DoNothingPlayer something; bash" &
echo $! >> $pid_file
# run the debugger
gnome-terminal --geometry=80x28+629+4 -t "debugger" -x bash -c "java DebugClient; bash" &
echo $! >> $pid_file

read -p "Press any key to kill all the terminals..."
if [ -f $pid_file ]; then
	while read PID
	do
		kill "$PID"
	done < $pid_file
fi
