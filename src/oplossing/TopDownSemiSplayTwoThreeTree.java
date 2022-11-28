package oplossing;


import java.util.Stack;

public class TopDownSemiSplayTwoThreeTree<E extends Comparable<E>> extends BottomUpSemiSplayTwoThreeTree<E> {

    Stack<TreeNode<E>> splayStack = new Stack<>();

    @Override
    public boolean contains(E o) {
        splayStack.clear();
        if (root == null){
            return false;
        }
        return searchAndSplay(root,o)!= null;
    }

    public TreeNode<E> searchAndSplay(TreeNode<E> start,E goal) {
        if (start.getKey1().equals(goal)){
            splayStack.push(start);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return start;
        }
        if(start.size()==2 && start.getKey2().equals(goal)){
            splayStack.push(start);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return start;
        }
        if (start.isleaf()){
            splayStack.push(start);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return null;
        } else if (goal.compareTo(start.getKey1()) < 0){
            splayStack.push(start);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return start.getChild1()==null? null : searchAndSplay(start.getChild1(), goal);
        } else if ((start.size()==2 && goal.compareTo(start.getKey2()) < 0) || (start.size()==1)){
            splayStack.push(start);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return start.getChild2()==null? null :searchAndSplay(start.getChild2(), goal);
        } else if (goal.compareTo(start.getKey2()) > 0){
            splayStack.push(start);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return start.getChild3()==null? null : searchAndSplay(start.getChild3(), goal);
        }
        splayStack.push(start);
        splay(splayStack);
        return null;
    }

    @Override
    public boolean add(E o){
        splayStack.clear();
        if (root == null){
            size++;
            root = new TreeNode<>(null, o);
            return true;
        }
        TreeNode<E> found = searchAndSplay(root,o);
        if (found != null){
            return false;
        }
        boolean added = false;
        TreeNode<E> current = root;
        while(!added){
            if (current.size() == 1 && current.getChild1() == null && current.getChild2() == null) {
                if (o.compareTo(current.getKey1()) < 0){
                    current.setKey2(current.getKey1());
                    current.setKey1(o);
                } else {
                    current.setKey2(o);
                }
                added = true;

            } else {
                if (o.compareTo(current.getKey1()) < 0){
                    if (current.getChild1() == null){
                        current.setChild1(new TreeNode<>(current, o));
                        splayStack.push(current.getChild1());
                        if(splayStack.size()==3){
                            splay(splayStack);
                        }
                        added = true;
                    } else {
                        current = current.getChild1();
                    }
                } else if (current.getKey2() == null || o.compareTo(current.getKey2()) < 0){
                    if (current.getChild2() == null){
                        current.setChild2(new TreeNode<>(current, o));
                        splayStack.push(current.getChild2());
                        if(splayStack.size()==3){
                            splay(splayStack);
                        }
                        added = true;
                    } else {
                        current = current.getChild2();
                    }
                } else {
                    if (current.getChild3() == null){
                        current.setChild3(new TreeNode<>(current, o));
                        splayStack.push(current.getChild3());
                        if(splayStack.size()==3){
                            splay(splayStack);
                        }
                        added = true;
                    } else {
                        current = current.getChild3();
                    }
                    }
                }
            }
        size++;
        return true;
    }

    @Override
    public boolean remove(E e){
        splayStack.clear();
        if (root == null){
            return false;
        }
        if (search(e) == null){
            searchAndSplay(root,e);
            return false;
        }
        size--;
        if (size == 0){
            root = null;
            return true;
        }
        TreeNode<E> node = searchAndSplay(root,e);
        if(node.size()==1){
            if(node.getChild1()==null && node.getChild2()!= null){
                swapChild(node,node.getChild2());
            } else if (node.getChild2()==null && node.getChild1()!=null){
                swapChild(node,node.getChild1());
            } else if(node.getChild1()!=null && node.getChild2()!=null){
                TreeNode<E> largestL=largestLchild(node,e);
                if(largestL.size()==1){
                    if(largestL.getChild1()==null){
                        node.setKey1(largestL.getKey1());
                        removeChild(largestL);
                    } else {
                        node.setKey1(largestL.getKey1());
                        swapChild(largestL,largestL.getChild1());
                    }
                } else {
                    node.setKey1(largestL.getKey2());
                    largestL.setKey2(null);
                }
            }else {
                removeChild(node);
            }

        } else {
            if(node.getChild1()!=null && node.getChild2()!=null && node.getChild3()!=null){
                if(node.getKey1().equals(e)){
                    switchKey1(e, node);
                } else {
                    switchKey2(e, node);
                }
            } else if(node.getChild1()!=null && node.getChild2()!=null && e.equals(node.getKey1())){
                switchKey1(e, node);
            } else if(node.getChild2()!=null && node.getChild3()!=null && e.equals(node.getKey2())){
                switchKey2(e, node);
            } else if(e.equals(node.getKey1())){
                node.setKey1(node.getKey2());
                node.setKey2(null);
                if(node.getChild2()!=null){
                    node.setChild1(node.getChild2());
                }
                node.setChild2(node.getChild3());
                node.setChild3(null);
            } else {
                node.setKey2(null);
                if(node.getChild2()==null){
                    node.setChild2(node.getChild3());
                    node.setChild3(null);
                }
            }
        }
        return true;
    }
}
