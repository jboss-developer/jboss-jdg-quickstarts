cmake_minimum_required (VERSION 2.6)
project (quickstart CXX)

if (CMAKE_VERSION VERSION_LESS "3.1")
  if (CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
    set (CMAKE_CXX_FLAGS "--std=gnu++11 ${CMAKE_CXX_FLAGS} -DHR_PROTO_EXPORT=")
  endif ()
else ()
  set (CMAKE_CXX_STANDARD 11)
    set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DHR_PROTO_EXPORT=")
endif ()
  
find_package(Protobuf REQUIRED)

file (GLOB proto_files "${CMAKE_CURRENT_SOURCE_DIR}/src/*.proto")

PROTOBUF_GENERATE_CPP(PROTO_SRCS PROTO_HDRS ${proto_files})

set (HOTROD_PACKAGE_DIR /home/rigazilla/hotrod)

include_directories(${CMAKE_CURRENT_BINARY_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)
include_directories(${PROTOBUF_INCLUDE_DIRS})
include_directories(${HOTROD_PACKAGE_DIR}/usr/include)

link_directories(${HOTROD_PACKAGE_DIR}/usr/lib)

add_executable(quickstart src/main.cpp ${PROTO_SRCS} ${PROTO_HDRS})
target_link_libraries(quickstart hotrod hotrod_protobuf ${PROTOBUF_LIBRARIES})
