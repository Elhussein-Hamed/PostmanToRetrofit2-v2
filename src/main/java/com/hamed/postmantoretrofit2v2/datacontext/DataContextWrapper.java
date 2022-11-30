package com.hamed.postmantoretrofit2v2.datacontext;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class DataContextWrapper implements DataContext {
    private final Map<String, Object> myDataId2Data;
    private final DataContext myParent;
    private final boolean myWithRules;
    private final DataProvider myDataProvider;

    private DataContextWrapper(String dataId, Object data, DataContext parent) {
        this(new HashMap<>(1), parent, false);
        myDataId2Data.put(dataId, data);
    }

    private DataContextWrapper(@NotNull Map<String, Object> dataId2data, DataContext parent, boolean withRules) {
        myDataId2Data = dataId2data;
        myParent = parent;
        myWithRules = withRules;
        myDataProvider = withRules ? dataId -> getDataFromSelfOrParent(dataId) : __ -> null;
    }

    @Override
    public Object getData(@NotNull String dataId) {
        Object result = getDataFromSelfOrParent(dataId);

        if (result == null && PlatformDataKeys.CONTEXT_COMPONENT.getName().equals(dataId)) {
            result = IdeFocusManager.getGlobalInstance().getFocusOwner();
        }

//        if (result == null && myWithRules) {
//            GetDataRule rule = ((DataManagerImpl) DataManager.getInstance()).getDataRule(dataId);
//            if (rule != null) {
//                return rule.getData(myDataProvider);
//            }
//        }

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
        return getContext(dataId2data, parent, false);
    }

    /**
     * Creates a simple data context which can apply data rules.
     */
    @NotNull
    public static DataContext getContext(@NotNull Map<String, Object> dataId2data, DataContext parent, boolean withRules) {
        return new DataContextWrapper(dataId2data, parent, withRules);
    }

    @NotNull
    public static DataContext getContext(String dataId, Object data) {
        return getContext(dataId, data, null);
    }

    @NotNull
    public static DataContext getProjectContext(Project project) {
        return getContext(CommonDataKeys.PROJECT.getName(), project);
    }
}
