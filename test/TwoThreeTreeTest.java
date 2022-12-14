import opgave.SearchTree;
import opgave.samplers.Sampler;
import oplossing.TwoThreeTree;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TwoThreeTreeTest implements SearchTreeTest {

    @Override
    public TwoThreeTree<Integer> createTree() {
        return new TwoThreeTree<>();
    }
    public TwoThreeTree<String> createStringTree(){return new TwoThreeTree<>();}

    @Test
    void emptyStringtree() {
        SearchTree<String> tree = createStringTree();
        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
    }

    @Test
    void addOneString() {
        SearchTree<String>tree = createStringTree();

        assertFalse(tree.contains("a"));
        tree.add("a");
        assertTrue(tree.contains("a"));
        assertEquals(1, tree.size());
    }

    @Test
    void removeOneString(){
        SearchTree<String>tree = createStringTree();

        assertFalse(tree.contains("a"));
        tree.add("a");
        assertTrue(tree.contains("a"));
        tree.remove("a");
        assertFalse(tree.contains("a"));
        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
    }

    @Test
    void addMultipleStrings(){
        SearchTree<String> tree = createStringTree();

        String a="";
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.add(a));
            a+="a";
        }
        a="";
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.contains(a),("should contain "+ a));
            a+="a";
        }
    }

    @Test
    void removeMultipleString(){
        SearchTree<String>tree = createStringTree();

        String a="";
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.add(a), String.format("should change when adding %d", i));
            a+="a";
        }
        a="";
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.contains(a), String.format("should contain %d", i));
            assertTrue(tree.remove(a), String.format("should change when removing %d", i));
            assertFalse(tree.contains(a), String.format("should not contain %d anymore", i));
            a+="a";
        }
        assertEquals(0, tree.size(), "should be empty");
    }

    @Test
    void Stringiterator() {
        SearchTree<String> tree = createStringTree();
        List<String> expected = new ArrayList<>();
        String a="";
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.add(a), String.format("should change when adding %d", i));
            expected.add(a);
            a+="a";
        }
        assertIterableEquals(expected, tree);
    }
    @Test
    void addRandom(){
        long start=System.currentTimeMillis();
        SearchTree<Integer> tree = createTree();
        Sampler random=new Sampler(new Random(),50000000);
        for (Integer el:random.getElements()) {
            assertTrue(tree.add(el));
            assertTrue(tree.contains(el),("should contain "+ el));

        }
        for(Integer el: random.getElements()){
            assertTrue(tree.contains(el),("should contain "+ el));
        }
        System.out.println("adding " + 50000000 + " elements to an empty tree costs " + (System.currentTimeMillis()-start));
    }

    @Test
    void removeRandom(){
        SearchTree<Integer>tree = createTree();

        Sampler random = new Sampler(new Random(),50000000);
        //ArrayList<Integer> random= new ArrayList<>(Arrays.asList(3,5,6,13,0,8,16,7,10,4,18,17,14,11,15,2,19,9,1,12));
        long start = System.currentTimeMillis();
        for (Integer el : random.getElements()) {
            assertTrue(tree.add(el), String.format("should change when adding %d", el));
        }

        for (Integer el : random.getElements()) {
            assertTrue(tree.contains(el), String.format("should contain %d", el));
            assertTrue(tree.remove(el), String.format("should change when removing %d", el));
            assertFalse(tree.contains(el), String.format("should not contain %d anymore", el));
        }
        assertEquals(0, tree.size(), "should be empty");
        System.out.println(System.currentTimeMillis()-start);
    }
}
