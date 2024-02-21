package de.no3x.util.function.testcommon;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Leaf {
    private final boolean isGreen;

    public static IsGreen builder() {
        return Leaf::new;
    }

    @FunctionalInterface
    public interface IsGreen {
        Leaf isGreen(boolean isGreen);
    }
}
