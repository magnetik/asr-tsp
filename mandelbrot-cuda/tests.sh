#!/bin/bash
echo "Starting tests..."
for i in {1000..10000..1000}
do
    ./mandelGPU $i
done
