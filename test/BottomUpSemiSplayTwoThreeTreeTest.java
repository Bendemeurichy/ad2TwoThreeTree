import opgave.SearchTree;
import opgave.samplers.Sampler;
import oplossing.BottomUpSemiSplayTwoThreeTree;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BottomUpSemiSplayTwoThreeTreeTest implements SearchTreeTest {
    @Override
    public SearchTree<Integer> createTree() {
        return new BottomUpSemiSplayTwoThreeTree<>();
    }

    public SearchTree<String> createStringTree(){return new BottomUpSemiSplayTwoThreeTree<>();}

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
        SearchTree<Integer> tree = createTree();
        Sampler random=new Sampler(new Random(),1000);
        //Integer[] random = new Integer[]{83, 214, 203, 2, 85, 98, 86, 156, 190, 210, 173, 141, 79, 284, 93, 215, 60, 187, 25, 235, 194, 62, 33, 4, 54, 242, 92, 176, 239, 154, 58, 97, 157, 211, 108, 198, 172, 181, 27, 113, 227, 286, 50, 72, 99, 171, 53, 200, 204, 199, 236, 266, 261, 251, 143, 81, 150, 1, 91, 279, 250, 40, 262, 283, 89, 34, 123, 129, 289, 268, 36, 166, 46, 120, 63, 244, 107, 71, 274, 52, 196, 142, 0, 69, 77, 56, 208, 179, 8, 280, 221, 229, 14, 112, 151, 160, 73, 51, 258, 282, 103, 217, 212, 182, 223, 271, 293, 180, 270, 147, 67, 178, 291, 148, 158, 265, 47, 288, 231, 174, 39, 183, 118, 109, 152, 149, 137, 111, 202, 153, 237, 121, 12, 249, 144, 224, 272, 105, 24, 161, 133, 9, 226, 276, 263, 122, 48, 110, 16, 119, 130, 297, 82, 213, 218, 106, 233, 241, 64, 165, 13, 195, 225, 32, 65, 138, 66, 75, 31, 41, 29, 252, 136, 116, 61, 277, 164, 88, 205, 296, 247, 45, 132, 11, 238, 80, 163, 177, 260, 259, 287, 78, 42, 84, 269, 189, 115, 126, 281, 7, 185, 206, 6, 273, 35, 28, 102, 257, 191, 170, 184, 254, 125, 159, 135, 256, 19, 246, 243, 74, 299, 240, 209, 188, 294, 20, 186, 127, 197, 167, 114, 140, 292, 87, 193, 155, 245, 117, 95, 228, 162, 3, 275, 131, 222, 59, 234, 124, 145, 255, 76, 57, 90, 94, 38, 220, 44, 134, 219, 201, 290, 104, 68, 37, 248, 253, 267, 55, 10, 96, 216, 100, 207, 26, 264, 146, 70, 128, 21, 22, 101, 232, 30, 49, 18, 230, 15, 5, 298, 23, 192, 169, 285, 17, 43, 175, 168, 278, 139, 295};

        System.out.println(random.getElements());
        for (Integer el:random.getElements()) {
            assertTrue(tree.add(el));
            assertTrue(tree.contains(el),("should contain "+ el));
        }
    }

    @Test
    void removeRandom(){
        SearchTree<Integer>tree = createTree();

        Sampler random = new Sampler(new Random(),30);
        //ArrayList<Integer> random= new ArrayList<>(Arrays.asList(3,5,6,13,0,8,16,7,10,4,18,17,14,11,15,2,19,9,1,12));
        for (Integer el : random.getElements()) {
            assertTrue(tree.add(el), String.format("should change when adding %d", el));
        }

        for (Integer el : random.getElements()) {
            assertTrue(tree.contains(el), String.format("should contain %d", el));
            assertTrue(tree.remove(el), String.format("should change when removing %d", el));
            assertFalse(tree.contains(el), String.format("should not contain %d anymore", el));
        }
        assertEquals(0, tree.size(), "should be empty");
    }
}
