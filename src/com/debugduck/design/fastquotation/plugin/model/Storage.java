package com.debugduck.design.fastquotation.plugin.model;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.StoragePathMacros;
import org.jetbrains.annotations.Nullable;

/**
 * Created by rafal on 5.11.16.
 *
 */
@State(name="SaveAction",
        storages = {@com.intellij.openapi.components.Storage(
            file = StoragePathMacros.APP_CONFIG + "/saveactions_settings.xml")})
public class Storage implements PersistentStateComponent<Storage>{


    private boolean isFirstLaunch = true;

    @Nullable
    @Override
    public Storage getState() {
        return this;
    }

    @Override
    public void loadState(Storage storage) {
        isFirstLaunch = false;
//        XmlSerializ
    }


    public boolean isFirstLaunch(){
        return isFirstLaunch;
    }
}
