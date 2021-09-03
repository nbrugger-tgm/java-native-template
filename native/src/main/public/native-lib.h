/*
 * This C++ source file was generated by the Gradle 'init' task.
 */
#ifndef NATIVE_LIB_H
#define NATIVE_LIB_H

#ifdef _WIN32
#define NATIVE_LIB_EXPORT_FUNC __declspec(dllexport)
#else
#define NATIVE_LIB_EXPORT_FUNC
#endif

#include <string>

namespace native_lib {
    class Greeter {
        public:
        std::string NATIVE_LIB_EXPORT_FUNC greeting();
    };
}

#endif
