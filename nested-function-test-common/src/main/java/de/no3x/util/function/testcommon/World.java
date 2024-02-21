package de.no3x.util.function.testcommon;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class World {
    private Tree tree;

    public static AddTree builder() {
        return World::new;
    }

    @FunctionalInterface
    public interface AddTree {
        World withTree(Tree tree);
    }

}
