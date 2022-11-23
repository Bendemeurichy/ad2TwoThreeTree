package oplossing;

import opgave.SearchTree;

import java.util.ArrayList;
import java.util.Iterator;

public class general23Tree<E extends Comparable<E>> implements SearchTree<E> {
    protected TreeNode<E> root = null;
    protected int size=0;

    public TreeNode<E> search(E o) {
        if (root == null){
            return null;
        }
        return searchFrom(root, o);
    }

    //extra method for search with starting point
    public TreeNode<E> searchFrom(TreeNode<E> start, E goal) {
        if (start.getKey1().equals(goal)){
            return start;
        }
        if(start.size()==2 && start.getKey2().equals(goal)){
            return start;
        }
        if (start.isleaf()){
            return null;
        } else if (goal.compareTo(start.getKey1()) < 0){
            return start.getChild1()==null? null : searchFrom(start.getChild1(), goal);
        } else if ((start.getKey2() == null || goal.compareTo(start.getKey2()) < 0)){
            return start.getChild2()==null? null :searchFrom(start.getChild2(), goal);
        } else if (goal.compareTo(start.getKey2()) > 0){
            return start.getChild3()==null? null : searchFrom(start.getChild3(), goal);
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E o) {
        return false;
    }

    @Override
    public boolean add(E o) {
        if (root == null){
            size++;
            root = new TreeNode<>(null, o);
            return true;
        }
        if (search(o)==null){
            size++;
            radd(o, root);
            return true;
        }
        return false;
    }

    public void radd(E o,TreeNode<E> node){}

    @Override
    public boolean remove(E e) {
        return false;
    }

    public TreeNode<E> largestLchild(TreeNode<E> from,E toRemove) {
        if (from.isleaf()){
            return from;
        }
        TreeNode<E> node;
        if(toRemove.equals(from.getKey1())){
            node = from.getChild1();
        } else {
            node = from.getChild2();
        }
        while (!node.isleaf()) {
            node = node.size() == 2 ? node.getChild3() : node.getChild2();
        }
        if(node.size()==1){
            while(node.getChild2()!=null){
                node = node.getChild2();
            }
        } else {
            while(node.getChild3()!=null){
                node = node.getChild3();
            }
        }
        return node;
    }

    public TreeNode<E> smallestRchild(TreeNode<E> from, E toRemove){
        if (from.isleaf()){
            return from;
        }
        TreeNode<E> node;
        if(toRemove.equals(from.getKey1())){
            node = from.getChild2();
        } else {
            node = from.getChild3();
        }
        if(node.size()==1){
            while(node.getChild1()!=null){
                node = node.getChild1();
            }
        } else {
            while(node.getChild2()!=null){
                node = node.getChild2();
            }
        }
        return node;
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    //use "depth first search" to put elements in arraylist -> return iterator of arraylist
    public Iterator<E> iterator() {
        return dfs(root, new ArrayList<>()).iterator();
    }

    public ArrayList<E> dfs(TreeNode<E> n, ArrayList<E> keylist) {
        if (n.isleaf()){
            keylist.add(n.getKey1());
            if (n.getKey2() != null){
                keylist.add(n.getKey2());
            }
        } else {
            if (n.getChild1()!=null){
                dfs(n.getChild1(), keylist);
            }
            keylist.add(n.getKey1());
            if (n.getChild2()!=null){
                dfs(n.getChild2(), keylist);
            }
            if (n.size() == 2){
                keylist.add(n.getKey2());
                if (n.getChild3()!=null){
                    dfs(n.getChild3(), keylist);
                }
            }
        }
        return keylist;
    }

    public void removeChild(TreeNode<E> node){
        if(node.getParent().getChild1().equals(node)){
            node.getParent().setChild1(null);
        } else if(node.getParent().getChild2().equals(node)){
            node.getParent().setChild2(null);
        } else {
            node.getParent().setChild3(null);
        }
    }

}
