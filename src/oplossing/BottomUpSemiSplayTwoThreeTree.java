package oplossing;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

public class BottomUpSemiSplayTwoThreeTree<E extends Comparable<E>> extends general23Tree<E> {

    @Override
    public boolean contains(E o) {
        if (root == null){
            return false;
        }
        return searchAndSplay(root, o) != null;
    }

    public TreeNode<E> searchAndSplay(TreeNode<E> start, E goal) {
        if (start.getKey1().equals(goal)){
            splay(start);
            return start;
        }
        if (start.size() == 2 && start.getKey2().equals(goal)){
            splay(start);
            return start;
        }
        if (start.isleaf()){
            splay(start);
            return null;
        } else if (goal.compareTo(start.getKey1()) < 0){
            return start.getChild1() == null ? null : searchFrom(start.getChild1(), goal);
        } else if ((start.getKey2() == null || goal.compareTo(start.getKey2()) < 0)){
            return start.getChild2() == null ? null : searchFrom(start.getChild2(), goal);
        } else if (goal.compareTo(start.getKey2()) > 0){
            return start.getChild3() == null ? null : searchFrom(start.getChild3(), goal);
        }
        splay(start);
        return null;
    }

    @Override
    public boolean add(E o) {
        if (root == null){
            size++;
            root = new TreeNode<>(null, o);
            return true;
        }
        if (search(o) == null){
            size++;
            radd(o, root);
            return true;
        }
        searchAndSplay(root, o);
        return false;
    }

    //radd is too specific for 23tree (thinks every leaf is same depth), so we override it

    public void radd(E o, TreeNode<E> node) {
        if (node.size() == 1){
            if (o.compareTo(node.getKey1()) < 0){
                node.setKey2(node.getKey1());
                node.setKey1(o);

            } else {
                node.setKey2(o);
            }
            splay(node);
        } else {
            if (o.compareTo(node.getKey1()) < 0){
                if (node.getChild1() == null){
                    node.setChild1(new TreeNode<>(node, o));
                    splay(node.getChild1());
                } else {
                    radd(o, node.getChild1());
                }
            } else if (node.getKey2() == null || o.compareTo(node.getKey2()) < 0){
                if (node.getChild2() == null){
                    node.setChild2(new TreeNode<>(node, o));
                    splay(node.getChild2());
                } else {
                    radd(o, node.getChild2());
                }
            } else {
                if (node.getChild3() == null){
                    node.setChild3(new TreeNode<>(node, o));
                    splay(node.getChild3());
                } else {
                    radd(o, node.getChild3());
                }
            }
        }
    }

    @Override
    public boolean remove(E e) {
        if (root == null){
            return false;
        }
        if (search(e) == null){
            searchAndSplay(root, e);
            return false;
        }
        size--;
        if (size == 0){
            root = null;
            return true;
        }
        TreeNode<E> node = search(e);
        if (node.isleaf()){
            if (node.size() == 1){
                removeChild(node);
            } else {
                if (e.equals(node.getKey1())){
                    node.setKey1(node.getKey2());
                    node.setKey2(null);

                } else {
                    node.setKey2(null);
                }
            }
            if (!node.isRoot()){
                splay(node.getParent());
            }
            return true;
        }
        if (node.size() == 1){
            if (node.getChild1() == null){
                if (node.isRoot()){
                    root = node.getChild2();
                    root.setParent(null);
                } else {
                    node.getParent().setChild1(node.getChild2());
                    node.getChild2().setParent(node.getParent());
                }
            } else if (node.getChild2() == null){
                if (node.isRoot()){
                    root = node.getChild1();
                    root.setParent(null);
                } else {
                    node.getParent().setChild1(node.getChild1());
                    node.getChild1().setParent(node.getParent());
                }
            } else {
                TreeNode<E> largestleft = largestLchild(node, e);
                node.setKey1(largestleft.size() == 1 ? largestleft.getKey1() : largestleft.getKey2());
                if (largestleft.size() == 1){
                    removeChild(largestleft);
                } else {
                    largestleft.setKey2(null);
                }
                splay(largestleft.getParent());
                return true;
            }
        } else {
            ArrayList<TreeNode<E>> children = node.getAllChildren();
            if (children.size() == 2){
                if (e.equals(node.getKey1())){
                    node.setKey1(node.getKey2());
                    node.setKey2(null);

                } else {
                    node.setKey2(null);
                }
                node.setChild1(children.get(0));
                node.setChild2(children.get(1));
                node.setChild3(null);
            } else if (children.size() == 1){
                if (e.equals(node.getKey1())){
                    node.setKey1(node.getKey2());
                    node.setKey2(null);

                } else {
                    node.setKey2(null);
                }
                node.setChild1(children.get(0));
                node.setChild2(null);
                node.setChild3(null);
            } else {
                TreeNode<E> replacenode = e.equals(node.getKey1()) ? largestLchild(node, e) : smallestRchild(node, e);
                if (replacenode.size() == 1){
                    node.setKey1(replacenode.getKey1());
                    removeChild(replacenode);
                } else {
                    node.setKey1(replacenode.getKey2());
                    replacenode.setKey2(null);
                }
                splay(replacenode.getParent());
                return true;
            }
        }
        if (!node.isRoot()){
            splay(node.getParent());
        }
        return true;
    }


