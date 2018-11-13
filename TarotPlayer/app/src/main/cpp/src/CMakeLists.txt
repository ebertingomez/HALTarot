cmake_minimum_required(VERSION 2.8)
set (CMAKE_CXX_STANDARD 14)
set (CMAKE_BUILD_TYPE "Debug")

project( reconnaissance )

file(
    GLOB_RECURSE
    SOURCE
    src/*
)


find_package( OpenCV REQUIRED )

include_directories( ${OpenCV_INCLUDE_DIRS} include/)

#add_library( reconnaissance STATIC ${SOURCE})

add_executable( reconnaissance main.cpp ${SOURCE})

#set_target_properties( reconnaissance PROPERTIES LINK_DEPEDS_NO_SHARED ON)
target_link_libraries( reconnaissance ${OpenCV_LIBS} "/usr/lib/x86_64-linux-gnu/libfann.a") 
