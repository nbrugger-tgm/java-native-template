package com.niton.gradle.plugin.natives;

import java.io.File;

public class NativeLib {
    public final String os,arch;
    public final File file;
    public final File parent;

    NativeLib(File f){
        this(f.parentFile.parentFile.name,f.parentFile.name,f)
    }
    public NativeLib(String os, String arch, File file) {
        this.os = os;
        this.arch = arch;
        this.file = file;
        parent = file.getParentFile();
    }

    public String getName(){
        return file.name.split("\\.")[0]
    }
}
