/*
 * This C++ source file was generated by the Gradle 'init' task.
 */

#include "native-lib.h"
#include <cassert>

int main() {
    native_lib::Greeter greeter;
    assert(greeter.greeting().compare("Hello, World!") == 0);
    return 0;
}
