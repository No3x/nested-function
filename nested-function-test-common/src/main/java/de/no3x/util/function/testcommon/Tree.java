package de.no3x.util.function.testcommon;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tree {
    private Branch branch;

    public static AddBranch builder() {
        return Tree::new;
    }

    @FunctionalInterface
    public interface AddBranch {
        Tree withBranch(Branch branch);
    }
}
