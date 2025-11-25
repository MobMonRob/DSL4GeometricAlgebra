# DSL4GA
This repository contains code to work with multivector expressions of geometric algebra. The idea is to realise a complete toolchain with a geometric algebra specific domain specific language based on [Truffle/Graal](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/) with state of the art smart editing features, debugging functionality and a fast implementation based on [JCasADi](https://github.com/MobMonRob/JCasADi/) focussed on bringing algorithmic differentation in the world of geometric algebra.


## Disclaimer
The current state of the project is a proof of concept, so it is not advised to use it in real world applications. If you have feedback or feature suggestions, please create a new GitHub Issue.

Especially be cautious regarding:
- the documentation may not be up to date.
- breaking changes can occur at any time.


## GraalVM Setup
Download [GraalVM (23.1.2) for JDK 21 Community 21.0.2](https://github.com/graalvm/graalvm-ce-builds/releases/tag/jdk-21.0.2) (Linux (amd64), Java21).

Extract the downloaded archive to an arbitrary location.


### Netbeans configuration
Add a new java platform with the name "GraalVM". \
If you use Netbeans IDE with newer version than 21 you have to start the IDE with JDK 21 to be able to build because the build configuration is configured to use the default JDK which is the JDK the IDE is started or you can configure explicit the platform to build by following steps and further the project configuration steps:\
- open project properties via right-click on the project
- navigate to Build / Compile
- click "Manage Java Platforms..."

or navigate to this point via the Tools main menu.

- click "Add Platform..."
- In the poping-up wizard:
  - select platform type "Java Standard Edition"
  - choose the platform folder within the extracted archive.
  - name it "GraalVM"


### Netbeans project configuration
- open project properties via right-click on the project
- navigate to Build / Compile
- in the drop-down list labeled "Java Platform" choose "GraalVM"


## GraalVM Update
Redo the [GraalVM Setup](#graalvm-setup) with the new version. If you use same path and foldername as before, you can skip the step [Netbeans configuration](#netbeans-configuration).

If you are the first collaborator updating to a new version, you also need to
- update the download link [in this documentation](#graalvm-setup).
- update the `graalvm.version` property in the [pom.xml of the DSL4GA__Parent](pom.xml). Note, that it differs from the JDK Version. "Language runtimes" is the keyword for it in the text of the download page.
- try building the project and fix broken code.


## Dependencies Setup
The project depends on the vecmath library in the refactored version of the JogAmp Community. Your can find this library [here](https://jogamp.org/deployment/java3d/1.7.1-build-20200222/vecmath.jar). Unfortunately there is no maven repository available. That is why you need to download the jar file manually and add it as a local depency of the project. To do this in the nebeans ide: Right-click on the depencies of the project and add the dependency manually. The group id is "org.jogamp.java3d", the artifactId is "vecmath" and the type is "jar". \
Alternatively clone it from [GitHub](https://github.com/JogAmp/vecmath/tree/dev1.7.1), update the compiler version in it's pom.xml and build it.

Clone and checkout
1. [GeometricAlgebra](https://github.com/orat/GeometricAlgebra)
2. [ConformalGeometricAlgebra](https://github.com/orat/ConformalGeometricAlgebra)
3. [SparseMatrix](https://github.com/orat/SparseMatrix)
4. [CGACasADi](https://github.com/orat/CGACasADi)

and build those projects to have them available in your local Maven cache. SparseMatrix is a simple Java sparse matrix implementation used primarily as interface between the annotation based Java API and the DSL. So it allows to write code independend from GA-specific objects. CGACasADi is a fast symbolic implementation of CGA based on [CasADI](https://web.casadi.org/). A Java-Wrapper for CasADI based on [Swig](https://www.swig.org/) is used for Java integration.


## Run
In order to run the example invokation in the package 'de.dhbw.rahmlab.geomalgelang.App' make sure you successfully executed the steps [GraalVM Setup](#graalvm-setup) and [Dependencies Setup](#dependencies-setup) beforehand. \
If you use an IDE other than Netbeans and execute the generated .class files directly rather than the generated .jar file, it might be necessary to configure the Maven execution in your IDE with the same properties set in the [nbactions.xml](nbactions.xml) file.


## Annotation based API
An [annotation based API](DSL4GA_Annotation/README.md) useful especially for testing is available.

## Netbeans IDE Tooling

### Syntax-Highlighting
A Syntax-Highlighting plugin for the Netbeans-IDE can be found [here](https://github.com/orat/netbeans-ocga/tree/master).

### Insertion of special characters
A Netbeans-IDE plugin which adds a submenu into the context-menu of the editor to insert CGA-specific symbols and operators can be found [here](https://github.com/orat/netbeans-cgasymbols).

## Implementations
There are two implementations of the [API](DSL4GA_API):
- [Truffle](DSL4GA_Impl_Truffle), which will be optimized for a good development experience. **Use truffle by default.**
- [Fast](DSL4GA_Impl_Fast), which will be optimized for runtime -not parsing- performance.

Both implementations share that their initialization is time-consuming, but repeated invocations are executed fast. \
Their syntax will be the same in the longrun. However, some features will never be implemented in Fast. These are: visualization, debugger. \
Fast will be used to measure the runtime difference to Truffle if certain CasADi features are used which are incompatible with a smooth debugging experience.

**Fast is currently broken. Do not use it!**
- Recent changes in the grammar are not handled properly.
- Builtins and operators can be missing or wrong.
- Missing features: arrays, higher-order functions.


## Syntax
The first line needs to declare the algebra used. Optionally, the implementation can be specified, too. \
With algebra being "cga" and implementation being "theImpl", the first line would be:
```
#algebra cga theImpl
```


## Expressions
- Numeric literals like "0.5" and scalar constants like "π" are in OPNS representation.

## Function definitions
#### Rules
- There needs to be at least one function defined with the name `main`. Invokations of the program will call this one first.
- Currently, callees need to be defined above the callers.
- In consequence, `main` needs to be the last function defined.
- Function overloading is currently not supported and will lead to an exception.
- A function will return only the list in the last line of its definition.
- The return value of a call needs to be assigned to a variable.
- An assignment to multiple variables in the same line is possible if the right side consists only of a call.
- The count of the assigned variables must match the count of the result values of a call.
- With an assignment to "_", a return value can be discarded. This is only possible for calls which return at least two values.
- If the right side of an assignment is not only a call but a composed expression, within it are only calls allowed which return exactly one value.

#### Example
Custom functions can be defined like in the following example:
```
fn test(a) {
	a, 5
}

fn main(a, b) {
	_, c = test(b)
	a, b, c
}
```

## Arrays
```
// Parameter
fn callee(a[]) {
	// Return
	a, a
}

fn caller() {
	// Init
	a[] = {0, 1, 2, 3}

	// Argument
	// Multiple assignment
	b[], c[] = callee(a)

	// Slicing
	/// First index: Start inclusive
	/// Second index: End exclusive
	/// -1: last index of array
	d[] = a[1:-2] // {1}
	e[] = a[1:] // {1, 2, 3}

	// Access
	f = a[0] // 0
	g = a[-1] // 3

	// Reversal
	h[] = reversed(a) // {3, 2, 1, 0}

	// Concatenation
	i[] = concat(a, a) // {0, 1, 2, 3, 0, 1, 2, 3}
}
```


## Higher-order functions “HOF”
HOF are currently primarily used to express iteration. \
HOF currently cannot be defined in the language itself. Instead HOF builtins are provided.

#### Available HOF builtins
| HOF         | Explanation |
| :---------- | :---------- |
| map         | Execute its argument function and return all intermediate result values. |
| mapaccum    | Same as map, but accumulate on the first argument and result value of its argument function. |
| mapfold     | Same as mapaccum, but return only the last result values. |

#### Pseudocode signatures
- `map(Func<SimpleX... -> SimpleY...>, Array/Simple...) -> Tuple of Array of Simple`
- `mapaccum(Func<SimpleAcc, SimpleCurrent... -> SimpleAcc, SimpleOut...>, SimpleAccInit, Array/Simple...) -> Tuple of Array of Simple`
- `mapfold(Func<SimpleAcc, SimpleCurrent... -> SimpleAcc, SimpleOut...>, SimpleAccInit, Array/Simple...) -> Tuple`

#### Rules
- The first argument of the HOF builtins is always a function “the argument function” which receives values (not arrays) and returns values (not arrays).
- All the array arguments need to have the same size, that is the count of the elements of the respective array.
- The iteration count is equal to the size of all the arguments arrays.
- Simple value (not array) arguments will be repeated for each iteration.
- The HOF calls its argument function in each iteration with the array elements at the index equal to the iteration.
- mapaccum and mapfold only: each iteration depends on the previous iteration. To achieve this, a single accumulator variable is used. It has to be the first argument and the first return value of the argument function. The first value of the accumulator variable is the second argument of the respective HOF.

#### Examples
```
fn add(a, b) {
	a+b, a
}

fn main() {
	a[] = {0, 1, 2}

	b[], c[] = map(add, 1, a)
	// b[] = {1, 2, 3}
	// c[] = {1, 1, 1}

	e[], f[] = mapaccum(add, 1, a)
	// e[] = {1, 2, 4}
	// f[] = {1, 1, 2}

	g, h = mapfold(add, 1, a)
	// g = 4
	// f = 2
}
```


## Visualization
Variables can be visualized after assignment with one or two preceding colons.
- `:a` will assume **IPNS** representation.
- `::a` will assume **OPNS** representation.

After leaving a function, the visualizations done in it will be cleaned up. Visualizations from the calling function remain.

Example visualization syntax within context:
```
fn main(a, b) {
	:c = a
	::d = b
}
```

The following table shows which elements are visualized and in which colors. The color depends on the grade of the object.

| geometric object             | grade | color |
| :--------------------------- | ------| ----- |
| plane, round-point, sphere   |   1   | red   |
| circle, oriented-point, line |   2   | green |
| point pair, flat-point       |   3   | blue  |
| point                        |   4   | yellow|

## Operators
Hint: Operator precedence determines how operators are parsed concerning each other. A higher precedence number
results in a higher binding strength. Thus operators with higher precedence become the operands of operators with lower precedence.

Exceptions from the precedence rules:
- Expressions like `a-b` evaluate to `subtraction(a, b)` instead of `geometric_product(a, negate(b))`.

### 2-ary operators
All 2-ary operators are left-associative.

#### Base 2-ary operators
Hint: The Unicode and Latex name for the symbol used for left contraction is "RIGHT FLOOR" and for right contraction is "LEFT FLOOR". Please be cautious to this detail when writing Latex or programming tools which work with the language.

| precedence | symbol   | latex   | unicode | name | hints |
| :--------: | :------: | ------- | ------- | ---- | ----- |
| 4          |          |         | \u0020  | geometric product | Zero or more space characters are interpreted as the operator. |
| 3          | &#x2227; | \wedge  | \u2227  | "wedge" or outer product (join, span for no common subspace) | joining linearily independend vectors/two disjoint subspaces |
| 1          | &#x002B; | +       | \u002B  | addition | |
| 1          | &#x002D; | -       | \u002D  | subtraction | |
| 3          | &#x230B; | \rfloor | \u230B  | left contraction |  |
| 3          | &#x230A; | \lfloor | \u230A  | right contraction | | where the grade operator for negative grades is zero. This implies that `something of higher grade cannot be contracted onto something of lower grade`. |
| 3          | &#x2228; | \vee    | \u2228  | "vee" or regressive product (meet if intersected) | |
| 2          | &#x002F; | /       | \u002F  | division (inverse geometric product) |  |

##### Implementation
$A\wedge B = \langle A B\rangle_{|k+l|}$

$A\rfloor B = \langle A B\rangle_{|l-k|}$

#### Additional 2-ary operators
| precedence | symbol   | latex | unicode | description |
| :--------: | :------: | ------| ------- | ----------- |
| 3          | &#x22C5; | \cdot | \u22C5  | dot product (inner product without scalar parts) $A\cdot B=\langle A B\rangle_{|k-l|,k\neq 0, l\neq 0}$|
| 3          | &#x2229; | \cap  | \u2229  | meet (intersection) = largest common subspace |
| 3          | &#x222A; | \cup  | \u222A  | join  (union) of two subspaces is there smallest superspace = smallest space containing them both |
| 3          | &#x2299; | \odot | \u2299  | hadamard product (element-wise multiplication) |


##### Implementation
$A\cdot B=\langle A B\rangle_{|k-l|,k\neq 0, l\neq 0}$

### 1-ary operators
All 1-ary operators have higher precedence than 2-ary ones. \
All 1-ary operators are right-sides except from the negate operator '-'. \
Except dual/undual the operators cancel itself so if your write X&#732;&#732; no reverse is executed.


#### Base 1-ary operators
| precedence | symbol           | latex                         | unicode      | description |  CLUscript |
| :--------: | :--------------: | ----------------------------- | ------------ | ----------- |  :------- |
| 5          | &#x002D;         | &#x002D;                      | \u002D       | negate      |  - |
| 6          | &#x207B;&#x00B9; | \textsuperscript{-1}          | \u207B\u00B9 | general inverse |  |
| 6          | &#x002A;         | \textsuperscript{\*}          | \u002A       | Hodge dual      | ! |
| 6          | &#x02DC;         | \textsuperscript{$\tilde$}      | \u02DC       | reverse/adjoint: reverse all multiplications (e.g. inverse for rotor) |  &#732; |
| 6          | &#x2020;         | \textsuperscript{\textdagger} | \u2020       | clifford conjugate (a sign change operation) | |

There exist three types of involution operations: Space inversion, reversion and the combination of both the clifford conjugation.

#### Additional 1-ary operators
| precedence | symbol           | latex                 | unicode      | description |
| :--------: | :--------------: | --------------------- | ------------ | ----------- |
| 6          | &#x207B;&#x002A; | \textsuperscript{-\*} | \u207B\u002A | undual |
| 6          | &#x00B2;         | \textsuperscript{2}                     | \u00B2       | square |
| 6          | &#x005E;         | \textsuperscript{$\wedge$}                      | \u005E       | grade involution/inversion (a sign change operation) $\hat{M} = \sum\limits_k{(-1)^k\langle M\rangle_{k}}$|

### Composite operators
| symbol | latex | unicode      | description |
| :----------------------------------------------------------------------------------------------------------------: | ----- | ------------ | ----------- |
| &#x003C;multivector&#x003E;&#x209A; (with &#x209A; ∈ {&#x2080;, &#x2081;, &#x2082;, &#x2083;, &#x2084;, &#x2085;}) |       | &#x003C; = \u003C,  &#x003E; = \u003E, &#x2080; = \u2080, &#x2081; = \u2081, &#x2082; = \u2082, &#x2083; = \u2083, &#x2084; = \u2084, &#x2085; = \u2085| grade extraction, grade p=0-5 as subscript |


## Built-in functions
### Base functions
| symbol      | description |
| :---------- | ------------ |
| exp()       | exponential of a bivector or a scalar |
| log()       | logarithm of general rotor/even multivector (should be normalized) |
| normalize() | normalize of an even multivector (general rotor, scalars inclusive)|
| sqrt()      | squared root of a general rotor/even multivector or a scalar |
| squaredNorm()      | squared norm of a mulitvector |
| scp()       | scalar product |
| dot()       | dot product, 0-grade indcluded - different to inner product |
| ip()        | inner product, 0-grade is excluded different to the dot-product |
| negate14()  | negate the signs of the vector- and 4-vector parts of an multivector. Usable to implement general-inverse. |

### up/down projection into euclidean space
| symbol      | description |
| :---------- | ------------ |
| up()        | up-projection of a euclidean vector into the conformal space |
| down()      | down-projection of a multivector into the euclidean space by normalization and rejection from the minkowski plane E0 |

### Scalar functions
| symbol      | description |
| :---------- | ------------ |
| atan2(x,y)  | arctansgent 2 (Converts the coordinates (x,y) to coordinates (r, theta) and returns the angle theta as the couterclockwise angle in radians between -pi and pi of the point (x,y) to the positive x-axis.)|
| sin(x)      | sine |
| cos(x)      | cosine |
| tan(x)      | tangent |
| atan(x)     | arctangent |
| asin(x)     | arcsine |
| acos(x)     | arccosine |
| abs()       | absolute value of a scalar only ||
| sign(x)     | -1 if x<0 else 1 |

## Symbols
### Base vector symbols
| symbol           | latex        | Unicode      | description |
| :--------------: | ------------ | ------------ | ----------- |
| &#x03B5;&#x2080; | \epsilon_0 | \u03B5\u2080 | base vector representing the origin |
| &#x03B5;&#x1D62; | \epsilon_i | \u03B5\u1D62 | base vector representing the infinity |
| &#x03B5;&#x2081; | \epsilon_1 | \u03B5\u2081 | base vector representing x direction |
| &#x03B5;&#x2082; | \epsilon_2 | \u03B5\u2082 | base vector representing y direction |
| &#x03B5;&#x2083; | \epsilon_3 | \u03B5\u2083 | base vector representing z direction |


### Further symbols
| symbol           | latex      | Unicode      | description | implementation |
| :--------------: | -----------| ------------ | ----------- | -------------- |
| &#x03B5;&#x208A; | \epsilon_+ | \u03B5\u208A |  | 0.5&#x03B5;&#x1D62; - &#x03B5;&#x2080; |
| &#x03B5;&#x208B; | \epsilon_- | \u03B5\u208B |  | 0.5&#x03B5;&#x1D62; + &#x03B5;&#x2080; |
| &#x03C0;         | \pi        | \u03C0       | Ludolphs- or circle constant | Math.PI |
| &#x0045;&#x2080; | E_0        | \u0045\u2080 | Minkowski bivector (is its own inverse) | &#x03B5;&#7522; &#x2227; &#x03B5;&#8320;|
| &#x0045;&#x2083; | E_3        | \u0045\u2083 | Euclidean pseudoscalar | &#x03B5;&#x2081; &#x2227; &#x03B5;&#x2082; &#x2227; &#x03B5;&#x2083;     |
| &#x0045;         | E          | \u0045       | Pseudoscalar | &#x03B5;&#x1D62; &#x2227; &#x03B5;&#x2081; &#x2227; &#x03B5;&#x2082; &#x2227; &#x03B5;&#x2083; &#x2227; &#x03B5;&#x2080;|


### Useful equations between above symbols
&#x03B5;&#x2080;&#x0045;&#x2080;=-&#x03B5;&#x2080;, &#x0045;&#x2080;&#x03B5;&#x2080;=&#x03B5;&#x2080;, &#x03B5;&#x1D62;&#x0045;&#x2080;=&#x03B5;&#x1D62;, &#x0045;&#x2080;&#x03B5;&#x1D62;=-&#x03B5;&#x1D62;, &#x0045;&#x2080;&#x00B2;=1, &#x03B5;&#x2080;&#x00B2;=&#x03B5;&#x1D62;&#x00B2;=0, &#x03B5;&#x208A;&#x00B2;=1, &#x03B5;&#x208B;&#x00B2;=-1, &#x03B5;&#x208A;&#x22C5;&#x03B5;&#x208B;=0

## Next Steps
- completing the experimentally and optimized PGA implementation
- merging the experimentally generic geometric algebra implementation into the main branch
- adding operators and built-ins for symbolic derivation and algorithmic differentiation
- adding more smart-editing features based on the language-agnostic LSP from GraalVM, completion of the implementation of a language-specific LSP
- adding more debugging features e.g. step-in/step-out, showing the complete stacktrace polyglot till inside the native [CasADi](https://web.casadi.org/) libraries by building to LLVM
- completing the design of a type-system and its implementation
- extending the syntax with multidimensional arrays, loops and if-statements (A student project is already in the branch "loops")
- Hyperwedge product implementation following [DeKeninck2020] to speed up program execution
- Symbolic optimization with [Maxima](https://maxima.sourceforge.io/) - automated symbolical optimization of functions
- C-code export and parallelization with CasADi
- execution speed benchmarks, espcially to compare FAST- and TRUFFLE-implementation, autogenerated C-Code, ...