    //FIXME: infinite adds? weird
    public void splay(TreeNode<E> subtreelast) {
        if (!subtreelast.isRoot()){
            if (!subtreelast.getParent().isRoot()){
                TreeNode<E> back1 = subtreelast.getParent();
                TreeNode<E> back2 = back1.getParent();
                ArrayList<E> groupedKeys = groupkeys(subtreelast.getAllKeys(), back1.getAllKeys(), back2.getAllKeys());
                ArrayList<TreeNode<E>> allChildren = groupChildren(subtreelast.getAllChildren(), back1.getAllChildren(), back2.getAllChildren());
                allChildren.removeIf((TreeNode<E> e) -> e == subtreelast || e == back1);
                if (groupedKeys.size() == 3){
                    back2.setKey1(groupedKeys.get(1));
                    back2.setKey2(groupedKeys.get(2));
                    back1.setKey1(groupedKeys.get(0));
                    back2.setChild1(back1);
                    back1.setChild1(null);
                    back1.setChild2(null);
                    //TODO: make simple addChild method that automatically adds a node to existing tree, using the allchildren list

                } else if (groupedKeys.size() == 4){
                    back2.setKey1(groupedKeys.get(2));
                    back2.setKey2(groupedKeys.get(3));
                    back1.setKey1(groupedKeys.get(0));
                    back1.setKey2(groupedKeys.get(1));
                    back2.setChild1(back1);
                    back2.setChild2(null);
                    back2.setChild3(null);
                    back1.setChild1(null);
                    back1.setChild2(null);
                    back1.setChild3(null);
                } else if (groupedKeys.size() == 5){
                    back2.setKey1(groupedKeys.get(2));
                    back2.setKey2(groupedKeys.get(4));
                    back1.setKey1(groupedKeys.get(0));
                    back1.setKey2(groupedKeys.get(1));
                    subtreelast.setKey1(groupedKeys.get(3));
                    subtreelast.setKey2(null);
                    clearSubtree(subtreelast, back1, back2);
                } else if (groupedKeys.size() == 6){
                    back2.setKey1(groupedKeys.get(2));
                    back2.setKey2(groupedKeys.get(5));
                    back1.setKey1(groupedKeys.get(0));
                    back1.setKey2(groupedKeys.get(1));
                    subtreelast.setKey1(groupedKeys.get(3));
                    subtreelast.setKey2(groupedKeys.get(4));
                    clearSubtree(subtreelast, back1, back2);

                }
                for (TreeNode<E> child : allChildren) {
                    addChild(back2, child);
                }
                splay(back2);
            }
        }
    }

    private void clearSubtree(TreeNode<E> subtreelast, TreeNode<E> back1, TreeNode<E> back2) {
        subtreelast.setParent(back2);
        back2.setChild1(back1);
        back2.setChild2(subtreelast);
        back2.setChild3(null);
        back1.setChild1(null);
        back1.setChild2(null);
        back1.setChild3(null);
        subtreelast.setChild1(null);
        subtreelast.setChild2(null);
        subtreelast.setChild3(null);
    }

    public ArrayList<E> groupkeys(ArrayList<E> keys1, ArrayList<E> keys2, ArrayList<E> keys3) {
        ArrayList<E> grouped = new ArrayList<>();
        Stream.of(keys1, keys2, keys3).forEach(grouped::addAll);
        grouped.sort(Comparator.naturalOrder());
        return grouped;
    }

    public ArrayList<TreeNode<E>> groupChildren(ArrayList<TreeNode<E>> children1, ArrayList<TreeNode<E>> children2, ArrayList<TreeNode<E>> children3) {
        ArrayList<TreeNode<E>> grouped = new ArrayList<>();
        Stream.of(children1, children2, children3).forEach(grouped::addAll);
        grouped.sort(Comparator.comparing(TreeNode::getKey1));
        return grouped;
    }

    public void addChild(TreeNode<E> subtreeroot, TreeNode<E> child) {
        //FIXME: add the children to the correct place in the searchtree
        if (child.getKey1().compareTo(subtreeroot.getKey1()) < 0){
            if (subtreeroot.getChild1() == null){
                subtreeroot.setChild1(child);
                child.setParent(subtreeroot);
            } else {
                addChild(subtreeroot.getChild1(), child);
            }
        } else if (subtreeroot.getKey2() == null || child.getKey1().compareTo(subtreeroot.getKey2()) < 0){
            if (subtreeroot.getChild2() == null){
                subtreeroot.setChild2(child);
                child.setParent(subtreeroot);
            } else {
                addChild(subtreeroot.getChild2(), child);
            }
        } else {
            if (subtreeroot.getChild3() == null){
                subtreeroot.setChild3(child);
                child.setParent(subtreeroot);
            } else {
                addChild(subtreeroot.getChild3(), child);
            }
        }
    }
}