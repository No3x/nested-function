import de.no3x.util.function.NestedFunction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Tests {

    @Test
    void test() {
        NestedFunction<Integer, String> function1 = NestedFunction.of(num -> String.valueOf(num * 2));
        NestedFunction<String, Integer> function2 = NestedFunction.of(str -> Integer.parseInt(str) + 1);
        NestedFunction<Integer, Integer> chainedFunction = function1.nested(function2);

        Integer result = chainedFunction.apply(5);

        assertThat(result).isEqualTo(11);
    }
}
