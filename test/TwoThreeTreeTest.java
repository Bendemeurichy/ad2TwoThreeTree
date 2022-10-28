import opgave.SearchTree;
import oplossing.TwoThreeTree;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
}
