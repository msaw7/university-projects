.PHONY: clean

ma.o: ma.c ma.h
	gcc -o $@ -c $< -Wall -Wextra -Wno-implicit-fallthrough -std=gnu17 -fPIC -O2

memory_tests.o: memory_tests.c memory_tests.h
	gcc -o $@ -c $< -Wall -Wextra -Wno-implicit-fallthrough -std=gnu17 -fPIC -O2

libma.so: ma.o memory_tests.o
	gcc -o $@ -shared $^ -Wl,--wrap=malloc -Wl,--wrap=calloc -Wl,--wrap=realloc -Wl,--wrap=reallocarray -Wl,--wrap=free -Wl,--wrap=strdup -Wl,--wrap=strndup

clean:
	rm -f ma.o memory_tests.o libma.so
