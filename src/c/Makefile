#!/bin/bash

# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

CC=gcc
COPTS="-std=c99"

all : mulcat

mulcat : mulcat.c
	$(CC) $(COPTS) -o mulcat mulcat.c

clean :
	rm -f mulcat *.o
