#!/bin/bash
if [ ! $1 ]
        then
                echo "Usage: `basename $0` <répertoire>"
                exit 1
fi
IFS=$'\n'
OK=0
KO=0
for i in {2..10..1}
do
	for line in $(cat result.dat)  
	do  
		nbVilles=`echo $line | awk '{print $3}'`
		seed=`echo $line | awk '{print $6}'` 
		goodresult=`echo $line | awk '{print $9}'`
	
		result=`mpiexec -f hosts -n $i ../$1/tsp $nbVilles $seed`
		result_min=`echo $result | awk '{print $3}'`
		result_time=`echo $result | awk '{print $6}'`
		if [ "$result_min" = "$goodresult" ]; then
			echo "OK ($i machines NbVilles : $nbVilles ; Seed : $seed ; Result = $result_min ; Time : $result_time)"
			OK=$((OK+1))
		else
	        	echo "*KO* ($i machines ; NbVilles : $nbVilles ; Seed : $seed ; ResultAttendu = $goodresult ; ResultObtenu = $result_min ; Time : $result_time)"
			KO=$((KO+1))
		fi
	done
done
TOTAL=$((OK+KO))
echo "OK : $OK/$TOTAL"
echo "KO : $KO/$TOTAL"
