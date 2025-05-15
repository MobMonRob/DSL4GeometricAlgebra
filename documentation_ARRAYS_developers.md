# Arrays
Arrays are used to store multiple multivectors, which can be accessed and changed by addressing them with their indices, inside of a single variable. The arrays are implemented using [`MultivectorSymbolicArray`](https://github.com/orat/GACalcAPI/blob/master/src/main/java/de/orat/math/gacalc/api/MultivectorSymbolicArray.java). This documentation is aimed at developers and advanced users of the DSL. For a more user-centric guide on how to use arrays when writing programs for the DSL, it is recommended to refer to the [user documentation](documentation_ARRAYS_users.md).

# Grammar
The usage of arrays is described in the [user documentation](documentation_ARRAYS_users.md) and the grammar can be formally found in different parts of the [ANTLR Parser File](DSL4GA_Common/src/main/antlr4/de/dhbw/rahmlab/dsl4ga/common/parsing/GeomAlgeParser.g4).

# Implementation
Since, unlike loops for example, arrays are just a data type, they don't have a separate Transform Class. Instead, they are implemented completely in [`FuncTransform.java`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java) and [`ExprTransform.java`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/ExprTransform.java)

## Detecting an array
>**_Summary:_** The difference between accesses to arrays and multivectors on a grammar/context level is that the contexts which can contain these accesses have an `array` member of type [`ArrayIndexContext`](#indexcalculation). For multivectors, this context is `null`, while for arrays, it contains the information on which element of the array is being accessed. For references to arrays (as opposed to previously accessing specific elements), no such differentiation can be made since, on grammar level, no index is being supplied here. For this case, all transform classes track their multivector and array variables in separate hash maps.

### Detecting an array during assignment
[Grammar for functions](DSL4GA_Common/src/main/antlr4/de/dhbw/rahmlab/dsl4ga/common/parsing/GeomAlgeParser.g4): 
```ANTLR
stmt
	:	SPACE* vizAssigned=vizAssignedR SPACE* ASSIGNMENT [...] 								# AssgnStmt
	| 	SPACE* assigned=IDENTIFIER SPACE* L_EDGE_BRACKET R_EDGE_BRACKET SPACE* ASSIGNMENT [...] # ArrayInitStmt
	;
```

Because of the syntactical differences between the creation of multivectors and arrays, a differentiation between them is straightforward. Since they have the `#AssgnStmt` and `#ArrayInitStmt` labels respectively, their creation can be handled in different functions of [FuncTransform](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java). The [`enterArrayInitStmt`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java) function handles the initialization of arrays and saves their array objects and variable names in the `localArrays` hash map. This hash map is used to make sure that no variable is being overwritten and to be able to differentiate between variable names representing multivectors and variable names representing arrays. It is also passed over to [`EpxrTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/ExprTransform.java) via the `EpxrTransform.updateArrays()` function and to [`LoopTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java) in its `LoopTransform.generate()` function, so they have access to the same information.

>**_NOTE:_** Currently, creating new arrays in loops is syntactically not allowed (see [the loops documentation](documentation_LOOPS_developers.md)). If that were to change, a similar differentiation would have to be made in [`LoopTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/LoopTransform.java).

### Detecting an array access
**Array accesses on the right side of the assignment** (right of the `=`) can also be handled straightforward because of their separate `arrayAccessExpr` [parser rule](DSL4GA_Common/src/main/antlr4/de/dhbw/rahmlab/dsl4ga/common/parsing/GeomAlgeParser.g4). Referencing the variable name of an array without an index is not allowed semantically but can't be suppressed syntactically, which is why [`EpxrTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/ExprTransform.java).`exitVariableReference()` only checks for multivector variables and throws an exception if it detects an array's variable name (using the [afforementioned](#detecting-an-array-during-assignment) hash map, which is filled during assignment).

**Array accesses on the left side of the assignment** (left of the `=`) can't be differentiated as easily, because they are both contained in the following [parser rule](DSL4GA_Common/src/main/antlr4/de/dhbw/rahmlab/dsl4ga/common/parsing/GeomAlgeParser.g4):
```ANTLR
vizAssignedR
	: viz+=COLON? viz+=COLON? assigned=(IDENTIFIER|LOW_LINE)
	| viz+=COLON? viz+=COLON? assigned=IDENTIFIER SPACE* array=arrayIndex
	;
```
<details>
<summary>Notes</summary>

>_Two seperate rules are being used here, because else `LOW_LINE arrayIndex` would be allowed syntactically._

>_Array visualization is not implemented (yet)._
</details>

The way multivectors and array accesses are differentiated here is by checking if the `array` member of the `VizAssignedRContext` is `null`, in which case it is a multivector, or if it contains an `ArrayIndexContext`, which in turn has an [`IndexCalcContext`](#indexcalculation) as member. That context is then being used to determine which array element is being accessed. Similar to the `exitVariableReference`, references to array names without indices can't be suppressed syntactically, but are suppressed semantically by checking the `localArrays` hash map.  

## Creating and modifying arrays
As mentioned in the [user documentation](documentation_ARRAYS_users.md), arrays are not created with a fixed size. Instead, you can choose to supply initial values or omit them. In the implementation, this is handled by using [`MultivectorSymbolicArray`](https://github.com/orat/GACalcAPI/blob/master/src/main/java/de/orat/math/gacalc/api/MultivectorSymbolicArray.java), which is an extension of `java.util.ArrayList`. When [accessing](#detecting-an-array-access) an array element as an assignment (array access left of the `=`), [`FuncTransform`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/FuncTransform.java).`setArrayElement()` is called. This function determines whether the operation expands the given array, which means that `ArrayList.add()` is being used, or if it references an existing index, in which case `ArrayList.set()` is being used. The index to determine this is always being calculated using the [`IndexCalculation`](#indexcalculation) class.

## Accessing arrays
As described [above](#detecting-an-array), only individual elements of arrays can be accessed and never the whole array. This is due to the immutability and therefore implicit strict typing of the language. The only types of interaction with arrays are consequently accessing an element by [referencing its index](#indexcalculation) or by using their [built-in length function](#length-function) in indices and loop parameters.

### IndexCalculation
The [`IndexCalculation`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/IndexCalculation.java) class is used in array indices and [loop parameters](documentation_LOOPS_developers.md) to ensure valid expressions and correct interpretations of them. [Syntactically](DSL4GA_Common/src/main/antlr4/de/dhbw/rahmlab/dsl4ga/common/parsing/GeomAlgeParser.g4), an index can either be an integer literal, a [built-in length function of an array](#length-function) (or, [in loops, the iterator variable](documentation_LOOPS_developers.md)), or a simple addition or subtraction between the two:
```ANTLR
indexCalc
	: (minus=HYPHEN_MINUS)? SPACE* integer=INTEGER_LITERAL	
	| id=IDENTIFIER			
	| id=IDENTIFIER (op=PLUS_SIGN|op=HYPHEN_MINUS) SPACE* integer=INTEGER_LITERAL 
	| len=LENGTH_INDICATOR SPACE* L_PARENTHESIS SPACE* id=IDENTIFIER SPACE* R_PARENTHESIS SPACE* ((op=PLUS_SIGN|op=HYPHEN_MINUS) SPACE* (minus=HYPHEN_MINUS)? SPACE* integer=INTEGER_LITERAL)? 
	;
```
This limitation is due to the fact that only integer literals, [loop iterators](documentation_LOOPS_developers.md) and [length functions](#length-function) can actually be interpreted as integers and all other expressions are handled, stored and interpreted as multivectors (or arrays of multivectors).

### Length function
As defined by the [grammar for the `IndexCalculation`](#indexcalculation), arrays have a built-in length function which returns the size of the [`MultivectorSymbolicArray`](https://github.com/orat/GACalcAPI/blob/master/src/main/java/de/orat/math/gacalc/api/MultivectorSymbolicArray.java) and is implemented in [`IndexCalculation`](DSL4GA_Impl_Fast/src/main/java/de/dhbw/rahmlab/dsl4ga/impl/fast/parsing/astConstruction/_utils/IndexCalculation.java).`calculateIndex()`. **At the moment, the length function can only be accessed inside of the `IndexCalcContext`**.
