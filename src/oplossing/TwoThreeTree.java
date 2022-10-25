package oplossing;

import opgave.SearchTree;
import java.util.Iterator;

public class TwoThreeTree<E extends Comparable<E>> implements SearchTree<E> {
    private TreeNode<E> root= null;

    //search a specific E in the tree and return its node, return null if not found
    public TreeNode<E> search(E o){
        if (root == null ){
            return null;
        }
        return searchFrom(root, o);
    }

    //extra method for search with starting point
    public TreeNode<E> searchFrom(TreeNode<E> start, E goal){
        if(start.getKey1()==goal || start.getKey2()==goal){
            return start;
        }
        if(start.getChild1()==null){
            return null;
        }else if(goal.compareTo(start.getKey1())<0){
            return searchFrom(start.getChild1(), goal);
        } else if ((start.getKey2()==null || goal.compareTo(start.getKey2())<0)){
            return searchFrom(start.getChild2(),goal);
        } else if (goal.compareTo(start.getKey2())>0){
            return searchFrom(start.getChild3(),goal);
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        if(isEmpty()){
            return 0;
        }
        return recsize(root);
    }

    public int recsize(TreeNode<E> node) {
        if (node.isleaf()){
            return node.size();
        }
        int childsize1=0;
        int childsize2=0;
        int childsize3=0;
        if (node.getChild1() !=null){
            childsize1 = recsize(node.getChild1());
        }

        if(node.getChild2() != null){
            childsize2 = recsize(node.getChild2());
        }

        if(node.getChild3() != null) {
            childsize3 = recsize(node.getChild3());
        }
        return childsize1+childsize2+childsize3;
    }

    @Override
    public boolean isEmpty() {
        return root==null;
    }

    @Override
    public boolean contains(E o) {
        return search(o)!=null;
    }

    @Override
    public boolean add(E o) {
        if( root==null){
            root = new TreeNode<>(null,o);
            return true;
        }
        if(! contains(o)){
            radd(o,root);
            return true;
        }
        return false;
    }

    //recursive helper function to add key to node
    private void radd(E o, TreeNode<E> node) {
        if(node.size()==2){
            if(o.compareTo(node.getKey1()) < 0 && node.getChild1() != null){
                radd(o,node.getChild1());
            } else if(o.compareTo(node.getKey2()) > 0 && node.getChild3() != null){
                radd(o,node.getChild3());
            } else if (node.getChild2() != null){
                radd(o,node.getChild2());
            } else {
                insert(o,node);
            }
        } else {
            if(o.compareTo(node.getKey1()) < 0 && node.getChild1() != null){
                radd(o,node.getChild1());
            } else if (node.getChild2() != null){
                radd(o,node.getChild2());
            } else {
                insert(o,node);
            }
        }
    }

    private void insert(E o, TreeNode<E> node) {
        if(node.size()==1){
            if (o.compareTo(node.getKey1())<0){
                node.setKey2(node.getKey1());
                node.setKey1(o);
            } else {
                node.setKey2(o);
            }
        } else {
            TreeNode<E> nroot;
            if(o.compareTo(node.getKey1())<0){
                nroot = new TreeNode<>(node.isRoot()?null:node.getParent(),node.getKey1());
                nroot.setChild1(new TreeNode<>(nroot,o));
                nroot.setChild2(new TreeNode<>(nroot,node.getKey2()));
                } else if (o.compareTo(node.getKey2())>0){
                nroot = new TreeNode<>(node.isRoot()?null:node.getParent(),node.getKey2());
                nroot.setChild2(new TreeNode<>(nroot,o));
                nroot.setChild1(new TreeNode<>(nroot,node.getKey1()));
            } else {
                nroot = new TreeNode<>(node.isRoot()?null:node.getParent(),o);
                nroot.setChild2(new TreeNode<>(nroot,node.getKey2()));
                nroot.setChild1(new TreeNode<>(nroot,node.getKey1()));
            }
            if(nroot.isRoot()){
                root=nroot;
            } else {
                balance(nroot);
            }
        }
    }

    private void balance(TreeNode<E> replaceRoot){
            if (replaceRoot.getParent().size()==1){
                if(replaceRoot.getKey1().compareTo(replaceRoot.getParent().getKey1())<0){
                    replaceRoot.getParent().setKey2(replaceRoot.getParent().getKey1());
                    replaceRoot.getParent().setKey1(replaceRoot.getKey1());
                    replaceRoot.getParent().setChild3(replaceRoot.getParent().getChild2());
                    replaceRoot.getParent().setChild1(replaceRoot.getChild1());
                    replaceRoot.getParent().setChild2(replaceRoot.getChild2());
                } else {
                    replaceRoot.getParent().setKey2(replaceRoot.getKey1());
                    replaceRoot.getParent().setChild2(replaceRoot.getChild1());
                    replaceRoot.getParent().setChild3(replaceRoot.getChild2());
                    replaceRoot.getChild1().setParent(replaceRoot.getParent());
                    replaceRoot.getChild2().setParent(replaceRoot.getParent());
                }
            } else {
                TreeNode<E> nroot;
                if(replaceRoot.getKey1().compareTo(replaceRoot.getParent().getKey1())<0){
                    nroot = new TreeNode<>(replaceRoot.getParent().isRoot()?null:replaceRoot.getParent().getParent(),replaceRoot.getParent().getKey1());
                    nroot.setChild1(replaceRoot);
                    TreeNode<E> rchild=new TreeNode<>(nroot,replaceRoot.getParent().getKey2());
                    nroot.setChild2(rchild);
                    rchild.setChild1(replaceRoot.getParent().getChild2());
                    rchild.setChild2(replaceRoot.getParent().getChild3());
                    replaceRoot.setParent(nroot);
                } else if (replaceRoot.getKey1().compareTo(replaceRoot.getParent().getKey2())>0){
                    nroot = new TreeNode<>(replaceRoot.getParent().isRoot()?null:replaceRoot.getParent().getParent(),replaceRoot.getParent().getKey2());
                    nroot.setChild2(replaceRoot);
                    TreeNode<E> lchild=new TreeNode<>(nroot,replaceRoot.getParent().getKey1());
                    nroot.setChild1(lchild);
                    lchild.setChild1(replaceRoot.getParent().getChild1());
                    lchild.setChild2(replaceRoot.getParent().getChild2());
                    replaceRoot.setParent(nroot);
                } else {
                    nroot = new TreeNode<>(replaceRoot.getParent().isRoot()?null:replaceRoot.getParent().getParent(),replaceRoot.getKey1());
                    TreeNode<E> rchild=new TreeNode<>(nroot,replaceRoot.getParent().getKey2());
                    TreeNode<E> lchild=new TreeNode<>(nroot,replaceRoot.getParent().getKey1());
                    nroot.setChild2(rchild);
                    nroot.setChild1(lchild);
                    lchild.setChild1(replaceRoot.getParent().getChild1());
                    lchild.setChild2(replaceRoot.getChild1());
                    rchild.setChild1(replaceRoot.getChild2());
                    rchild.setChild2(replaceRoot.getParent().getChild3());
                }
                if(nroot.isRoot()){
                    root=nroot;
                } else {
                    balance(nroot);
                }
            }
        }
    @Override
    public boolean remove(E e) {
        return false;
    }

    @Override
    public void clear() {
        root=null;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }
}
