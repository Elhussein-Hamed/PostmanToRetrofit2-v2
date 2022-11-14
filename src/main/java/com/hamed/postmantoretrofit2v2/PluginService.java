package com.hamed.postmantoretrofit2v2;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "PluginState",
        storages = @Storage("my-plugin-state.xml")
)
public class PluginService implements PersistentStateComponent<PluginState> {

    public PluginState pluginState = new PluginState();

    @Override
    public @Nullable PluginState getState() {
        return pluginState;
    }

    @Override
    public void loadState(@NotNull PluginState state) {
        pluginState = state;
    }

    public static PersistentStateComponent<PluginState> getInstance() {
        return ServiceManager.getService(PluginService.class);
    }
}
