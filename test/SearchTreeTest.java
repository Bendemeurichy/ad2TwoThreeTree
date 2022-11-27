import opgave.SearchTree;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface SearchTreeTest {
    int k=100000;

    SearchTree<Integer> createTree();

    @Test
    default void empty() {
        SearchTree<Integer> tree = createTree();
        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
    }

    @Test
    default void addOne() {
        SearchTree<Integer>tree = createTree();

        assertFalse(tree.contains(1));
        tree.add(1);
        assertTrue(tree.contains(1));
        assertEquals(1, tree.size());
    }

    @Test
    default void removeOne() {
        SearchTree<Integer>tree = createTree();

        assertFalse(tree.contains(1));
        tree.add(1);
        assertTrue(tree.contains(1));
        tree.remove(1);
        assertFalse(tree.contains(1));
        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
    }

    @Test
    default void addMultiple() {
        long start=System.currentTimeMillis();
        SearchTree<Integer> tree = createTree();

        for (int i = 0; i < k; i++) {
            assertTrue(tree.add(i));
        }
        for (int i = 0; i < k; i++) {
            assertTrue(tree.contains(i), String.format("should contain %d", i));
        }
        System.out.println("adding " + k + " elements to an empty tree costs " + (System.currentTimeMillis()-start));
    }

    @Test
    default void removeMultiple() {
        SearchTree<Integer>tree = createTree();

        for (int i = 0; i < k; i++) {
            assertTrue(tree.add(i), String.format("should change when adding %d", i));
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < k; i++) {
            assertTrue(tree.contains(i), String.format("should contain %d", i));
            assertTrue(tree.remove(i), String.format("should change when removing %d", i));
            assertFalse(tree.contains(i), String.format("should not contain %d anymore", i));
        }
        assertEquals(0, tree.size(), "should be empty");
        System.out.println("removing " + k + " elements costs: " + (System.currentTimeMillis()-start) );
    }

    @Test
    default void iterator() {
        SearchTree<Integer> tree = createTree();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.add(i), String.format("should change when adding %d", i));
            expected.add(i);
        }
        assertIterableEquals(expected, tree);
    }
}
