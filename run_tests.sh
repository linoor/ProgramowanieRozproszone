#!/bin/bash

cp src_compiled/* oramus_tests/
cd oramus_tests/
javac PMO_StartTest.java

# duplicate &1(stdout) to 5
for i in {1..1}
do	
	exec 5>&1
	output=$(time java PMO_StartTest | tee /dev/fd/5)

	echo -e "\n\n"
	echo -e "Errors:\n"
	echo "$output" | grep Blad
done

source ~/.bashrc
jobsdone;
