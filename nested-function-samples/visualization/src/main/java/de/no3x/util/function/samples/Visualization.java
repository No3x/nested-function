package de.no3x.util.function.samples;

import com.google.common.graph.Network;
import de.no3x.util.function.samples.ui.GraphUI;
import de.no3x.util.function.samples.util.GraphWrapper;
import de.no3x.util.function.testcommon.Branch;
import de.no3x.util.function.testcommon.Leaf;
import de.no3x.util.function.testcommon.Tree;

import javax.swing.*;
import java.awt.*;


public class Visualization {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        content.add(new GraphUI(createGraph()));
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * create some nodes
     *
     * @return the Nodes in an array
     */
    private static Network<Tree, Integer> createGraph() {
        final GraphWrapper<Tree> graphWrapper = new GraphWrapper<>();
        Tree t1 = Tree.builder().withBranch(Branch.builder().withLeaf(Leaf.builder().isGreen(true)));
        Tree t2 = Tree.builder().withBranch(Branch.builder().withLeaf(Leaf.builder().isGreen(false)));
        graphWrapper.addNode(t1);
        graphWrapper.addNode(t2);

        return graphWrapper.getGraph();
    }
}
