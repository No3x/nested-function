# NestedFunction

Simple extension of `Function` to express chained (or nested) `Functions` fluently. Nested means a function is dependent on the output of the previous function.

## Chaining
You can create a `NestedFunction` using the static method `of`, which wraps a regular Function.
Then you can chain `NestedFunction`s together by using the `nested` method:

```java
NestedFunction<Integer, String> function1 = NestedFunction.of(num -> String.valueOf(num * 2));
NestedFunction<String, Integer> function2 = NestedFunction.of(str -> Integer.parseInt(str) + 1);
NestedFunction<Integer, Integer> chainedFunction = function1.nested(function2);
Integer result = chainedFunction.apply(5); // result will be 11
```
But there is no benefit to it. You can archive the same with `Function#andThen` already. So please see the other samples.

## Chaining with Predicates
You can create a Predicate of a `NestedFunction` by using the `predicate` method:

```java
Predicate<World> treeHasBranchAndHasGreenLeaf = NestedFunction
    .of(World::getTree)
    .nested(Tree::getBranch)
    .nested(Branch::getLeaf)
    .predicate(Leaf::isGreen);

assertThat(treeHasBranchAndHasGreenLeaf).accepts(world);
```
## Chaining with Caching Results
You can cache results of a `NestedFunction` by using the `cached` method:

### As result cache

```java
NestedFunction<Integer, Integer> function = NestedFunction.of(num -> num * 2);
Function<Integer, Integer> cachedFunction = function.cached(num -> expensiveOperation(num));
final List<Integer> collect = Stream.of(2, 4, 5, 6, 2).map(cachedFunction).collect(Collectors.toList());

System.out.println(collect);

private static int expensiveOperation(Integer num) {
    final int result = num * 2;
    System.out.println(result + " not cached");
    return result;
}

Prints
8 not cached
16 not cached
20 not cached
24 not cached
[8, 16, 20, 24, 8]
```
Another useful sample is when calling an external webservice where you know it will return the same response per `Type`.
```java
final Function<MyModel, Boolean> cached = NestedFunction.of(MyModel::getType).cached(service::callWebservice);

Stream<MyModel> filtered = stream.filter(cached::apply);

assertThat(filtered).extracting(MyModel::getType).containsOnly(MyType.B);
```
### As Filter
```java
NestedFunction<Integer, Integer> function = NestedFunction.of(num -> num * 3);
Function<Integer, Boolean> cachedFunction = function.cached(num -> expensiveOperation(num));

final List<Integer> collect = Stream.of(2, 4, 5, 6, 2).filter(cachedFunction::apply).collect(Collectors.toList());

System.out.println(collect);

private static boolean expensiveOperation(Integer num) {
    final boolean result = num % 2 == 0;
    System.out.println(result + " not cached");
    return result;
}
Prints
true not cached
true not cached
false not cached
true not cached
[2, 4, 6, 2]
```
