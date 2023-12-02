package com.hamed.postmantoretrofit2v2.datacontext;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.wm.IdeFocusManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class DataContextWrapper implements DataContext {
    private final Map<String, Object> myDataId2Data;
    private final DataContext myParent;

    private DataContextWrapper(String dataId, Object data, DataContext parent) {
        this(new HashMap<>(1), parent);
        myDataId2Data.put(dataId, data);
    }

    private DataContextWrapper(@NotNull Map<String, Object> dataId2data, DataContext parent) {
        myDataId2Data = dataId2data;
        myParent = parent;
    }

    @Override
    public Object getData(@NotNull String dataId) {
        Object result = getDataFromSelfOrParent(dataId);

        if (result == null && PlatformDataKeys.CONTEXT_COMPONENT.getName().equals(dataId)) {
            result = IdeFocusManager.getGlobalInstance().getFocusOwner();
        }

        return result;
    }

    @Nullable
    private Object getDataFromSelfOrParent(String dataId) {
        return myDataId2Data.containsKey(dataId) ? myDataId2Data.get(dataId) :
                myParent == null ? null : myParent.getData(dataId);
    }

    @NotNull
    public static DataContext getContext(String dataId, Object data, DataContext parent) {
        return new DataContextWrapper(dataId, data, parent);
    }

    @NotNull
    public static DataContext getContext(@NotNull Map<String,Object> dataId2data, DataContext parent) {
        return new DataContextWrapper(dataId2data, parent);
    }
}
