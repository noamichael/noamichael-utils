package org.noamichael.utils.se.tree;

import java.util.List;

/**
 *
 * @author michael
 * @param <T>
 */
public interface TreeNode<T> {

    public void setChildren(List<TreeNode<?>> children);

    public List<TreeNode<?>> getChildren();

    public void setParent(TreeNode<?> parent);

    public TreeNode<?> getParent();

    public void setData(T data);

    public T getData();
    
}
