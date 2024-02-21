package de.no3x.util.function.samples.ui;

import com.google.common.graph.Network;
import de.no3x.util.function.NestedFunction;
import de.no3x.util.function.testcommon.Branch;
import de.no3x.util.function.testcommon.Leaf;
import de.no3x.util.function.testcommon.Tree;
import edu.uci.ics.jung.layout.algorithms.FRLayoutAlgorithm;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultNodeLabelRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class GraphUI extends JPanel {

    private static final long serialVersionUID = -4332663871914930864L;

    private final transient Network<Tree, Integer> graph;

    /** the visual component and renderer for the graph */
    VisualizationViewer<Tree, Integer> vv;

    public GraphUI(Network<Tree, Integer> graph) {
        this.graph = graph;
        setLayout(new BorderLayout());

        FRLayoutAlgorithm<Tree> layoutAlgorithm = new FRLayoutAlgorithm<>();
        layoutAlgorithm.setMaxIterations(100);
        vv = new VisualizationViewer<>(graph, layoutAlgorithm, new Dimension(400, 400));

        vv.getRenderContext()
                .setEdgeDrawPaintFunction(
                        new PickableEdgePaintFunction<>(vv.getPickedEdgeState(), Color.black, Color.cyan));

        vv.setBackground(Color.white);

        vv.getRenderContext().setNodeLabelRenderer(new DefaultNodeLabelRenderer(Color.cyan));
        vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));

        final Function<Tree, Paint> nodeFillPaintFunction = NestedFunction
                .of(Tree::getBranch)
                .nested(Branch::getLeaf)
                .either( Leaf::isGreen, it ->
                        it
                        .isTrue(Color.GREEN)
                        .isFalse(Color.BLUE));

        vv.getRenderContext().setNodeFillPaintFunction(nodeFillPaintFunction);

        // add a listener for ToolTips
        vv.setNodeToolTipFunction(Object::toString);
        vv.setEdgeToolTipFunction(Object::toString);
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        add(panel);

        final DefaultModalGraphMouse<Number, Number> graphMouse = new DefaultModalGraphMouse<>();
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(e -> scaler.scale(vv, 1.1f, vv.getCenter()));
        JButton minus = new JButton("-");
        minus.addActionListener(e -> scaler.scale(vv, 1 / 1.1f, vv.getCenter()));

        JComboBox<ModalGraphMouse.Mode> modeBox = graphMouse.getModeComboBox();
        JPanel modePanel = new JPanel();
        modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        modePanel.add(modeBox);

        JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        controls.add(scaleGrid);
        controls.add(modePanel);
        add(controls, BorderLayout.SOUTH);
    }

}
