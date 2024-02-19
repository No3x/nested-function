# NestedFunction

## Chaining
You can create a NestedFunction using the static method `of`, which wraps a regular Function.
Then you can chain NestedFunctions together using the nested method:

```java
NestedFunction<Integer, String> function1 = NestedFunction.of(num -> String.valueOf(num * 2));
NestedFunction<String, Integer> function2 = NestedFunction.of(str -> Integer.parseInt(str) + 1);
NestedFunction<Integer, Integer> chainedFunction = function1.nested(function2);
Integer result = chainedFunction.apply(5); // result will be 11
```
But there is no benefit to it. You can archive the same with `Function#andThen` already. So please see the other samples.

## Chaining with Predicates
You can chain a NestedFunction with a Predicate using the `then` method:

```java
Predicate<World> treeHasBranchAndHasGreenLeaf = NestedFunction.of(World::getTree)
    .nested(Tree::getBranch)
    .nested(Branch::getLeaf)
    .then(Leaf::isGreen);

assertThat(treeHasBranchAndHasGreenLeaf).accepts(world);
```
## Caching Results
You can cache results of a NestedFunction using the `cached` method:

### As result cache
```java
NestedFunction<Integer, Integer> function = NestedFunction.of(num -> num * 2);
Function<Integer, Integer> cachedFunction = function.cached(num -> expensiveOperation(num));
final List<Integer> collect = Stream.of(2, 4, 5, 6, 2).map(cachedFunction).collect(Collectors.toList());

System.out.println(collect);

private static int expensiveOperation(Integer num) {
    System.out.println("not cached");
    return num * 2;
}

Prints
not cached
not cached
not cached
not cached
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
NestedFunction<Integer, Integer> function = NestedFunction.of(num -> num * 2);
Function<Integer, Boolean> cachedFunction = function.cached(num -> expensiveOperation(num));

final List<Integer> collect = Stream.of(2, 4, 5, 6, 2).filter(cachedFunction::apply).collect(Collectors.toList());

System.out.println(collect);

private static boolean expensiveOperation(Integer num) {
    System.out.println("not cached");
    return num % 2 == 0;
}
Prints
not cached
not cached
not cached
not cached
[2, 4, 5, 6, 2]
```
