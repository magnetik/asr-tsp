#!/bin/bash
if [ ! $1 ]
        then
                echo "Usage: `basename $0` <répertoire>"
                exit 1
fi
IFS=$'\n'
OK=0
KO=0
for line in $(cat result.dat)  
do  
	nbVilles=`echo $line | awk '{print $3}'`
	seed=`echo $line | awk '{print $6}'` 
	goodresult=`echo $line | awk '{print $9}'`

	result=`../$1/tsp $nbVilles $seed`
	result_min=`echo $result | awk '{print $3}'`
	result_time=`echo $result | awk '{print $6}'`
	if [ "$result_min" = "$goodresult" ]; then
		echo "OK (NbVilles : $nbVilles ; Seed : $seed ; Result = $result_min ; Time : $result_time)"
		OK=$((OK+1))
	else
        	echo "*KO* (NbVilles : $nbVilles ; Seed : $seed ; ResultAttendu = $goodresult ; ResultObtenu = $result_min ; Time : $result_time)"
		KO=$((KO+1))
	fi
done
TOTAL=$((OK+KO))
echo "OK : $OK/$TOTAL"
echo "KO : $KO/$TOTAL"
