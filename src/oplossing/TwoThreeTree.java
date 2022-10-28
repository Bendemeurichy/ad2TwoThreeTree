package oplossing;

import opgave.SearchTree;

import java.util.ArrayList;
import java.util.Iterator;

public class TwoThreeTree<E extends Comparable<E>> implements SearchTree<E> {
    private TreeNode<E> root = null;

    //search a specific E in the tree and return its node, return null if not found
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
            return searchFrom(start.getChild1(), goal);
        } else if ((start.getKey2() == null || goal.compareTo(start.getKey2()) < 0)){
            return searchFrom(start.getChild2(), goal);
        } else if (goal.compareTo(start.getKey2()) > 0){
            return searchFrom(start.getChild3(), goal);
        }
        return null;

    }

    @Override
    public int size() {
        if (isEmpty()){
            return 0;
        }
        return recsize(root);
    }

    public int recsize(TreeNode<E> node) {
        if (node.isleaf()){
            return node.size();
        }
        int childsize1 = 0;
        int childsize2 = 0;
        int childsize3 = 0;
        if (node.getChild1() != null){
            childsize1 = recsize(node.getChild1());
        }

        if (node.getChild2() != null){
            childsize2 = recsize(node.getChild2());
        }

        if (node.getChild3() != null){
            childsize3 = recsize(node.getChild3());
        }
        return childsize1 + childsize2 + childsize3;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E o) {
        return search(o) != null;
    }

    //handle recursive add function
    @Override
    public boolean add(E o) {
        if (root == null){
            root = new TreeNode<>(null, o);
            return true;
        }
        if (!contains(o)){
            radd(o, root);
            return true;
        }
        return false;
    }

    //recursive helper function to add key to node, finds correct leaf to add key to
    private void radd(E o, TreeNode<E> node) {
        if (node.size() == 2){
            if (o.compareTo(node.getKey1()) < 0 && node.getChild1() != null){
                radd(o, node.getChild1());
            } else if (o.compareTo(node.getKey2()) > 0 && node.getChild3() != null){
                radd(o, node.getChild3());
            } else if (node.getChild2() != null){
                radd(o, node.getChild2());
            } else {
                insert(o, node);
            }
        } else {
            if (o.compareTo(node.getKey1()) < 0 && node.getChild1() != null){
                radd(o, node.getChild1());
            } else if (node.getChild2() != null){
                radd(o, node.getChild2());
            } else {
                insert(o, node);
            }
        }
    }

    //make a new replacement tree at the correct leaf
    private void insert(E o, TreeNode<E> node) {
        if (node.size() == 1){
            if (o.compareTo(node.getKey1()) < 0){
                node.setKey2(node.getKey1());
                node.setKey1(o);
            } else {
                node.setKey2(o);
            }
        } else {
            TreeNode<E> nroot;
            if (o.compareTo(node.getKey1()) < 0){
                nroot = new TreeNode<>(node.isRoot() ? null : node.getParent(), node.getKey1());
                nroot.setChild1(new TreeNode<>(nroot, o));
                nroot.setChild2(new TreeNode<>(nroot, node.getKey2()));
            } else if (o.compareTo(node.getKey2()) > 0){
                nroot = new TreeNode<>(node.isRoot() ? null : node.getParent(), node.getKey2());
                nroot.setChild2(new TreeNode<>(nroot, o));
                nroot.setChild1(new TreeNode<>(nroot, node.getKey1()));
            } else {
                nroot = new TreeNode<>(node.isRoot() ? null : node.getParent(), o);
                nroot.setChild2(new TreeNode<>(nroot, node.getKey2()));
                nroot.setChild1(new TreeNode<>(nroot, node.getKey1()));
            }
            if (nroot.isRoot()){
                root = nroot;
            } else {
                balance(nroot);
            }
        }
    }

    //recursive balancing function, rebalance 2-3 tree untill parent of size 1 or new root, very compute heavy
    private void balance(TreeNode<E> replaceRoot) {
        if (replaceRoot.getParent().size() == 1){
            if (replaceRoot.getKey1().compareTo(replaceRoot.getParent().getKey1()) < 0){
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
                replaceRoot.getParent().getChild3().setParent(replaceRoot.getParent());
            }
        } else {
            TreeNode<E> nroot;
            if (replaceRoot.getKey1().compareTo(replaceRoot.getParent().getKey1()) < 0){
                nroot = new TreeNode<>(replaceRoot.getParent().isRoot() ? null : replaceRoot.getParent().getParent(), replaceRoot.getParent().getKey1());
                nroot.setChild1(replaceRoot);
                TreeNode<E> rchild = new TreeNode<>(nroot, replaceRoot.getParent().getKey2());
                nroot.setChild2(rchild);
                rchild.setChild1(replaceRoot.getParent().getChild2());
                rchild.setChild2(replaceRoot.getParent().getChild3());
                rchild.getChild1().setParent(rchild);
                rchild.getChild2().setParent(rchild);
                replaceRoot.setParent(nroot);
            } else if (replaceRoot.getKey1().compareTo(replaceRoot.getParent().getKey2()) > 0){
                nroot = new TreeNode<>(replaceRoot.getParent().isRoot() ? null : replaceRoot.getParent().getParent(), replaceRoot.getParent().getKey2());
                nroot.setChild2(replaceRoot);
                TreeNode<E> lchild = new TreeNode<>(nroot, replaceRoot.getParent().getKey1());
                nroot.setChild1(lchild);
                lchild.setChild1(replaceRoot.getParent().getChild1());
                lchild.setChild2(replaceRoot.getParent().getChild2());
                lchild.getChild1().setParent(lchild);
                lchild.getChild2().setParent(lchild);
                replaceRoot.setParent(nroot);
            } else {
                nroot = new TreeNode<>(replaceRoot.getParent().isRoot() ? null : replaceRoot.getParent().getParent(), replaceRoot.getKey1());
                TreeNode<E> rchild = new TreeNode<>(nroot, replaceRoot.getParent().getKey2());
                TreeNode<E> lchild = new TreeNode<>(nroot, replaceRoot.getParent().getKey1());
                nroot.setChild2(rchild);
                nroot.setChild1(lchild);
                lchild.setChild1(replaceRoot.getParent().getChild1());
                lchild.setChild2(replaceRoot.getChild1());
                rchild.setChild1(replaceRoot.getChild2());
                rchild.setChild2(replaceRoot.getParent().getChild3());
                replaceRoot.setParent(nroot);
            }
            if (nroot.isRoot()){
                root = nroot;
            } else {
                balance(nroot);
            }
        }
    }

    @Override
    public boolean remove(E e) {
        if (contains(e)){
            TreeNode<E> node = search(e);
            TreeNode<E> largestLchild = largestLchild(node);
            if (largestLchild.size() == 2){
                if (e.equals(node.getKey1())){
                    node.setKey1(largestLchild.getKey2());
                } else {
                    node.setKey2(largestLchild.getKey1());
                }
                largestLchild.setKey2(null);
            } else {
                if (node.isRoot()){
                    root = null;
                } else {
                    if (largestLchild.getParent().getChild1() == largestLchild){
                        largestLchild.getParent().setChild1(null);
                    } else if (largestLchild.getParent().getChild2() == largestLchild){
                        largestLchild.getParent().setChild2(null);
                    } else if (largestLchild.getParent().size() == 2){
                        largestLchild.getParent().setChild3(null);
                    }
                    delbalance(largestLchild.getParent());
                }
            }
            return true;
        }
        return false;
    }

    private void delbalance(TreeNode<E> parent) {
        if (subtreesize(parent) == 6){
            if (parent.getChild1() == null || parent.getChild1().isEmpty()){
                parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
                parent.setKey1(parent.getChild2().getKey1());
                parent.getChild2().setKey1(parent.getChild2().getKey2());
                parent.getChild2().setKey2(null);

            } else if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                parent.setChild2(new TreeNode<>(parent, parent.getKey1()));
                parent.setKey1(parent.getChild1().getKey2());
                parent.getChild1().setKey2(null);
            } else {
                parent.setChild3(new TreeNode<>(parent, parent.getKey2()));
                parent.setKey2(parent.getChild2().getKey2());
                parent.getChild2().setKey2(null);
            }
        } else if (subtreesize(parent) == 5){
            if (parent.getChild2() != null && parent.getChild2().size() == 2){
                if (parent.getChild1() == null || parent.getChild1().isEmpty()){
                    parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
                    parent.setKey2(parent.getChild2().getKey1());
                    parent.getChild2().setKey1(parent.getChild2().getKey2());
                    parent.getChild2().setKey2(null);
                } else {
                    parent.setChild3(new TreeNode<>(parent, parent.getKey2()));
                    parent.setKey2(parent.getChild2().getKey2());
                    parent.getChild2().setKey2(null);
                }
            } else { //TODO implement 5tree with 1 move  (g2 in notebook)
                if (parent.getChild1() != null && parent.getChild1().size() == 2){
                    if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                        parent.setChild2(new TreeNode<>(parent, parent.getKey2()));
                        parent.getChild2().setKey2(parent.getChild3().getKey1());
                        parent.setKey2(null);
                        parent.setChild3(null);
                    } else {
                        parent.getChild2().setKey2(parent.getKey2());
                        parent.setKey2(null);
                    }
                } else {
                    if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                        parent.getChild1().setKey2(parent.getKey1());
                        parent.setKey1(parent.getKey2());
                        parent.setChild2(parent.getChild3());
                        parent.setChild3(null);
                    } else {
                        parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
                        parent.getChild1().setKey2(parent.getChild2().getKey1());
                        parent.setKey1(parent.getKey2());
                        parent.setChild2(parent.getChild3());
                        parent.setChild3(null);
                        parent.setKey2(null);
                    }
                }
            }
        } else if (subtreesize(parent) == 4){ //TODO: implement subtrees 4-2(recursion for 2tree)
            if (parent.getChild3() == null || parent.getChild3().isEmpty()){
                parent.getChild2().setKey2(parent.getKey2());
                parent.setKey2(null);
            } else if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                parent.getChild1().setKey2(parent.getKey1());
                parent.setKey1(parent.getKey2());
                parent.setChild2(parent.getChild3());
                parent.setChild3(null);
            } else {
                parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
                parent.getChild1().setKey2(parent.getChild2().getKey1());
                parent.setChild2(parent.getChild3());
                parent.setKey1(parent.getKey2());
                parent.setKey2(null);
                parent.setChild3(null);
            }
        } else if (subtreesize(parent) == 3){
            if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                if (parent.getChild2().isEmpty()){
                    parent.getChild2().setKey1(parent.getKey1());
                    parent.getChild2().setChild1(parent.getChild1().getChild3());
                    parent.getChild1().setChild3(null);
                } else {
                    parent.setChild2(new TreeNode<>(parent, parent.getKey1()));
                }
                parent.setKey1(parent.getChild1().getKey2());
                parent.getChild1().setKey2(null);
            } else {
                if (parent.getChild1() != null){
                    parent.getChild1().setKey1(parent.getKey1());
                    parent.getChild1().setChild2(parent.getChild2().getChild1());
                    if (parent.getChild1().getChild2() != null){
                        parent.getChild1().getChild2().setParent(parent.getChild1());
                    }
                    parent.getChild2().setChild1(null);
                } else {
                    parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
                }
                parent.setKey1(parent.getChild2().getKey1());
                parent.getChild2().setKey1(parent.getChild2().getKey2());
                parent.getChild2().setKey2(null);
                parent.getChild2().setChild1(parent.getChild2().getChild2());
                parent.getChild2().setChild2(parent.getChild2().getChild3());
                parent.getChild2().setChild3(null);
            }
        } else { //subtree is size 2 --> problem because size of balanced tree is smaller
            if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                parent.getChild1().setKey2(parent.getKey1());
                parent.setKey1(null);
            } else {
                parent.getChild2().setKey2(parent.getChild2().getKey1());
                parent.getChild2().setKey1(parent.getKey1());
                if (parent.getChild1() != null){
                    parent.getChild1().setKey1(parent.getChild2().getKey1());
                    parent.getChild1().setKey2(parent.getChild2().getKey2());
                } else {
                    parent.setChild1(parent.getChild2());
                }
                parent.getChild1().setChild2(parent.getChild2().getChild1());
                parent.getChild1().setChild3(parent.getChild2().getChild2());
                if (parent.getChild1().getChild2() != null){
                    parent.getChild1().getChild2().setParent(parent.getChild1());
                }
                if (parent.getChild1().getChild3() != null){
                    parent.getChild1().getChild3().setParent(parent.getChild1());
                }
                parent.setChild2(null);
                parent.setKey1(null);

            }
            if (parent.isRoot()){
                root = parent.getChild1() == null ? parent.getChild2() : parent.getChild1();
                root.setParent(null);
            } else {
                delbalance(parent.getParent());
            }
        }
    }

    private int subtreesize(TreeNode<E> root) {
        return root.size() + ((root.getChild1() == null || root.getChild1().isEmpty()) ? 0 : root.getChild1().size()) +
                ((root.getChild2() == null || root.getChild2().isEmpty()) ? 0 : root.getChild2().size()) + ((root.getChild3() == null || root.getChild3().isEmpty()) ? 0 : root.getChild3().size());
    }

    public TreeNode<E> largestLchild(TreeNode<E> from) {
        if (from.isleaf()){
            return from;
        }
        TreeNode<E> node = from.getChild1();
        while (!node.isleaf()) {
            node = node.size() == 2 ? node.getChild3() : node.getChild2();
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
            dfs(n.getChild1(), keylist);
            keylist.add(n.getKey1());
            dfs(n.getChild2(), keylist);
            if (n.size() == 2){
                keylist.add(n.getKey2());
                dfs(n.getChild3(), keylist);
            }
        }
        return keylist;
    }
}
