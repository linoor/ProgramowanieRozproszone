#!/bin/bash

cd src/
# compile all of the files
javac *.java
# run the server
gnome-terminal --geometry=80x16+21+6 -t "Server" -x bash -c "java PMO_Server; bash" &
sleep 2
# run the debugger
gnome-terminal --geometry=80x28+629+4 -t "debugger" -x bash -c "java DebugClient; bash" &
# run the first player
gnome-terminal --geometry=80x16+701-97 -t "do nothing player" -x bash -c "java DoNothingPlayer something; bash" &
# run my player
gnome-terminal --geometry=80x16+16+446 -t "my player" -x bash -c "java Start myplayer; bash" &
