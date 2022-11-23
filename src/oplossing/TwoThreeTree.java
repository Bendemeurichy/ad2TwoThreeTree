package oplossing;

public class TwoThreeTree<E extends Comparable<E>> extends general23Tree<E> {
    @Override
    public boolean contains(E o) {
        return search(o) != null;
    }

    //recursive helper function to add key to node, finds correct leaf to add key to
    public void radd(E o, TreeNode<E> node) {
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
    public void insert(E o, TreeNode<E> node) {
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
                balance(nroot);
        }
    }

    //recursive balancing function, rebalance 2-3 tree untill parent of size 1 or new root, very compute heavy
    private void balance(TreeNode<E> replaceRoot) {
        if(replaceRoot.isRoot()){
            root=replaceRoot;
        } else if (replaceRoot.getParent().size() == 1){
            if (replaceRoot.getKey1().compareTo(replaceRoot.getParent().getKey1()) < 0){
                replaceRoot.getParent().setKey2(replaceRoot.getParent().getKey1());
                replaceRoot.getParent().setKey1(replaceRoot.getKey1());
                replaceRoot.getParent().setChild3(replaceRoot.getParent().getChild2());
                replaceRoot.getParent().setChild1(replaceRoot.getChild1());
                replaceRoot.getParent().setChild2(replaceRoot.getChild2());
                replaceRoot.getParent().getChild1().setParent(replaceRoot.getParent());
                replaceRoot.getParent().getChild2().setParent(replaceRoot.getParent());
            } else {
                replaceRoot.getParent().setKey2(replaceRoot.getKey1());
                replaceRoot.getParent().setChild2(replaceRoot.getChild1());
                replaceRoot.getParent().setChild3(replaceRoot.getChild2());
                replaceRoot.getChild1().setParent(replaceRoot.getParent());
                replaceRoot.getChild2().setParent(replaceRoot.getParent());
                replaceRoot.getParent().getChild3().setParent(replaceRoot.getParent());
                replaceRoot.getParent().getChild2().setParent(replaceRoot.getParent());
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
                lchild.getChild1().setParent(lchild);
                lchild.getChild2().setParent(lchild);
                rchild.getChild2().setParent(rchild);
                rchild.getChild1().setParent(rchild);
            }
                balance(nroot);
        }
    }

