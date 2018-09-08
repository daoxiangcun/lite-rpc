package com.yhl.rpc.common.inf;

import java.util.List;

/**
 * Created by yuhongliang on 18-8-29.
 */
public interface IZKChildListener {
    /**
     * Will be called when there is child added/deleted.
     * @param parentPath the parent ZK node path;
     * @param currentChildren the node name list of current children.
     */
    void onChanged(String parentPath, List<String> currentChildren);
}
