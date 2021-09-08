package com.niton.gradle.plugin.natives

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.language.assembler.tasks.Assemble
import org.gradle.language.cpp.plugins.CppBasePlugin
import org.gradle.language.jvm.tasks.ProcessResources

abstract class NativePlugin implements Plugin<Project> {
    static final jnrJarError = "Cant package JNR libs into jar: JNR doesnt supports natives in Jar files so the libs cant be shipped"
    private JavaNativeConfig config;
    private Copy nativeCopyTask;
    @Override
    void apply(Project project) {
        project.plugins.apply("java")

        config = project.extensions.create("natives",JavaNativeConfig)
        config.dontInclude.set(false)
        config.mode.set(NativeMode.JNI)
        config.pathBuilderMap = project.objects.mapProperty(NativeMode.class,JavaNativeConfig.PathComposer.class)

        config.pathBuilderMap.set([
                (NativeMode.JNA) : (lib,p)->"$p.os-$p.arch",
                (NativeMode.JNI) : (lib,p)->"NATIVE/${p.arch}/${p.os}",
                (NativeMode.JNR) : (lib,p)->p.logger.error(jnrJarError)
        ])

        nativeCopyTask = project.tasks.register("copyNatives", Copy){
            into("$project.buildDir/natives")
        }.get()
        project.tasks.processResources.dependsOn(nativeCopyTask)
        project.dependencies.extensions.add("nativeLib", (Project p)->this.addNativeDependency(p,project))

        project.tasks.withType(Jar) {
            properties.put("jna.library.path","pwd")//TODO:
        }
    }


    static FileCollection findLibNatives(String projectName, Project p){
        Directory libDir = p.project(projectName).layout.buildDirectory.dir("lib").get()
        FileTree tree = libDir.asFileTree
        return p.files(tree.filter((File f) -> f.isFile()))
    }

    void addNativeDependency(Project nativeProject, Project addTo){
        addTo.evaluationDependsOn(nativeProject.path)
        if(nativeProject.plugins.withType(CppBasePlugin).size() == 0){
            addTo.println("Plugins:")
            addTo.println(nativeProject.plugins.toList())
            throw new StopActionException("Project ${nativeProject.name} seems not to be a C++ project")
        }
        def releaseTasks =
                nativeProject.tasks
                .withType(Assemble)
                .stream()
                //.filter(t->!t.path.contains("debug"))
                .toList()
        print("Tasks : ${nativeProject.tasks.toList()}")
        print("Assemble type : ${nativeProject.tasks.getByPath(":assemble")}")
        addTo.tasks.processResources.dependsOn(releaseTasks)
        print("Add native pre-builds ($nativeProject) $releaseTasks")
        for (File f : findLibNatives(nativeProject.path,addTo).files){
            print("ADD : $f")
            def lib = new NativeLib(f);
            addTo.tasks.withType(ProcessResources).forEach{
                def mode = config.mode.get();
                def map = config.pathBuilderMap.get()
                it.from(lib.parent) {
                    include(lib.name)
                    rename(lib.name,"${map[mode].getPath(lib,project)}/$lib.name")
                }
            }
            nativeCopyTask.from(lib.parent) {
                include(lib.name)
                rename(lib.name,"$lib.os-$lib.arch/$lib.name")
            }
        }
    }
}
