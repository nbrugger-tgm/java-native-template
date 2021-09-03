package com.vendor.project.main;

import com.vendor.project.core.ProjectLib;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        ProjectLib lib = new ProjectLib();
        System.out.println(lib.foo(10));
    }
}
