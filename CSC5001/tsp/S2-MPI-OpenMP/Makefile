src=$(wildcard *.c)
obj=$(src:.c=.o)
CFLAGS= -fopenmp
LDLIBS=-lm -fopenmp
PRG=tsp
VTCC=vtcc -vt:cc
CC= mpicc 

$(PRG): $(obj)
	  $(CC) -o $@ $^ $(LDLIBS)
clean:		   
		   rm -f $(obj) $(PRG) *~ *.z

