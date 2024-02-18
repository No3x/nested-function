package de.no3x.util.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NestedFunctionTest {

    record World(Tree tree) {

        static AddTree builder() {
            return World::new;
        }

        interface AddTree {
            World withTree(Tree tree);
        }

    }

     record Tree(Branch branch) {

        static AddBranch builder() {
            return Tree::new;
        }

        interface AddBranch {
            Tree withBranch(Branch branch);

            default Tree withoutBranch() {
                return new Tree(null);
            }
        }
    }

    record Branch(Leaf leaf) {

        static Branch.AddLeaf builder() {
            return Branch::new;
        }

        interface AddLeaf {
            Branch withLeaf(Leaf leaf);
        }
    }


    record Leaf(boolean isGreen) {

            static Leaf.IsGreen builder() {
                return Leaf::new;
            }

            interface IsGreen {
                Leaf isGreen(boolean isGreen);
            }
        }

    @Nested
    class Simple {
        @Test
        void simpleTwoLevel_greenLeaf_accepts() {
            final Branch branch = Branch.builder().withLeaf(Leaf.builder().isGreen(true));

            Predicate<Branch> branchHasGreenLeaf = NestedFunction.of(Branch::leaf).then(Leaf::isGreen);

            assertThat(branchHasGreenLeaf).accepts(branch);
        }

        @Test
        void simpleTwoLevel_noGreenLeaf_rejects() {
            final Branch branch = Branch.builder().withLeaf(Leaf.builder().isGreen(false));

            Predicate<Branch> branchHasGreenLeaf = NestedFunction.of(Branch::leaf)
                    .then(Leaf::isGreen);

            assertThat(branchHasGreenLeaf).rejects(branch);
        }
    }

    @Nested
    class Complex {

        final Tree tree = Tree.builder().withBranch(Branch.builder().withLeaf(Leaf.builder().isGreen(true)));

        @Test
        void complexThreeLevel_OptionalReferenceImpl_greenLeaf_accepts() {

            Predicate<Tree> treeHasBranchAndHasGreenLeafOptional = (_tree) -> Optional.of(_tree.branch()).map(Branch::leaf).stream().anyMatch(Leaf::isGreen);

            assertThat(treeHasBranchAndHasGreenLeafOptional).accepts(tree);
        }

        @Test
        void complexThreeLevel_greenLeaf_accepts() {

            Predicate<Tree> treeHasBranchAndHasGreenLeaf = NestedFunction.of(Tree::branch)
                    .nested(Branch::leaf)
                    .then(Leaf::isGreen);

            assertThat(treeHasBranchAndHasGreenLeaf).accepts(tree);
        }

        @Test
        void complexThreeLevel_nullBranch_rejects() {
            final Tree tree = Tree.builder().withoutBranch();

            Predicate<Tree> treeHasBranchAndHasGreenLeaf = NestedFunction.of(Tree::branch)
                    .nested(Branch::leaf)
                    .then(Leaf::isGreen);

            assertThat(treeHasBranchAndHasGreenLeaf).rejects(tree);
        }

        @Test
        void complexThreeLevel_nullLeaf_rejects() {
            final Tree tree = Tree.builder().withBranch(null);

            Predicate<Tree> treeHasBranchAndHasGreenLeaf = NestedFunction.of(Tree::branch)
                    .nested(Branch::leaf)
                    .then(Leaf::isGreen);

            assertThat(treeHasBranchAndHasGreenLeaf).rejects(tree);
        }

        @Test
        void complexThreeLevel_worldGreenLeaf_accepts() {
            final World world = World.builder()
                    .withTree(Tree.builder()
                            .withBranch(Branch.builder()
                                    .withLeaf(Leaf.builder().isGreen(true))));

            Predicate<World> treeHasBranchAndHasGreenLeaf = NestedFunction.of(World::tree)
                    .nested(Tree::branch)
                    .nested(Branch::leaf)
                    .then(Leaf::isGreen);

            assertThat(treeHasBranchAndHasGreenLeaf).accepts(world);
        }
    }

    @Nested
    class Cached {

        abstract static class Service {
            abstract boolean isRelevant(MyType type);
        }

        enum MyType {A, B, C}

        record MyModel(MyType type) {
        }

        @Mock
        private Service service;

        @BeforeEach
        public void setup() {
            when(service.isRelevant(any(MyType.class))).thenAnswer(invocationOnMock -> {
                final MyType type = invocationOnMock.getArgument(0);
                System.out.println("Service called for type " + type);
                return switch (type) {
                    case A, C -> false;
                    case B -> true;
                };
            });
        }

        @Test
        void simple_cached_callsFunctionOnlyOncePerType() {
            final Stream<MyModel> stream = Stream.of(new MyModel(MyType.A), new MyModel(MyType.B), new MyModel(MyType.B));

            final Function<MyModel, Boolean> cached = NestedFunction.of(MyModel::type).cached(service::isRelevant);
            Stream<MyModel> filtered = stream.filter(cached::apply);

            assertThat(filtered).extracting(MyModel::type).containsOnly(MyType.B);
            // Stream operations are done at this point, therefore verify after assertions
            verify(service).isRelevant(MyType.A);
            verify(service).isRelevant(MyType.B);
            verify(service, never()).isRelevant(MyType.C);
        }
    }
}