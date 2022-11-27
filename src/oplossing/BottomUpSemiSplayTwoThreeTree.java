package oplossing;

import java.util.Stack;

public class BottomUpSemiSplayTwoThreeTree<E extends Comparable<E>> extends General23Tree<E> {
    @Override
    public boolean contains(E o) {
        if (root == null){
            return false;
        }
        splay(collectPath(o));
        return search(o) != null;
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
        splay(collectPath(o));
        search( o);
        return false;
    }

    //radd is too specific for 23tree (thinks every leaf is same depth), so we override it

    public void radd(E o, TreeNode<E> node) {
        if (node.size() == 1 && node.getChild1() == null && node.getChild2() == null) {
            if (o.compareTo(node.getKey1()) < 0){
                node.setKey2(node.getKey1());
                node.setKey1(o);

            } else {
                node.setKey2(o);
            }
            splay(collectPath(node.getKey1()));

        } else {
            if (o.compareTo(node.getKey1()) < 0){
                if (node.getChild1() == null){
                    node.setChild1(new TreeNode<>(node, o));
                    splay(collectPath(node.getChild1().getKey1()));
                } else {
                    radd(o, node.getChild1());
                }
            } else if (node.getKey2() == null || o.compareTo(node.getKey2()) < 0){
                if (node.getChild2() == null){
                    node.setChild2(new TreeNode<>(node, o));
                    splay(collectPath(node.getChild2().getKey1()));
                } else {
                    radd(o, node.getChild2());
                }
            } else {
                if (node.getChild3() == null){
                    node.setChild3(new TreeNode<>(node, o));
                    splay(collectPath(node.getChild3().getKey1()));
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
            splay(collectPath(e));
            return false;
        }
        size--;
        if (size == 0){
            root = null;
            return true;
        }
        TreeNode<E> node = search(e);
        if(node.size()==1){
            if(node.getChild1()==null && node.getChild2()!= null){
                swapChild(node,node.getChild2());
                splay(collectPath(node.getKey1()));
            } else if (node.getChild2()==null && node.getChild1()!=null){
                swapChild(node,node.getChild1());
                splay(collectPath(node.getKey1()));
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
                splay(collectPath(node.getKey1()));
            }else {
                removeChild(node);
                splay(collectPath(node.getParent().getKey1()));
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
            splay(collectPath(node.getKey1()));
        }

        return true;
    }

    private void switchKey2(E e, TreeNode<E> node) {
        TreeNode<E> smallestR=smallestRchild(node,e);
        if(smallestR.size()==1){
            node.setKey2(smallestR.getKey1());
            if(smallestR.getChild2()!=null ){
                swapChild(smallestR,smallestR.getChild2());
            } else {
                removeChild(smallestR);
            }
        } else {
            node.setKey2(smallestR.getKey1());
            smallestR.setKey1(smallestR.getKey2());
            smallestR.setChild1(smallestR.getChild2());
            smallestR.setChild2(smallestR.getChild3());
            smallestR.setChild3(null);
            smallestR.setKey2(null);
        }
    }

    private void switchKey1(E e, TreeNode<E> node) {
        TreeNode<E> largestL=largestLchild(node,e);
        if(largestL.size()==1){
            node.setKey1(largestL.getKey1());
            if(largestL.getChild1()!=null){
                swapChild(largestL,largestL.getChild1());
            } else {
                removeChild(largestL);
            }
        } else {
            node.setKey1(largestL.getKey2());
            largestL.setKey2(null);
        }
    }


    //FIXME: too much function calls
    //Save splay path in stack?
    //rework splay to splay nodes, not keys, draw out
    public void splay(Stack<TreeNode<E>> splaypath) {
        TreeNode<E> lastNode = splaypath.pop();
        while (splaypath.size() >= 2) {
            TreeNode<E> parent = splaypath.pop();
            TreeNode<E> grandparent = splaypath.pop();
            if (grandparent.size() == 2 && grandparent.getChild2() != null && parent.equals(grandparent.getChild2())){
                if (lastNode.equals(parent.getChild1())){
                    E temp = lastNode.size() == 2 ? lastNode.getKey2() : lastNode.getKey1();
                    if (lastNode.size() == 2){
                        lastNode.setKey2(lastNode.getKey1());
                        parent.setChild1(lastNode.getChild3());
                        if (lastNode.getChild3() != null){
                            lastNode.getChild3().setParent(parent);
                        }
                        lastNode.setChild3(lastNode.getChild2());

                    } else {
                        parent.setChild1(lastNode.getChild2());
                        if (lastNode.getChild2() != null){
                            lastNode.getChild2().setParent(parent);
                        }
                    }
                    lastNode.setKey1(grandparent.getKey1());
                    grandparent.setKey1(temp);
                    lastNode.setChild2(lastNode.getChild1());
                    lastNode.setChild1(grandparent.getChild1());
                    if (grandparent.getChild1() != null){
                        grandparent.getChild1().setParent(lastNode);
                    }
                    grandparent.setChild1(lastNode);
                    lastNode.setParent(grandparent);
                } else if(parent.size()==2 && lastNode.equals(parent.getChild2())){
                    TreeNode<E> tempNode;
                    E temp = lastNode.size() == 2 ? lastNode.getKey2() : lastNode.getKey1();
                    if (lastNode.size() == 2){
                        E temp2 = lastNode.getKey1();
                        lastNode.setKey1(grandparent.getKey1());
                        lastNode.setKey2(parent.getKey1());
                        parent.setKey1(temp);
                        grandparent.setKey1(temp2);
                        parent.setChild2(lastNode.getChild3());
                        if (lastNode.getChild3() != null){
                            lastNode.getChild3().setParent(parent);
                        }
                        tempNode = lastNode.getChild2();
                        lastNode.setChild3(lastNode.getChild1());
                    } else {
                        lastNode.setKey1(grandparent.getKey1());
                        grandparent.setKey1(parent.getKey1());
                        parent.setKey1(temp);
                        parent.setChild2(lastNode.getChild2());
                        if (lastNode.getChild2() != null){
                            lastNode.getChild2().setParent(parent);
                        }
                        tempNode = lastNode.getChild1();

                    }
                    lastNode.setChild2(parent.getChild1());
                    if (parent.getChild1() != null){
                        parent.getChild1().setParent(lastNode);
                    }
                    lastNode.setChild1(grandparent.getChild1());
                    if (grandparent.getChild1() != null){
                        grandparent.getChild1().setParent(lastNode);
                    }
                    parent.setChild1(tempNode);
                    if (tempNode != null){
                        tempNode.setParent(parent);
                    }
                        grandparent.setChild1(lastNode);
                        lastNode.setParent(grandparent);
                } else if (lastNode.equals(parent.getChild2()) || lastNode.equals(parent.getChild3())) {
                    E temp = parent.size() == 2 ? parent.getKey2() : parent.getKey1();
                    if ( parent.size()== 2) {
                        parent.setKey2(parent.getKey1());
                        parent.setKey1(grandparent.getKey1());
                        grandparent.setKey1(temp);
                        parent.setChild3(parent.getChild2());

                    } else {
                        parent.setKey1(grandparent.getKey1());
                        grandparent.setKey1(temp);
                    }
                    parent.setChild2(parent.getChild1());
                    parent.setChild1(grandparent.getChild1());
                    if (grandparent.getChild1() != null){
                        grandparent.getChild1().setParent(parent);
                    }
                    grandparent.setChild1(parent);
                    parent.setParent(grandparent);
                    grandparent.setChild2(lastNode);
                    lastNode.setParent(grandparent);
                }
                lastNode = grandparent;
            } else if ( parent.size()==2 && lastNode.equals(parent.getChild2())){
                if ( parent.equals(grandparent.getChild1())){
                    grandparent.setChild1(parent.getChild3());
                    if (parent.getChild3() != null){
                        parent.getChild3().setParent(grandparent);
                    }
                    if(grandparent.getParent()!= null){
                        if(grandparent.getParent().getChild1()!=null && grandparent.getParent().getChild1().equals(grandparent)){
                            grandparent.getParent().setChild1(parent);
                        } else if (grandparent.getParent().getChild2()!=null && grandparent.getParent().getChild2().equals(grandparent)){
                            grandparent.getParent().setChild2(parent);
                        } else {
                            grandparent.getParent().setChild3(parent);
                        }
                        parent.setParent(grandparent.getParent());
                    } else {
                        parent.setParent(null);
                        root = parent;
                    }
                    parent.setChild3(grandparent);
                    grandparent.setParent(parent);
                } else if ( parent.equals(grandparent.getChild3()) || parent.equals(grandparent.getChild2())){
                    if (grandparent.size()==1){
                        grandparent.setChild2(parent.getChild1());
                    } else {
                        grandparent.setChild3(parent.getChild1());
                    }
                    if (parent.getChild1() != null){
                        parent.getChild1().setParent(grandparent);
                    }
                    if(grandparent.getParent()!= null){
                        if(grandparent.getParent().getChild1()!=null && grandparent.getParent().getChild1().equals(grandparent)){
                            grandparent.getParent().setChild1(parent);
                        } else if (grandparent.getParent().getChild2()!=null && grandparent.getParent().getChild2().equals(grandparent)){
                            grandparent.getParent().setChild2(parent);
                        } else {
                            grandparent.getParent().setChild3(parent);
                        }
                        parent.setParent(grandparent.getParent());
                    } else {
                        parent.setParent(null);
                        root = parent;
                    }
                    parent.setChild1(grandparent);
                    grandparent.setParent(parent);
                }
                lastNode = parent;
            } else if ( parent.equals(grandparent.getChild1()) && lastNode.equals(parent.getChild1())){
                if(grandparent.getParent()!= null){
                    if(grandparent.getParent().getChild1()!=null && grandparent.getParent().getChild1().equals(grandparent)){
                        grandparent.getParent().setChild1(parent);
                    } else if (grandparent.getParent().getChild2()!=null && grandparent.getParent().getChild2().equals(grandparent)){
                        grandparent.getParent().setChild2(parent);
                    } else {
                        grandparent.getParent().setChild3(parent);
                    }
                    parent.setParent(grandparent.getParent());
                } else {
                    parent.setParent(null);
                    root = parent;
                }
                if(parent.size()==2){
                    grandparent.setChild1(parent.getChild3());
                    if (parent.getChild3() != null){
                        parent.getChild3().setParent(grandparent);
                    }
                    parent.setChild3(grandparent);
                    grandparent.setParent(parent);
                } else {
                    grandparent.setChild1(parent.getChild2());
                    if (parent.getChild2() != null){
                        parent.getChild2().setParent(grandparent);
                    }
                    parent.setChild2(grandparent);
                    grandparent.setParent(parent);
                }
                lastNode = parent;
            } else if ( parent.equals(grandparent.getChild1()) && (lastNode.equals(parent.getChild2()) || lastNode.equals(parent.getChild3()))){
                if(grandparent.getParent()!= null){
                    if(grandparent.getParent().getChild1()!=null && grandparent.getParent().getChild1().equals(grandparent)){
                        grandparent.getParent().setChild1(lastNode);
                    } else if (grandparent.getParent().getChild2()!=null && grandparent.getParent().getChild2().equals(grandparent)){
                        grandparent.getParent().setChild2(lastNode);
                    } else {
                        grandparent.getParent().setChild3(lastNode);
                    }
                    lastNode.setParent(grandparent.getParent());
                } else {
                    lastNode.setParent(null);
                    root = lastNode;
                }
                if (parent.size()==2){
                    parent.setChild3(lastNode.getChild1());
                } else {
                    parent.setChild2(lastNode.getChild1());
                }
                if (lastNode.getChild1() != null){
                    lastNode.getChild1().setParent(parent);
                }
                if(lastNode.size()==2){
                    grandparent.setChild1(lastNode.getChild3());
                    if (lastNode.getChild3() != null){
                        lastNode.getChild3().setParent(grandparent);
                    }
                    lastNode.setChild3(grandparent);
                    grandparent.setParent(lastNode);
                } else {
                    grandparent.setChild1(lastNode.getChild2());
                    if (lastNode.getChild2() != null){
                        lastNode.getChild2().setParent(grandparent);
                    }
                    lastNode.setChild2(grandparent);
                    grandparent.setParent(lastNode);
                }
                lastNode.setChild1(parent);
                parent.setParent(lastNode);
            } else if ( (parent.equals(grandparent.getChild2())||parent.equals(grandparent.getChild3())) && lastNode.equals(parent.getChild1())){
                if(grandparent.getParent()!= null){
                    if(grandparent.getParent().getChild1()!=null && grandparent.getParent().getChild1().equals(grandparent)){
                        grandparent.getParent().setChild1(lastNode);
                    } else if (grandparent.getParent().getChild2()!=null && grandparent.getParent().getChild2().equals(grandparent)){
                        grandparent.getParent().setChild2(lastNode);
                    } else {
                        grandparent.getParent().setChild3(lastNode);
                    }
                    lastNode.setParent(grandparent.getParent());
                } else {
                    lastNode.setParent(null);
                    root = lastNode;
                }
                if (grandparent.size()==2){
                    grandparent.setChild3(lastNode.getChild1());
                } else {
                    grandparent.setChild2(lastNode.getChild1());
                }
                if(lastNode.getChild1()!= null){
                    lastNode.getChild1().setParent(grandparent);
                }
                if ( lastNode.size()==2){
                    parent.setChild1(lastNode.getChild3());
                    if (lastNode.getChild3() != null){
                        lastNode.getChild3().setParent(parent);
                    }
                    lastNode.setChild3(parent);
                    parent.setParent(lastNode);
                } else {
                    parent.setChild1(lastNode.getChild2());
                    if (lastNode.getChild2() != null){
                        lastNode.getChild2().setParent(parent);
                    }
                    lastNode.setChild2(parent);
                    parent.setParent(lastNode);
                }
                lastNode.setChild1(grandparent);
                grandparent.setParent(lastNode);
            } else {
                if(grandparent.getParent()!= null){
                    if(grandparent.getParent().getChild1()!=null && grandparent.getParent().getChild1().equals(grandparent)){
                        grandparent.getParent().setChild1(parent);
                    } else if (grandparent.getParent().getChild2()!=null && grandparent.getParent().getChild2().equals(grandparent)){
                        grandparent.getParent().setChild2(parent);
                    } else {
                        grandparent.getParent().setChild3(parent);
                    }
                    parent.setParent(grandparent.getParent());
                } else {
                    parent.setParent(null);
                    root = parent;
                }
                if (grandparent.size()==2){
                    grandparent.setChild3(parent.getChild1());
                } else {
                    grandparent.setChild2(parent.getChild1());
                }
                if (parent.getChild1() != null){
                    parent.getChild1().setParent(grandparent);
                }
                parent.setChild1(grandparent);
                grandparent.setParent(parent);
                lastNode = parent;
            }

        }
    }

    public Stack<TreeNode<E>> collectPath(E o){
        Stack<TreeNode<E>> currentPath = new Stack<>();
        boolean found = false;
        TreeNode<E> current = root;

        while(!found) {
            currentPath.push(current);
            if (current.getKey1().equals(o)){
                found = true;
            } else if (current.size() == 2 && current.getKey2().equals(o)){
                found = true;
            } else if (current.isleaf()){
                found = true;
            } else if (o.compareTo(current.getKey1()) < 0){
                if (current.getChild1() == null){
                    found = true;
                } else {
                    current = current.getChild1();
                }
            } else if ((current.getKey2() == null || o.compareTo(current.getKey2()) < 0)){
                if(current.getChild2() == null){
                    found = true;
                } else {
                    current = current.getChild2();
                }
            } else if (o.compareTo(current.getKey2()) > 0){
                if (current.getChild3() == null){
                    found = true;
                } else {
                    current = current.getChild3();
                }
            }
        }
        return currentPath;
    }

}