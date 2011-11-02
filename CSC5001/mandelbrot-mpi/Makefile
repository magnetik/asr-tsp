#.SILENT:
#Usage : make MAXITER=100 -pour lancer l'exécution de 100 itérations du programme mandelbrot_seq

MAXITER=10

BINROOT=mandelbrot

MPICC=mpicc
CC=gcc
DFLAGS=
LAUNCHER=
CFLAGS=-fopenmp
LDFLAGS=
LDLIBS=-lX11
BIN=$(BINROOT)_mpi $(BINROOT)_mpi_c 
RUN=$(LAUNCHER) $(BINROOT)_seq  $(MAXITER)

all : $(BIN)

%_mpi : %_mpi.c 
	$(MPICC) $(CFLAGS) $? -o $@ $(DFLAGS) $(LDFLAGS) $(LDLIBS)
	
%_mpi_c : %_mpi_c.c 
	$(MPICC) $(CFLAGS) $? -o $@ $(DFLAGS) $(LDFLAGS) $(LDLIBS)
	
%_seq: %_seq.c 
	$(CC) $(CFLAGS) $? -o $@ $(DFLAGS) $(LDFLAGS) $(LDLIBS)

run : $(BIN)
	/usr/bin/time -a -f "elapsedTime(in seconds)=%e:userTime(in seconds)=%U:systemTime(in seconds)=%S:maxProcessSize(in Kbytes)=%M:nbPageFaults=%F:nbSwap=%W:err(=0 if OK)=%x" $(RUN)

clean :
	rm -rf *~ $(BIN) $(OBJ)


