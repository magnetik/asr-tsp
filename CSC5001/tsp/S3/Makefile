src=$(wildcard *.c)
obj=$(src:.c=.o)
CFLAGS=-Wall -g
LDLIBS=-lm
PRG=tsp
$(PRG): $(obj)
	  $(CC) -o $@ $^ $(LDLIBS)
clean:		   
		   rm -f $(obj) $(PRG) *~

