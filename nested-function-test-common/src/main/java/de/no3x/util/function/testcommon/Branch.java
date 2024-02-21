package de.no3x.util.function.testcommon;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Branch {
    private Leaf leaf;

    public static AddLeaf builder() {
        return Branch::new;
    }
    @FunctionalInterface
    public interface AddLeaf {
        Branch withLeaf(Leaf leaf);
    }
}
