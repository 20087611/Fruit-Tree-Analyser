package com.recognition;
import com.recognition.util.TreeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TreeUtilTest {
    TreeUtil treeUtil;

    @BeforeEach
    void createFruitArray() {
        treeUtil = new TreeUtil(3, 3);
        treeUtil.pixelArray[0] = -1;
        treeUtil.pixelArray[1] = 1;
        treeUtil.pixelArray[2] = 2;
        treeUtil.pixelArray[3] = 3;
        treeUtil.pixelArray[4] = 4;
        treeUtil.pixelArray[5] = -1;
        treeUtil.pixelArray[6] = -1;
        treeUtil.pixelArray[7] = -1;
        treeUtil.pixelArray[8] = -1;
    }

    @Test
    void addFruitToArray() {
        treeUtil.addFruitToArray(2, 1);
        assertEquals(5, treeUtil.pixelArray[treeUtil.calculateArrayPosition(1, 2)], "addFruitToArray() not adding fruits to array correctly.");
    }

    @Test
    void addNonFruitToArray() {
        treeUtil.addNonFruitToArray(2, 1);
        assertEquals(-1, treeUtil.pixelArray[treeUtil.calculateArrayPosition(1, 2)], "addNonFruitToArray() not adding fruits to array correctly!");
    }

    @Test
    void calculateArrayPosition() {
        assertEquals(11, treeUtil.calculateArrayPosition(3, 2), "calculateArrayPosition() calculation not correct!");
    }

    @Test
    void unionFruitPixels() {
        treeUtil.unionFruitPixels();
        assertEquals(1, treeUtil.pixelArray[3], "unionFruitPixels() not union-ing same-set pixels correctly!");
    }

    @Test
    void pixelIsWhite() {
        assertTrue(treeUtil.pixelIsWhite(4), "pixelIsWhite() is not recognising white pixels correctly!");
    }

    @Test
    void atTopEdge() {
        assertTrue(treeUtil.atTopEdge(1), "atTopEdge() is not recognizing a pixel at the top edge of an image (2D array) correctly");
    }

    @Test
    void atRightEdge() {
        assertTrue(treeUtil.atRightEdge(5), "atRightEdge() is not recognizing a pixel at the right edge of an image (2D array) correctly");
    }

    @Test
    void atBottomEdge() {
        assertTrue(treeUtil.atBottomEdge(7), "atBottomEdge() is not recognizing a pixel at the bottom edge of an image (2D array) correctly");
    }

    @Test
    void atLeftEdge() {
        assertTrue(treeUtil.atLeftEdge(3), "atLeftEdge() is not recognizing a pixel at the left edge of an image (2D array) correctly");
    }

    @Test
    void unionPixels() {
        treeUtil.unionPixels(1,4);
        assertEquals(1, treeUtil.pixelArray[4], "unionPixels() is not unioning two pixels correctly!");
    }

    @Test
    void quickUnion() {
        treeUtil.quickUnion(treeUtil.pixelArray, 1,2);
        assertEquals(1, treeUtil.pixelArray[2], "quickUnion() is not working correctly!");
    }

    @Test
    void find() {
        treeUtil.unionFruitPixels();
        assertEquals(1, treeUtil.find(treeUtil.pixelArray, 4), "find() is not correctly finding a disjoint set's parent node!" );
    }
}