cmake_minimum_required (VERSION 2.6)
project (quickstart)

find_package(Protobuf REQUIRED)

file (GLOB cpp_files "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp")
file (GLOB proto_files "${CMAKE_CURRENT_SOURCE_DIR}/src/*.proto")

PROTOBUF_GENERATE_CPP(PROTO_SRCS PROTO_HDRS ${proto_files})

include_directories(${PROTOBUF_INCLUDE_DIRS})
include_directories(${CMAKE_CURRENT_BINARY_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

add_executable(quickstart ${cpp_files} ${PROTO_SRCS} ${PROTO_HDRS})
target_link_libraries(quickstart hotrod ${PROTOBUF_LIBRARIES})
