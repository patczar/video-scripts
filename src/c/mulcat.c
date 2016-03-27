/*
(c) Patryk Czarnik
Distributed under MIT License. See LICENCE file in the root directory for details.
*/

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#define BUFSIZE 2048

void usage() {
}

int copy_desc_to_desc(int in_fd, int out_fd) {
	int size=0, now;
	char *buf = malloc(BUFSIZE);
	while((now = read(in_fd, buf, BUFSIZE)) > 0) {
		size += now;
		write(out_fd, buf, now);
	}
	return size;
}

int copy_file_to_desc(char* path, int out_fd) {
	int in_fd = open(path, O_RDONLY);
	if(in_fd == -1)
		return -1;
	return copy_desc_to_desc(in_fd, out_fd);
}

void do_copy(int n, char* tinputs, char** inputs, int out) {
	for(int i=0; i<n; i++) {
		if(tinputs[i] == 't') {
			write(out, inputs[i], strlen(inputs[i]));
		} else if (tinputs[i] == 'i') {
			copy_desc_to_desc(0, out);
		} else if (tinputs[i] == 'f') {
			copy_file_to_desc(inputs[i], out);
		} else if (tinputs[i] == 'w') {
			int time = atoi(inputs[i]);
			sleep(time);
		}
	}
}

int main(int argc, char **args) {
	if(argc <= 1) {
		usage();
		return 1;
	}

	char* output = NULL;
	char** inputs = (char**) malloc(sizeof(char*)*(argc));
	char* tinputs = (char*) malloc(sizeof(char)*(argc));
	int cinput = 0;

	for(int i=1; i<argc; i++) {
		if(args[i][0] == '-') {
			if(strcmp(args[i], "-o") == 0) {
				output = args[++i];
			} else if(strcmp(args[i], "-f") == 0) {
				tinputs[cinput] = 'f';
				inputs[cinput++] = args[++i];
			} else if(strcmp(args[i], "-i") == 0) {
				tinputs[cinput] = 'i';
			} else if(strcmp(args[i], "-w") == 0) {
				tinputs[cinput] = 'w';
				inputs[cinput++] = args[++i];
			}
		} else {
			tinputs[cinput] = 't';
			inputs[cinput++] = args[i];
		}		
	}
	
	// Output file descriptor. Defaults to the standard output.
	int out = 1;
	if(output != NULL) {
		out = open(output, O_WRONLY);
		if(out == -1) {
			fprintf(stderr, "Failed to open %s\n", out);
			return 1;
		}
	}
	
	do_copy(cinput, tinputs, inputs, out);
	
	close(out);
	free(tinputs);
	free(inputs);
}
