src=$(wildcard *.c)
obj=$(src:.c=.o)
CFLAGS= 
LDLIBS=-lm 
PRG=tsp
VTCC=vtcc -vt:cc
CC=$(VTCC) mpicc 

$(PRG): $(obj)
	  $(CC) -o $@ $^ $(LDLIBS)
clean:		   
		   rm -f $(obj) $(PRG) *~ *.z

