package com.niton.gradle.plugin.natives

import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface JavaNativeConfig {
    Property<NativeMode> getMode()
    Property<Boolean> getDontInclude()
    MapProperty<NativeMode,PathComposer> getPathBuilderMap();
    interface PathComposer{
        public String getPath(NativeLib lib, Project project)
    }
}
