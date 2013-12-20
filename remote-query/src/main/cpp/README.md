Compile and install hotrod-cpp:

    $ mkdir build_linux
    $ cd build_linux
    $ cmake -DCMAKE_INSTALL_PREFIX:PATH=/usr ..
    $ cmake --build .
    $ sudo cmake --build . --target install

Compile and run the quickstart:

    $ mkdir build_linux
    $ cd build_linux
    $ cmake ..
    $ cmake --build .
    $ ./quickstart
