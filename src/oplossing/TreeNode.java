package oplossing;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<E extends Comparable<E>> {
    private TreeNode<E> parent = null;
    private E key1;
    private E key2;
    private TreeNode<E> Child1;
    private TreeNode<E> Child2;
    private TreeNode<E> Child3;

    public TreeNode(TreeNode<E> parent,E key) {
        this.parent=parent;
        this.key1=key;
    }

    //getters and setters for every field
    public TreeNode<E> getParent() {
        return parent;
    }

    public void setParent(TreeNode<E> parent) {
        this.parent = parent;
    }
    public E getKey1() {
        return key1;
    }

    public void setKey1(E key1) {
        this.key1 = key1;
    }

    public E getKey2() {
        return key2;
    }

    public void setKey2(E key2) {
        this.key2 = key2;
    }

    public TreeNode<E> getChild1() {
        return Child1;
    }

    public void setChild1(TreeNode<E> child1) {
        Child1 = child1;
    }

    public TreeNode<E> getChild2() {
        return Child2;
    }

    public void setChild2(TreeNode<E> child2) {
        Child2 = child2;
    }

    public TreeNode<E> getChild3() {
        return Child3;
    }

    public void setChild3(TreeNode<E> child3) {
        Child3 = child3;
    }

    public boolean isEmpty(){
        return getKey1() == null && getKey2() == null;
    }

    public int size(){
        return key2==null?1:2;
    }

    public boolean isRoot(){
        return parent == null ;
    }

    public boolean isleaf() {
        if (size()==2){
            return getChild1() == null && getChild2() == null && getChild3() == null;
        } else {
            return getChild1() == null && getChild2() == null;
        }
    }

    public ArrayList<E> getAllKeys(){
        ArrayList<E> keys = new ArrayList<>(List.of(key1));
        if(key2!=null){
            keys.add(key2);
        }
        return keys;
    }

    public ArrayList<TreeNode<E>> getAllChildren(){
        ArrayList<TreeNode<E>> children = new ArrayList<>();
        if (Child1!=null){
            children.add(Child1);
        } if (Child2!=null){
            children.add(Child2);
        } if (Child3!=null){
            children.add(Child3);
        }
        return children;
    }
}
