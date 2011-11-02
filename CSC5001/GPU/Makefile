CC=gcc

CUDA_HOME=/usr/local/cuda
CUDAROOT=/usr/local/cuda
CUDASDKROOT=/usr/local/cuda/C
CUDANVIDIA=/usr/lib/nvidia
NVCC=$(CUDA_HOME)/bin/nvcc

NVCFLAGS=-I $(CUDASDKROOT)/common/inc -I $(CUDA_HOME)/include

CFLAGS=-L$(CUDASDKROOT)/lib -L$(CUDANVIDIA)

pi: pi.o
	$(NVCC) $(CFLAGS) -v  -o $@ $^ -lcutil_i386 

clean:
	rm -f *.o *.linkinfo pi

%.o: %.cu
	$(NVCC) $(NVCFLAGS) -v -c -o $@ $<

