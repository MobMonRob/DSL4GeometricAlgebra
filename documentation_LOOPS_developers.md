# Loops
Loops are used to iterate through a code segment multiple times. Their execution is implemented in two ways, with one approach being [`GACalcAPI`-specific](#gacalcapi-method) and one being the [broader, "catch-all" approach](#java-native-method). This documentation is aimed at developers and advanced users of the DSL. For a more user-centric guide on how to use loops when writing programs for the DSL, it is recommended to refer to the [user documentation](documentation_LOOPS_users.md).

# Grammar
The usage of loops is described in the [user documentation](documentation_LOOPS_users.md) and the grammar can be formally found in the ["Loop" section of the ANTLR Parser File](DSL4GA_Common/src/main/antlr4/de/dhbw/rahmlab/dsl4ga/common/parsing/GeomAlgeParser.g4#L126).

# Implementation
> **_Summary:_** Loops are implemented in the [`LoopTransform.java`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java) (for [first parsing](#first-parsing-looptranform) and [execution](#executing-the-loop)) and [`LoopAPITransform.java`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopAPITransform.java) (for [optional second parsing for `GACalcAPI` execution](#gacalcapi-method)) classes. [`LoopTransform`.`generate()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L62) is called in [`FuncTransform`.`enterLoopStmt()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java#L152), so whenever a loop is being created in a function. [`LoopTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java) determines whether the loop can be solved using a [`GACalcAPI`](#gacalcapi-method)-specific or a ["catch-all"](#java-native-method) method. 

## The different execution methods
To understand the different stages of parsing, it is important to note that there are two different methods of executing loops. The [Java-native method](#java-native-method) uses a normal Java loop and can handle all syntactically correct loops. However, a loop execution method which is more specific to geometric algebra is provided by the [`GACalcAPI`](#gacalcapi-method). Because of its specificity, the latter option should always be preferred and the [native method](#java-native-method) only be used as fallback solution for unsupported cases. These unsupported cases are described in the section on [deciding the method of execution](#deciding-the-method-of-execution).

The [`GACalcAPI` method](#gacalcapi-method) utilizes one of three functions, depending on the type of loop: 

<table><thead>
<tr>
<th>Function</th>
<th>Description</th>
<th>Examples</th>
</tr></thead>
<tbody>
<tr>
<td>

#### `mapaccum()`
</td>
<td>

Used if there are dependencies between iterations (one iteration influences the next iteration). It returns all intermediate results, which is useful for array operations.</td>
<td>

```
for (i; 0; 3; 1){
    a[i+1] = a[i] + 1 // a[i+1] changes the following value of a
}
```

</td>
</tr>
<tr>
<td>

#### `fold()`
</td>
<td>

Like mapaccum, except that intermediate results are not calculated/returned, which is useful for multivector variables.
</td>
<td>

```
for (i; 0; 3; 1){
    v = v + 1 // v depends on the previous iteration
}
```

</td>
</tr>
<tr>
<td>

#### `map()`
</td>
<td>
	
Used if the calculations between iterations are independent of each other.</td>
<td>

```
for (i; 0; 3; 1){
    a[i] = a[i] + 1 // a[i] depends only on the current value
}
```

</td>
</tr>
</tbody>
</table>

## First Parsing: [`LoopTranform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java)
The entrypoint to the first parsing is the call to [`LoopTransform`.`generate()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L62) by [`FuncTransform`.`enterLoopStmt()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java#L152). The function body is ignored in [`FuncTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java), because its `SkippingParseTreeWalker` is [instructed](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java#L44) to skip the `LoopBodyContext.class`, which contains the loop body. In [`LoopTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java) the loop body is then parsed for the first time, which has multiple purposes:
- To [validate](#validating-the-loop) only correct expressions are being used (and to throw descriptive expressions)
- To [decide](#deciding-the-method-of-execution) whether the [`GACalcAPI`](#gacalcapi-method)-specific method can be used
- To fill arrays and hash maps to [prepare](#preparing-the-execution) the execution stage

And finally to trigger the [execution of the loop](#executing-the-loop), using the determined method.

Like in [`FuncTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java) and [`ExprTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/ExprTransform.java), a `SkippingParseTreeWalker` is used to visit each of the contexts in the loop body. 

### Validating the loop
The validations during the first parsing take place in the [`generateLoopTransform()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L73), [`enterInsideLoopStmt()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L100) and [`enterArrayAccessExpr()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L138). 
- [`generateLoopTransform()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L73) created the `LoopTransform` object and handles the [loop parameters](documentation_LOOPS_users.md#loop-parameters), using [`addIterator()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L326) to parse the iterator variable and [`parseLoopParam()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L314) to parse the other loop parameters. The validation for the iterator variable consists of ensuring that no variable with the same name has been declared before. For the other parameters, the validation consits of ensuring that the none of the parameters is the iterator variable, since that could lead to a never-ending loop.
- [`enterInsideLoopStmt()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L100) is used to parse the left side of assignments in the loop body. Here, the validation consists of ensuring that only correct assignments are made, meaning that: No multivector variable is accessed as array variable (or vice versa), the iterator variable isn't reassigned and arrays are accessed correctly.
- [`enterArrayAccessExpr()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L138) is used to parse array accesses on the right side of assignments in the loop body. Here, it is validated that arrays are accessed correctly.


### Deciding the method of execution
As mentioned [above](#the-different-execution-methods), it is currently not always possible to use the [`GACalcAPI` method](#gacalcapi-method) to execute the loop. These cases are: 

<table><thead>
<tr>
<th>Case</th>
<th style="width:190px">Examples</th>
<th>Explanation</th>
<th>Notes for possible future implementation</th>
</tr></thead>
<tbody>
<tr>
<td>

#### [Loop step != +1](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L80)
</td>
<td>

1:
```
for (i; 0; 5; 2){
    [...]
}
```

2:
```
for (i; 5; 0; -1){
    [...]
}
```
</td>
<td>

The `GACalcAPI` functions don't get information about the loop parameters other than the number of iterations (see the [section on LoopAPITransform.handleArrayArgs()]).  </td>
<td>

This can be solved by further manipulating the arrays which are supplied to the functions: 
- Example 1: Drop every second element of array `a`.
- Example 2: Reverse array `a`.

It's then also important to adjust the [applyLoopResults() method] accordingly. It currently doesn't take the loop step into account.

</td>
<tr>
<td>

#### [Calculation != "+1" with iterator in array access index](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L139)
</td>
<td>

```
for (i; 2; 5; 1){
    a[i] = a[i+2] + x
    a[i-2] = a[i] + x
}
```
</td>
<td>

It's not possible to use the `GACalcAPI` functions if, on either side of the assignment, an array element is accessed dynamically via any expression other than `a[i]` or `a[i+1]`.  This is because [`mapaccum()`](#mapaccum) and [`fold()`](#fold) don't support calculations like these and instead always assume that one iteration only influences the one directly following it.

</td>
<td>
</td>
</tr>
</tr>
<tr>
<td>

#### [Assignment index is not the iterator]((DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L121))
</td>
<td>

```
a[] = {0, 1, 2}
for (i; 0; 2; 1){
    a[1] = foo()
    b[i] = a[i] + x
}
```

</td>
<td>

Changing one constant array element leads to problems if that element is then accessed in another line of the loop. The representation of `b[i] = a[i] + x` which would be given to the `GACalcAPI` function call would only contain the original values of `a`, because there isn't a way to represent the connection of `a[1]` as an array element of `a` and the two lines are treated as independent of each other.   
</td>
<td>

[The check for this case](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L118) is very broad and doesn't test if this array element is being used in another line. If, in the given example, the second line wasn't there or the first line was `a[2] = foo()` - which would reference an array element which wouldn't be accessed by `a[i]`, the `GACalcAPI` functions would return the correct result. It wouldn't be enough for this test to only look for accesses to "`a[1]`", it would also need to look for implicit references to `a[1]`, like for example "`a[len(a)-2]`".

It's also possible this is solvable by modifiying [`argsArray`](#argsarray) for the second line, but this approach hasn't been evaluated further than the initial idea.
</td>
</tr>
<tr>
<td>

#### [Constant array access to an array which is changed in the same loop]((DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L184))
</td>
<td>

```
for (i; 0; 3; 1){
    a[i] = a[0] + x
    v = a[0] + x
}
```
</td>
<td>

Accessing an array element of an array which is being changed in the same loop leads to a similar problem as in the previous case, since the representations of `a[i] = a[0] + x` and `v = a[0] + x` which would be given to the `GACalcAPI` function call can't reference that they are connected to the first line.</td>
<td>

[The check for this case](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L183) does test if the array is being changed in the loop (so it works without the first line), but beyond that, it's the same principle as with the previous case. 
</td>
</tr>
<tr>
<td>

#### [Nested loops](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L128)
</td>
<td>

```
for (i; 0; 3; 1){
    for (e; 0; 3; 1){
        [...]
    }
}
```
</td>
<td>

For nested loops, the outer loop needs to be executed natively. The inner loop "decides for itself" whether or not it needs to be executed natively (for more details, see [the section on native execution](#java-native-method)).
</td>
<td>

</td>
</tr>
</tbody>
</table>

To determine whether a loop has to be executed natively, `LoopTransform`'s `nativeLoop` flag is used. It's set to `false` when the `LoopTransform` object is created and set to `true` as soon as one of the cases is detected. The flag then [gets read before the loop is executed](#executing-the-loop). 

### Preparing the execution
As mentioned [above](#the-different-execution-methods), it is preffered to use the [`GACalcAPI` method](#gacalcapi-method) rather than the [native method](#java-native-method) for the loop execution. However, to be able to call the `GACalcAPI` functions, a special representation of the loop needs to be supplied using a number of arrays. In order to fill these arrays, further parsing is required. The [native method](#java-native-method) doesn't need this further parsing, so everything which is only related to the `GACalcAPI` functions is prefaced with `if (!nativeLoop)`, so these parts aren't executed unnecessarily. 

Because the representation for the [`GACalcAPI` method uses a second class to parse the loop again](#gacalcapi-method) and this second parsing needs a lot of the same information as the first parsing, this overlapping information is stored in [`LoopTransformSharedResources.java`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/LoopTransformSharedResources.java). To check for the native cases and prepare for the [execution](#gacalcapi-method), the following data structures are filled: 
<table><thead>
<tr>
<th>Variable</th>
<th>Type</th>
<th>Purpose</th>
<th>Contained in</th>
</tr></thead>
<tbody>
<tr>
<td colspan="4" style="text-align:center">

#### Arrays used to determine which execution method is going to be used 
</td>
</tr>
<tr>
<td>


#### `functionVariables`, `functionVariablesView`

#### `functionArrays` 
</td>
<td>

`Map<String, MultivectorSymbolic>`

`Map<String, MultivectorSymbolicArray>`
</td>
<td>

Contain the variables **which are inherited from [`FuncTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java).**
</td>
<td>

[`LoopTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java) and [`LoopTransformSharedResources`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/LoopTransformSharedResources.java)
</td>
</tr>
<tr>
<td>

#### `leftSideNames`
</td>
<td>

`Map<String, List<Integer>>`
</td>
<td>

Contains the names of all variables which are being assigned (_left of the assignment_) in the loop and the numbers of the lines they are being assigned in as `List<Integer>`.

This is used to keep track of which variables are being assigned at which point in order to be able to:
- [Check if a variable has been modified in the loop (_left of the assignment_) before or after it was accessed (_right of the assignment_)](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopAPITransform.java#L74)
- [Apply the loop result of the correct occurence of each variable](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L305)
- Check which variables have been modified in the loop
</td>
<td>

[`LoopTransformSharedResources`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/LoopTransformSharedResources.java)
</td>
</tr>

<tr>
<td>

#### `rightSideUniqueNames`
</td>
<td>

`Set<String>`
</td>
<td>

Contains the names of all variables which are being accessed (_right of the assignment_) before they are being reassigned (_left of the assignment_) in the loop. 

This is used to [detect which multivectors are being](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L190) [`accumulated`](#mapaccum).
</td>
<td>

[`LoopTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java)
</td>
</tr>
<tr>
<td>

#### `foldMVnames`
</td>
<td>

[`FoldSet`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/FoldSet.java)
</td>
<td>

Contains the names of all multivector variables which can be accumulated in [`fold`](#fold).

Variable names are added if they occur on the left side of the assignment and removed if they occur on the right side of the assignment after having occured on the left side.
</td>
<td>

[`LoopTransformSharedResources`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/LoopTransformSharedResources.java)</td>
</tr>
<tr>
<td>

#### `accumArrayNames`
</td>
<td>

`Set<String>`
</td>
<td>

Contains the names of all array variables which can be accumulated in [`mapaccum`](#mapaccum).

Variable names are added if they occur on the left side of the assignment with `i+1` in the array index and [removed if they aren't accessed (_on the right side of an assignment in the loop_)](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L190).
</td>
<td>

[`LoopTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java)
</td>
</tr>
<tr>
<td>

#### `accumMVnames`
</td>
<td>

`Set<String>`
</td>
<td>

Contains the names of all multivector variables which can be accumulated in [`mapaccum`](#mapaccum).

Variable names are added if they occur on the left side of the assignment and have previously been accessed (_on the right side of an assignment in the loop_). This is checked by utilizing the `Set` `actuallyUsedAccums`, which is a looser version of [`rightSideUniqueNames`](#rightsideuniquenames), because it contains all right side variables except if they are accessed and assigned in the same line, while [`rightSideUniqueNames`](#rightsideuniquenames) only contains variables which are accessed before they are reassigned.
</td>
<td>

[`LoopTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java)
</td>
</tr>
<tr>
<td colspan="4" style="text-align:center">

### Arrays directly used by the [`GACalcAPI` method](#gacalcapi-method)
_Examples can be found [here](https://github.com/orat/CGACasADi/blob/4dab0865fccbc0403ca54ebac3e7288e314017a4/CGACasADi/src/main/java/de/orat/math/cgacasadi/LoopsExample.java)._
</td>
</tr>
<tr>
<td>

#### `argsAccumInitial`,
#### `argsSimple`,
#### `argsArray` 

</td>
<td>

`List<MultivectorSymbolic>`,

`List<MultivectorSymbolic>`,


`List<MultivectorSymbolicArray>`
</td>
<td>

The `args` parameters are filled with the values of the loop which are already known before the loop is executed. For each, they are:
- `argsAccumInitial`: The initial values of the variables which are being accumulated using [`mapaccum`](#mapaccum) or [`fold`](#fold). For multivectors, the initial value of the multivector. For arrays, the value of the first array element which is being accessed. 
- `argsSimple`: The multivectors and array elements which are being used, but not reassigned, in the loop.
- `argsArray`: The initial values of the arrays which are being reassigned in the loop.
</td>
<td>

[`LoopTransformSharedResources`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/LoopTransformSharedResources.java)
</td>
</tr>
<tr>
<td>

#### `paramsAccum`,
#### `paramsSimple`,
#### `paramsArray` 

</td>
<td>

`List<MultivectorPurelySymbolic>`
</td>
<td>

The `params` parameters are filled with purely symbolic multivectors representations `args` parameters. These purely symbolic multivectors are used to represent relations of variables to each other without calculating their actual values. For each `args` parameter, a `params` parameter has to be supplied:
- `paramsAccum`: Purely symbolic representations of multivectors of variables which are being accumulated using [`mapaccum`](#mapaccum) or [`fold`](#fold).
- `paramsSimple`: Purely symbolic representations of the multivectors and array elements which are being used, but not reassigned, in the loop.
- `paramsArray`: Purely symbolic representations of the arrays which are being reassigned in the loop.
</td>
<td>

[`LoopTransformSharedResources`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/LoopTransformSharedResources.java)
</td>
</tr>
<tr>
<td>

#### `returnsAccum`,
#### `returnsArray` 

</td>
<td>

`List<MultivectorSymbolic>`
</td>
<td>

The `returns` parameters are filled with the representations of each line of the loop and [consist of](SECTION TO FILL THE ARRAYS) combinations of elements from the `params` arrays:
- `returnsAccum`: Contains the representations of all lines which affect the following iteration. 
- `returnsArray`: Contains the representations of all lines which don't affect the following iteration.
</td>
<td>

[`LoopTransformSharedResources`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/LoopTransformSharedResources.java)
</td>
</tr>
</tbody>
</table>

## Executing the loop
In [`LoopTransform`.`exitLoopBody`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L182), the actual execution of the loop is initiated. At this point, the [arrays used to determine which execution method is going to be used](#arrays-used-to-determine-which-execution-method-is-going-to-be-used) are completely filled and `nativeLoop` has its final value. If it's `true`, the [native method](#java-native-method) is called, if it's `false`, the [`GACalcAPI`](#gacalcapi-method) is used.

### `GACalcAPI` method
While the [arrays used to determine which execution method is going to be used](#arrays-used-to-determine-which-execution-method-is-going-to-be-used) are filled, the [arrays needed for the function calls](#arrays-directly-used-by-the-gacalcapi-method) aren't yet. To fill them, [`LoopAPITransform`.`generate()`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopAPITransform.java) is [called for each line of the loop](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java#L194).

#### [`LoopAPITransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopAPITransform.java)
LoopAPITransform performs a second parsing of the loop, this time to fill up the [arrays needed for the function calls](#arrays-directly-used-by-the-gacalcapi-method). 


### Java-native method

