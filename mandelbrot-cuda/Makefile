CC=gcc

CUDA_HOME=/usr/local/cuda
CUDASDKROOT=$(CUDA_HOME)/C
CUDANVIDIA=/usr/lib/nvidia
NVCC=$(CUDA_HOME)/bin/nvcc

NVCFLAGS=-I $(CUDASDKROOT)/common/inc -I $(CUDA_HOME)/include

CFLAGS= -L $(CUDASDKROOT)/lib -L $(CUDANVIDIA)

LDLIBS=-lX11

all: mandelCPU mandelGPU

mandelCPU: mandelCPU.o
	$(NVCC) -o $@ $^ $(CFLAGS)  -lcutil_i386 -lrt $(LDLIBS)

mandelGPU: mandelGPU.o
	$(NVCC) -o $@ $^ $(CFLAGS)  -lcutil_i386 -lrt $(LDLIBS)

clean:
	rm -f *.o *.linkinfo mandelGPU mandelCPU

%.o: %.cu
	$(NVCC) $(NVCFLAGS) -c -o $@ $<

