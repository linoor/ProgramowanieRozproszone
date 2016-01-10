#!/bin/bash

cd src/
# compile all of the files
javac *.java
# run the server
xterm -T "Server" -e bash -c "java PMO_Server; bash" &
sleep 2
# run the debugger
xterm -T "debugger" -e bash -c "java DebugClient; bash" &
# run the first player
xterm -T "do nothing player" -e bash -c "java DoNothingPlayer something; bash" &
# run my player
xterm -T "my player" -e bash -c "java Start myplayer; bash" &
