#!/bin/bash
if [ ! $1 ]
        then
                echo "Usage: `basename $0` <répertoire>"
                exit 1
fi
IFS=$'\n'
OK=0
KO=0
count=0
temp=0
echo "nbMachines nbVilles result_tim"
for i in {2..10..1}
do
    count=0
    temp=0
    
	for line in $(cat result-light.dat)
	do
	        nbVilles=`echo $line | awk '{print $3}'`
	        if [ $count -eq 0 ]; then
	            oldNbVilles=$nbVilles
	        fi
	        
	        seed=`echo $line | awk '{print $6}'`
	        goodresult=`echo $line | awk '{print $9}'`
	
	        result=`mpiexec -f hosts -n $i ../$1/tsp $nbVilles $seed`
	        result_min=`echo $result | awk '{print $3}'`
	        result_time=`echo $result | awk '{print $6}'`
	        
	        if [ "$result_min" = "$goodresult" ]; then
	            if [ "$oldNbVilles" = "$nbVilles" ]; then 
	                temp=$((temp+result_time))
	                count=$((count+1))
	            else
	                moy=$((temp/count))
	                echo "$i $oldNbVilles $moy"
	                temp=$result_time
	                count=1
	                oldNbVilles=$nbVilles
	            fi
	            
	            OK=$(($OK+1))
	        else
	            echo "mistake !!"
	            
	            KO=$(($KO+1))
	        fi
	done
	moy=$((temp/count))
    echo "$i $oldNbVilles $moy"

done
moy=$((temp/count))
echo "10 $oldNbVilles $moy"
TOTAL=$(($OK+$KO))
if [ "$OK" = "$TOTAL" ]; then
	echo "THERE IS A MISTAKE IN THE CODE, START ./RESULT.SH TO SEE WHERE"	
fi

