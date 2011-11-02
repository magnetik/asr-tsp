#!/bin/bash
SEED=48648
if [ ! $1 ]
	then
		echo "Usage: `basename $0` <répertoire>"
		exit 1
fi
for i in 7 11 15
do
	result=`../$1/tsp $i $SEED`
	echo $result
done