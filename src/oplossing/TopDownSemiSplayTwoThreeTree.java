package oplossing;


import opgave.SearchTree;
import opgave.samplers.Sampler;
import opgave.samplers.ZipfSampler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TopDownSemiSplayTwoThreeTree<E extends Comparable<E>> implements SearchTree<E> {

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
            TreeNode<E> next =start.getChild1()==null? null : searchAndSplay(start.getChild1(), goal);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return next;
        } else if ((start.size()==2 && goal.compareTo(start.getKey2()) < 0) || (start.size()==1)){
            splayStack.push(start);
            TreeNode<E> next =start.getChild2()==null? null : searchAndSplay(start.getChild2(), goal);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return next;
        } else if (goal.compareTo(start.getKey2()) > 0){
            splayStack.push(start);
            TreeNode<E> next=start.getChild3()==null? null : searchAndSplay(start.getChild3(), goal);
            if(splayStack.size()==3){
                splay(splayStack);
                splayStack.push(start);
            }
            return next;
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
        } else if ((start.size()==2 && goal.compareTo(start.getKey2()) < 0) || (start.size()==1)){
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
        while ((node.size()==2 && node.getChild3()!=null) || (node.size()==1 && node.getChild2()!=null)){
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
        while(node.getChild1()!=null){
            node = node.getChild1();
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
        if(!node.isRoot()){
            if (node.getParent().getChild1()!=null && node.getParent().getChild1().equals(node)){
                node.getParent().setChild1(null);
            } else if (node.getParent().getChild2()!=null &&node.getParent().getChild2().equals(node)){
                node.getParent().setChild2(null);
            } else {
                node.getParent().setChild3(null);
            }
        } else {
            root = null;
        }
    }

    public void swapChild(TreeNode<E> parent,TreeNode<E> node){
        if(! parent.isRoot()){
            if(parent.getParent().getChild1()!=null && parent.getParent().getChild1().equals(parent)){
                parent.getParent().setChild1(node);
                node.setParent(parent.getParent());
            } else if (parent.getParent().getChild2()!=null && parent.getParent().getChild2().equals(parent)){
                parent.getParent().setChild2(node);
                node.setParent(parent.getParent());
            } else {
                parent.getParent().setChild3(node);
                node.setParent(parent.getParent());
            }
        } else {
            root=node;
            node.setParent(null);
        }
    }

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

    public void switchKey2(E e, TreeNode<E> node) {
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

    public void switchKey1(E e, TreeNode<E> node) {
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
    public static void main(String[] args) throws IOException {
        BufferedWriter addNormal = new BufferedWriter(new FileWriter("extra/TopDownBenchAddNormal.csv"));
        BufferedWriter removeNormal = new BufferedWriter(new FileWriter("extra/TopDownBenchRemoveNormal.csv"));
        BufferedWriter addZipf = new BufferedWriter(new FileWriter("extra/TopDownBenchAddZipf.csv"));
        BufferedWriter removeZipf = new BufferedWriter(new FileWriter("extra/TopDownBenchRemoveZipf.csv"));
        BufferedWriter searchNormal = new BufferedWriter(new FileWriter("extra/TopDownBenchSearchNormal.csv"));
        BufferedWriter searchZipf = new BufferedWriter(new FileWriter("extra/TopDownBenchSearchZipf.csv"));
        Random rand = new Random();

        TopDownSemiSplayTwoThreeTree<Integer> tree = new TopDownSemiSplayTwoThreeTree<>();

        int size = 1000;
        int testsize = 100;

        for(int i =0;i<testsize;i++){
            addNormal.append(String.valueOf(size + i * 1000)).append(",");
            removeNormal.append(String.valueOf(size + i * 1000)).append(",");
            addZipf.append(String.valueOf(size + i * 1000)).append(",");
            removeZipf.append(String.valueOf(size + i * 1000)).append(",");
            searchNormal.append(String.valueOf(size + i * 1000)).append(",");
            searchZipf.append(String.valueOf(size + i * 1000)).append(",");
        }

        addNormal.append("\n");
        removeNormal.append("\n");
        addZipf.append("\n");
        removeZipf.append("\n");
        searchNormal.append("\n");
        searchZipf.append("\n");

        for (int i= 0; i <testsize; i++){
            Sampler sampler = new Sampler(rand,size);
            ZipfSampler zipfSampler = new ZipfSampler(rand,size);

            List<Integer> list = sampler.sample(size);
            List<Integer> zipfList = zipfSampler.sample(size);
            long startAddNormal = System.currentTimeMillis();
            for (Integer el: list) {
                tree.add(el);
                tree.contains(el);
            }
            addNormal.append(String.valueOf(System.currentTimeMillis() - startAddNormal)).append(",");

            long startSearchNormal = System.currentTimeMillis();
            for (Integer el: list) {
                tree.contains(el);
            }
            searchNormal.append(String.valueOf(System.currentTimeMillis() - startSearchNormal)).append(",");

            long startRemoveNormal = System.currentTimeMillis();
            for (Integer el: list) {
                tree.remove(el);
                tree.contains(el);
            }
            removeNormal.append(String.valueOf(System.currentTimeMillis() - startRemoveNormal)).append(",");

            tree.clear();
            long startAddZipf = System.currentTimeMillis();
            for (Integer el: zipfList) {
                tree.add(el);
                tree.contains(el);
            }
            addZipf.append(String.valueOf(System.currentTimeMillis() - startAddZipf)).append(",");

            long startSearchZipf = System.currentTimeMillis();
            for (Integer el: zipfList) {
                tree.contains(el);
            }
            searchZipf.append(String.valueOf(System.currentTimeMillis() - startSearchZipf)).append(",");

            long startRemoveZipf = System.currentTimeMillis();
            for (Integer el: zipfList) {
                tree.remove(el);
                tree.contains(el);
            }
            removeZipf.append(String.valueOf(System.currentTimeMillis() - startRemoveZipf)).append(",");


            size+=1000;
        }
        addNormal.close();
        removeNormal.close();
        addZipf.close();
        removeZipf.close();
        searchNormal.close();
        searchZipf.close();
    }
}