    @Override
    public boolean remove(E e) {
        if (contains(e)){
            size--;
            TreeNode<E> node = search(e);
            TreeNode<E> largestLchild = largestLchild(node,e);
            if (largestLchild.size() == 2){
                if (e.equals(node.getKey1())){
                    node.setKey1(largestLchild.getKey2());
                } else {
                    node.setKey2(largestLchild.getKey2());
                }
                largestLchild.setKey2(null);
            } else {
                if (largestLchild.isRoot()){
                    root = null;
                } else {
                    if(node.getKey1().equals(e)){
                        node.setKey1(largestLchild.getKey2()!=null?largestLchild.getKey2():largestLchild.getKey1());
                    } else {
                        node.setKey2(largestLchild.getKey2()!=null?largestLchild.getKey2(): largestLchild.getKey1());
                    }
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

    private void delbalance(TreeNode<E> parent) { //TODO: if problem node is empty and not null move children correctly
        if (subtreesize(parent) == 6){
            if (parent.getChild1() == null || parent.getChild1().isEmpty()){
                setchild1(parent);
                movechildren1(parent);

            } else if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                setchild2(parent);
                parent.getChild1().setChild3(null);
                if(parent.getChild2().getChild1()!=null){
                    parent.getChild2().getChild1().setParent(parent.getChild2());
                }
            } else {
                setchild3(parent);
                parent.getChild2().setChild3(null);
                if(parent.getChild3().getChild1()!=null){
                    parent.getChild3().getChild1().setParent(parent.getChild3());
                }
                parent.setKey2(parent.getChild2().getKey2());
                parent.getChild2().setKey2(null);
            }
        } else if (subtreesize(parent) == 5){
            if (parent.getChild2() != null && parent.getChild2().size() == 2){
                if (parent.getChild1() == null || parent.getChild1().isEmpty()){
                    setchild1(parent);
                    parent.getChild2().setChild1(parent.getChild2().getChild2());
                    parent.getChild2().setChild2(parent.getChild2().getChild3());
                    parent.getChild2().setChild3(null);
                    parent.setKey1(parent.getChild2().getKey1());
                    parent.getChild2().setKey1(parent.getChild2().getKey2());
                    parent.getChild2().setKey2(null);
                } else {
                    setchild3(parent);
                    if(parent.getChild3().getChild1() !=null){
                        parent.getChild3().getChild1().setParent(parent.getChild3());
                    }
                    parent.getChild2().setChild3(null);
                    parent.setKey2(parent.getChild2().getKey2());
                    parent.getChild2().setKey2(null);
                }
            } else { //scenario with no double node in the middle
                if (parent.getChild1() != null && parent.getChild1().size() == 2){ //double node is first child
                    if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                        setchild2(parent);
                        if(parent.getChild2().getChild1() !=null){
                            parent.getChild2().getChild1().setParent(parent.getChild2());
                        }
                        parent.getChild1().setChild3(null);
                    } else {
                        if(parent.getChild3()!= null){
                            parent.getChild3().setKey1(parent.getKey2());
                        } else {
                            parent.setChild3(new TreeNode<>(parent, parent.getKey2()));
                        }
                        parent.setKey2(parent.getChild2().getKey1());
                        parent.getChild3().setChild2(parent.getChild3().getChild1());
                        parent.getChild3().setChild1(parent.getChild2().getChild2());
                        parent.getChild2().setChild2(parent.getChild2().getChild1());
                        parent.getChild2().setChild1(parent.getChild1().getChild3());
                        if(parent.getChild3().getChild1() !=null){
                            parent.getChild3().getChild1().setParent(parent.getChild3());
                            parent.getChild2().getChild1().setParent(parent.getChild2());
                        }
                        parent.getChild2().setKey1(parent.getKey1());
                        parent.setKey1(parent.getChild1().getKey2());
                        parent.getChild1().setKey2(null);
                        parent.getChild1().setChild3(null);
                    }
                } else { //3rd node is double node
                    if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                        if(parent.getChild2() != null){
                            parent.getChild2().setKey1(parent.getKey2());
                        } else {
                            parent.setChild2(new TreeNode<>(parent,parent.getKey2()));
                        }
                        parent.getChild2().setChild2(parent.getChild3().getChild1());
                        if(parent.getChild2().getChild2() != null){
                            parent.getChild2().getChild2().setParent(parent.getChild2());
                        }
                    } else {
                        if(parent.getChild1() != null){
                            parent.getChild1().setKey1(parent.getKey1());
                        } else {
                            parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
                        }
                        parent.getChild1().setChild2(parent.getChild2().getChild1());
                        parent.setKey1(parent.getChild2().getKey1());
                        parent.getChild2().setChild1(parent.getChild2().getChild2());
                        parent.getChild2().setKey1(parent.getKey2());
                        parent.getChild2().setChild2(parent.getChild3().getChild1());
                        if(parent.getChild1().getChild1() != null){
                            parent.getChild1().getChild2().setParent(parent.getChild1());
                            parent.getChild2().getChild2().setParent(parent.getChild2());
                        }
                    }
                    parent.setKey2(parent.getChild3().getKey1());
                    parent.getChild3().setKey1(parent.getChild3().getKey2());
                    parent.getChild3().setKey2(null);
                    parent.getChild3().setChild1(parent.getChild3().getChild2());
                    parent.getChild3().setChild2(parent.getChild3().getChild3());
                    parent.getChild3().setChild3(null);
                }
            }
        } else if (subtreesize(parent) == 4){ //TODO: implement subtrees 4-2(recursion for 2tree)
            if (parent.getChild3() == null || parent.getChild3().isEmpty()){
                parent.getChild2().setKey2(parent.getKey2());
                parent.setKey2(null);
                if(parent.getChild3() != null){
                    parent.getChild2().setChild3(parent.getChild3().getChild1());
                    parent.getChild2().getChild3().setParent(parent.getChild2());
                    parent.setChild3(null);
                }
            } else if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                parent.getChild1().setKey2(parent.getKey1());
                parent.setKey1(parent.getKey2());
                if(parent.getChild2() != null){
                    parent.getChild1().setChild3(parent.getChild2().getChild1());
                    parent.getChild1().getChild3().setParent(parent.getChild1());
                }
                parent.setChild2(parent.getChild3());
                parent.setChild3(null);
                parent.setKey2(null);
            } else {
                //wrong, no need for new node
                if(parent.getChild1() != null){
                    parent.getChild1().setKey1(parent.getKey1());
                } else {
                    parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
                }
                parent.getChild1().setKey2(parent.getChild2().getKey1());
                parent.getChild1().setChild2(parent.getChild2().getChild1());
                parent.getChild1().setChild3(parent.getChild2().getChild2());
                if(parent.getChild1().getChild2() != null){
                    parent.getChild1().getChild2().setParent(parent.getChild1());
                    parent.getChild1().getChild3().setParent(parent.getChild1());
                }
                parent.setChild2(parent.getChild3());
                parent.setKey1(parent.getKey2());
                parent.setKey2(null);
                parent.setChild3(null);
            }
        } else if (subtreesize(parent) == 3){
            if (parent.getChild2() == null || parent.getChild2().isEmpty()){
                if (parent.getChild2() != null){
                    parent.getChild2().setKey1(parent.getKey1());
                    parent.getChild2().setChild2(parent.getChild2().getChild1());
                    parent.getChild2().setChild1(parent.getChild1().getChild3());
                    parent.getChild2().getChild1().setParent(parent.getChild2());
                    parent.getChild1().setChild3(null);
                } else {
                    parent.setChild2(new TreeNode<>(parent, parent.getKey1()));
                }
                parent.setKey1(parent.getChild1().getKey2());
                parent.getChild1().setKey2(null);
            } else {
                if (parent.getChild1() != null){ // if problem point is empty node, move children of empty node
                    parent.getChild1().setKey1(parent.getKey1());
                    parent.getChild1().setChild2(parent.getChild2().getChild1());
                    if (parent.getChild1().getChild2() != null){
                        parent.getChild1().getChild2().setParent(parent.getChild1());
                    }
                    parent.getChild2().setChild1(null);
                } else {
                    parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
                }
                movechildren1(parent);
            }
        } else { //subtree is size 2 --> problem because size of balanced tree is smaller
            if (parent.getChild2() == null || parent.getChild2().isEmpty()){ //
                parent.getChild1().setKey2(parent.getKey1());
                parent.setKey1(null);
                if(parent.getChild2()!=null){
                    parent.getChild1().setChild3(parent.getChild2().getChild1());
                    parent.getChild1().getChild3().setParent(parent.getChild1());
                }
            } else { // make empty node as root of subtree size 2 and repeat balance with empty node as problem node
                parent.getChild2().setKey2(parent.getChild2().getKey1());
                parent.getChild2().setKey1(parent.getKey1());
                if (parent.getChild1() != null){ // if problem node is empty and not null, move children as well
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

    private void setchild3(TreeNode<E> parent) {
        if(parent.getChild3()!= null){
            parent.getChild3().setKey1(parent.getKey2());
        }else {
            parent.setChild3(new TreeNode<>(parent, parent.getKey2()));
        }
        parent.getChild3().setChild2(parent.getChild3().getChild1());
        parent.getChild3().setChild1(parent.getChild2().getChild3());
    }

    private void setchild2(TreeNode<E> parent) {
        if(parent.getChild2() != null){
            parent.getChild2().setKey1(parent.getKey1());
        } else {
            parent.setChild2(new TreeNode<>(parent, parent.getKey1()));
        }
        parent.setKey1(parent.getChild1().getKey2());
        parent.getChild1().setKey2(null);
        parent.getChild2().setChild2(parent.getChild2().getChild1());
        parent.getChild2().setChild1(parent.getChild1().getChild3());
    }

    private void setchild1(TreeNode<E> parent) {
        if(parent.getChild1() != null){
            parent.getChild1().setKey1(parent.getKey1());
        } else {
            parent.setChild1(new TreeNode<>(parent, parent.getKey1()));
        }
        parent.getChild1().setChild2(parent.getChild2().getChild1());
        if(parent.getChild1().getChild2() != null){
            parent.getChild1().getChild2().setParent(parent.getChild1());
        }
    }

    private void movechildren1(TreeNode<E> parent) {
        parent.setKey1(parent.getChild2().getKey1());
        parent.getChild2().setKey1(parent.getChild2().getKey2());
        parent.getChild2().setKey2(null);
        parent.getChild2().setChild1(parent.getChild2().getChild2());
        parent.getChild2().setChild2(parent.getChild2().getChild3());
        parent.getChild2().setChild3(null);
    }

    public int subtreesize(TreeNode<E> root) {
        return root.size() + ((root.getChild1() == null || root.getChild1().isEmpty()) ? 0 : root.getChild1().size()) +
                ((root.getChild2() == null || root.getChild2().isEmpty()) ? 0 : root.getChild2().size()) +
                ((root.getChild3() == null || root.getChild3().isEmpty()) ? 0 : root.getChild3().size());
    }

}
