package org.noamichael.utils.se.tree;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michael
 */
public class BasicTreeNode<T> implements TreeNode<T> {

    private List<TreeNode<?>> children;
    private TreeNode<?> parent;
    private T data;

    public BasicTreeNode() {
        this.children = new ArrayList();
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public BasicTreeNode(TreeNode<?> parent, T data) {
        this();
        this.parent = parent;
        this.data = data;
        this.parent.getChildren().add(this);
    }

    @Override
    public void setChildren(List<TreeNode<?>> children) {
        this.children = children;
    }

    @Override
    public List<TreeNode<?>> getChildren() {
        return children;
    }

    @Override
    public void setParent(TreeNode<?> parent) {
        this.parent = parent;
    }

    @Override
    public TreeNode<?> getParent() {
        return parent;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }
}
