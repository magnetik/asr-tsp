src=$(wildcard *.c)
obj=$(src:.c=.o)
CFLAGS=-Wall -g -fopenmp
LDLIBS=-lm -fopenmp
PRG=tsp
VTCC=vtcc -vt:cc
CC= gcc

all: $(PRG)

$(PRG): $(obj)
	  $(CC) -o $@ $^ $(LDLIBS)
	 
clean:		   
		   rm -f $(obj) $(PRG) *~ *.z *.otf

