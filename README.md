# DSL4GA
This repository contains code to work with multivector expressions of geometric algebra. The idea is to realise a complete toolchain with a geometric algebra specific domain specific language based on [Truffle/Graal](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/) with state of the art smart editing features, debugging functionality and a fast implementation based on [JCasADi](https://github.com/MobMonRob/JCasADi/) focused on bringing algorithmic differentation in the world of geometric algebra.


## Disclaimer
The current state of the project is a proof of concept, so it is not advised to use it in real world applications. If you have feedback or feature suggestions, please create a new GitHub Issue.

Especially be cautious regarding:
- The documentation may not be up to date.
- Breaking changes can occur at any time.
- There is still no static type system. Even trivial errors may only be noticed late while executing. Sometimes exceptions are raised later than the actual error or not at all. Thus erroneous code can lead to undefined behaviour.
- Rules specified in the documentation may not be consistently enforced with an eager exception. But adherence to them will avoid undefined behaviour and increase intelligibility of raised exceptions.


## Features Overview
- Domain specific language ("DSL") for Geometric Algebras. Including user defined functions.
- Multiple Geometric Algebras can be used (Currently CGA and PGA). New ones can be added fast with changes in GACasADi.
- Debugging to step through the DSL code and view variables content. Enabled by GraalVM. Tested with Netbeans.
- Visualization of Objekts while debugging.
- Fast numeric evaluation due to the internal use of sparsity and symbolic simplification of expressions (with CasADi and Maxima CAS). Additionally, the use of Geometric Algebra linearizes some transformations which increases likelyhood that the CAS finds shorter (faster) expressions.
- LaTeX printing of expressions (with help of Maxima). Currently only at the end of program execution. Adding more flexibility is planned.


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
- [SparseMatrix](https://github.com/orat/SparseMatrix)
- [GeometricAlgebra](https://github.com/orat/GeometricAlgebra)
- [ConformalGeometricAlgebra](https://github.com/orat/ConformalGeometricAlgebra)
- [JNativeLibLoader](https://github.com/MobMonRob/JNativeLibLoader)
- [JCasADi](https://github.com/MobMonRob/JCasADi)
- [GACalcAPI](https://github.com/orat/GACalcAPI)
- [GACasADi](https://github.com/orat/GACasADi)
- [Euclid3DViewAPI](https://github.com/orat/Euclid3DViewAPI)
- [EuclidView3d](https://github.com/orat/EuclidView3d)

and build the projects in these repositories to have them available in your local Maven cache. Some of them require a C++ compiler and Linux to build. Read the README.md of these projects to make sure the projects will build. Once build, the artifacts should run on Windows as well.

SparseMatrix is a simple Java sparse matrix implementation used primarily as interface between the annotation based Java API and the DSL. So it allows to write code independend from GA-specific objects. GACasADi is a fast symbolic implementation of GA based on [CasADI](https://web.casadi.org/). A Java-Wrapper for CasADI based on [Swig](https://www.swig.org/) is used for Java integration.

Install Maxima 5.47.0 on your system.


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

### Algebra definitions
The first line needs to declare the algebra used. Optionally, the implementation can be specified, too. \
With algebra being "cga" and implementation being "theImpl", the first line would be:
```
#algebra cga theImpl
```

At the moment only the algebras "cga" and "pga" are available.

### Function definitions
#### Rules
- There needs to be at least one function defined with the name `main`. Invokations of the program will call this one first.
- Currently, callees need to be defined above the callers.
- Recursion is not possible.
- In consequence, `main` needs to be the last function defined.
- Function overloading is currently not supported and will lead to an exception.
- A function will return only the list in the last line of its definition.
- The return value of a call needs to be assigned to a variable.
- An assignment to multiple variables in the same line is possible if the right side consists only of a call.
- The count of the assigned variables must match the count of the result values of a call.
- With an assignment to "_", a return value can be discarded. This is only possible for calls which return at least two values.
- If the right side of an assignment is not only a call but a composed expression, within it are only calls allowed which return exactly one value.
- Variable mutation is not possible. This means, that once assigned, its value cannot change until it scope ends. But if the same scope is entered multiple times, each time the variable can have a different value.
- Functions are pure. This means they 1. are right-unique relations from their arguments to their returns und 2. have no side effects. Thus these "function" subroutines behave similar to mathematical functions.


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

### Arrays
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

	// Concatenation of 2 arrays
	i[] = concat(a, a) // {0, 1, 2, 3, 0, 1, 2, 3}

	// Range
	//// Start inclusive
	//// Stop exclusive
	//// Step
	j[] = range(1, 10, 1)
}
```

#### Rules
- Arrays are static. Their size cannot change.
- The main Function shall not receive or return arrays.

### Higher-order functions “HOF”
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
- The first argument of the HOF builtins is always a function “the argument function”.
- The argument function does not receive or return arrays.
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

	d[], e[] = mapaccum(add, 1, a)
	// d[] = {1, 2, 4}
	// e[] = {1, 1, 2}

	f, g = mapfold(add, 1, a)
	// f = 4
	// g = 2
}
```

### Visualization
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

The color of the visualized objects depends on the grade of the geometric object.
| grade | color |
| ------| ----- |
|   1   | red   |
|   2   | green |
|   3   | blue  |
|   4   | yellow|

### Expressions
- Numeric literals like "0.5" and scalar constants like "π" are in OPNS representation.

### Operators
Hint: Operator precedence determines how operators are parsed concerning each other. A higher precedence number
results in a higher binding strength. Thus operators with higher precedence become the operands of operators with lower precedence.

Exceptions from the precedence rules:
- Expressions like `a-b` evaluate to `subtraction(a, b)` instead of `geometric_product(a, negate(b))`.

#### 2-ary operators
All 2-ary operators are left-associative.

##### Base 2-ary operators
Hint: The Unicode and Latex name for the symbol used for left contraction is "RIGHT FLOOR" and for right contraction is "LEFT FLOOR". Please be cautious to this detail when writing Latex or programming tools which work with the language.

| precedence | symbol   | latex   | unicode | name | hints |
| :--------: | :------: | ------- | ------- | ---- | ----- |
| 4          |          |         | \u0020  | geometric product | Zero or more space characters are interpreted as the operator. |
| 3          | &#x2227; | \wedge  | \u2227  | "wedge" or outer product (join/union or meet/intersection dependendend of the orientation type of the arguments) |
| 1          | &#x002B; | +       | \u002B  | addition | |
| 1          | &#x002D; | -       | \u002D  | subtraction | |
| 3          | &#x230B; | \rfloor | \u230B  | left contraction |  |
| 3          | &#x230A; | \lfloor | \u230A  | right contraction | | where the grade operator for negative grades is zero. This implies that `something of higher grade cannot be contracted onto something of lower grade`. |
| 3          | &#x2228; | \vee    | \u2228  | "vee" or regressive product (join/union or meet/intersection dependendend of the orientation type of the arguments) | |
| 2          | &#x002F; | /       | \u002F  | division (inverse geometric product) |  |

##### Implementation
$A\wedge B = \langle A B\rangle_{|k+l|}$

$A\rfloor B = \langle A B\rangle_{|l-k|}$

##### Additional 2-ary operators
| precedence | symbol   | latex | unicode | description |
| :--------: | :------: | ------| ------- | ----------- |
| 3			 | &#x00D7; | \times | \u00D7  | commutator product |
| 3          | &#x22C5; | \cdot  | \u22C5  | dot product (inner product without scalar parts) |
| 3          | &#x2229; | \cap   | \u2229  | meet (intersection) = largest common subspace |
| 3          | &#x222A; | \cup   | \u222A  | join  (union) of two subspaces is there smallest superspace = smallest space containing them both |
| 3          | &#x2299; | \odot  | \u2299  | hadamard product (element-wise multiplication) |

##### Implementation
$A\cdot B=\langle A B\rangle_{|k-l|,k\neq 0, l\neq 0}$

#### 1-ary operators
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

#### Composite operators
| symbol | latex | unicode      | description |
| :----------------------------------------------------------------------------------------------------------------: | ----- | ------------ | ----------- |
| &#x003C;multivector&#x003E;&#x209A; (with &#x209A; ∈ {&#x2080;, &#x2081;, &#x2082;, &#x2083;, &#x2084;, &#x2085;}) |       | &#x003C; = \u003C,  &#x003E; = \u003E, &#x2080; = \u2080, &#x2081; = \u2081, &#x2082; = \u2082, &#x2083; = \u2083, &#x2084; = \u2084, &#x2085; = \u2085| grade extraction, grade p=0-5 as subscript |

### Built-in functions
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
| up()        | up-projection of a euclidean vector into the space of the multivector (conformal, projection, ... depending on the algbra) |
| down()      | down-projection of a multivector into the euclidean space (by normalization and rejection from the minkowski plane E0 in the case of CGA) |
| euclid()    | euclidean part of the multivector (Blades containing **only** base elements with metric 1 and no others. (without 0-grade scalar)) |
| idle()      | idle part of the multivector (includes no location information) (Blades containing base elements with metric 0 or -1. (without 0-grade scalar)) |

#### not yet implemented
| symbol      | description |
| :---------- | ------------ |
| coef()      | with two mulitvectors as arguments. The second must be one blade only. The function extracts the coefficient for this blade in the first argument as as scalar |

#### Scalar functions
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

### Symbols
#### Base vector symbols
| symbol           | latex        | Unicode      | description |
| :--------------: | ------------ | ------------ | ----------- |
| &#x03B5;&#x2080; | \epsilon_0 | \u03B5\u2080 | base vector representing the origin |
| &#x03B5;&#x1D62; | \epsilon_i | \u03B5\u1D62 | base vector representing the infinity |
| &#x03B5;&#x2081; | \epsilon_1 | \u03B5\u2081 | base vector representing x direction |
| &#x03B5;&#x2082; | \epsilon_2 | \u03B5\u2082 | base vector representing y direction |
| &#x03B5;&#x2083; | \epsilon_3 | \u03B5\u2083 | base vector representing z direction |

#### Further symbols
| symbol           | latex      | Unicode      | description | implementation |
| :--------------: | -----------| ------------ | ----------- | -------------- |
| &#x03B5;&#x208A; | \epsilon_+ | \u03B5\u208A |  | 0.5&#x03B5;&#x1D62; - &#x03B5;&#x2080; |
| &#x03B5;&#x208B; | \epsilon_- | \u03B5\u208B |  | 0.5&#x03B5;&#x1D62; + &#x03B5;&#x2080; |
| &#x03C0;         | \pi        | \u03C0       | Ludolphs- or circle constant | Math.PI |
| &#x0045;&#x2080; | E_0        | \u0045\u2080 | Minkowski bivector (is its own inverse) | &#x03B5;&#7522; &#x2227; &#x03B5;&#8320;|
| &#x0045;&#x2083; | E_3        | \u0045\u2083 | Euclidean pseudoscalar | &#x03B5;&#x2081; &#x2227; &#x03B5;&#x2082; &#x2227; &#x03B5;&#x2083;     |
| &#x0045;         | E          | \u0045       | Pseudoscalar | &#x03B5;&#x1D62; &#x2227; &#x03B5;&#x2081; &#x2227; &#x03B5;&#x2082; &#x2227; &#x03B5;&#x2083; &#x2227; &#x03B5;&#x2080;|

#### Useful equations between above symbols
&#x03B5;&#x2080;&#x0045;&#x2080;=-&#x03B5;&#x2080;, &#x0045;&#x2080;&#x03B5;&#x2080;=&#x03B5;&#x2080;, &#x03B5;&#x1D62;&#x0045;&#x2080;=&#x03B5;&#x1D62;, &#x0045;&#x2080;&#x03B5;&#x1D62;=-&#x03B5;&#x1D62;, &#x0045;&#x2080;&#x00B2;=1, &#x03B5;&#x2080;&#x00B2;=&#x03B5;&#x1D62;&#x00B2;=0, &#x03B5;&#x208A;&#x00B2;=1, &#x03B5;&#x208B;&#x00B2;=-1, &#x03B5;&#x208A;&#x22C5;&#x03B5;&#x208B;=0

## Algebras

Each algebra has to define a dual operator. Using the Hodge-dual makes such a definition dependend from the choice of the basis.

### PGA - projective geometric algebra Cl(3,0,1)

This algebra contains flat objects only. 

The dual operator is defined by the Hodge Dual. Using the euclidean split it admits the closed form expression $\tilde{A_I}E_3+\epsilon_0\hat{\tilde{A_E}}E_3$ with $A_I=idle(A), A_E=euclid(A) $.

The inverse operator is implemented by analysing the type of the mulitivector and switching automatically between different implementations. Not all multivectors have an inverse e.g. specific points at infinity or pure idle lines. Inverses are only determined for extrinsic orientation types. The inverse of a plane is the same plane. The inverse of an axis and of a point only changes the sign. The inverse of a motor is the reverse (normalization of the motor is a precondition which is not tested during compiletime and also not tested during runtime) and the most complex inverse is needed for a general bivector. For this, the euclidean split of $B\tilde{B} = a+b\epsilon_{0123}$ is used. This is a study number corresponding to a dual number. The dual number inverse is $\frac{1}{a+b\epsilon_{0123}} = \frac{1}{a}+\frac{b}{a^2}\epsilon_{0123}$. Multiplying by $\tilde{B}$ results in $\frac{1}{B}=(\frac{1}{a}-\frac{b}{a^2}\epsilon_{0123})\tilde{B}$. 

#### Geometric objects with intrinsic orientation type

The orientation type of the following objects corresponds with the so called outer product null space representation (OPNS), sometimes also named as "point based" representation.

Orthogonal reflection of objects from this type results in inversion of the orientation. Reflection of objects inside the reflection plane do not changed its orientation. Therefore these objects are handedness-preserving under reflection on planes.

Homogeneous/directed points are defined as:

| object | grade | formula |
| :---------- | :------ | :-------- |
| point | 1 |  $\displaystyle p = e_0 + \vec{n}$ |

and from this, the following geometric objects can be created by joining the points (using the wedge-operator):

| object | grade | formula | description |
| :---------- | :------ | :-------- | ---------------- |
| spear (join line) | 2 |  $\displaystyle l = p_2\wedge p_1 = \vec{n}\wedge p$ | The line points from the first to the second point, or is defined by one point and an euclidean direction vector. |
| plane | 3 |   $\displaystyle \pi = p_1\wedge p_2\wedge p_3 = p\wedge\vec{n}^{\ast} =\epsilon_0\wedge\vec{n}^{\ast}+\vec{x}\wedge\vec{n}^{\ast} = \epsilon_0\wedge\vec{n}^{\ast}-(\vec{x}\cdot\vec{n})E_3$ | clockwise  arrangement of the points, defines the plane and the direction of its normal vector. |

Spears correspond with polar vectors and can represent local orbits or momenta of points.

#### Geometric objects with extrinsic orientation type

The orientation type of the following objects corresponds with the so called commutator product null space representation (CPNS), sometimes also named as "plane-based" representation.

| object | grade | formula |
| :---------- | :------ | :-------- |
| plane | 1 |  $\displaystyle \pi =\vec{n}+(\vec{x}\cdot\vec{n})\epsilon_0 = p_3\vee p_2\vee p_1$ |

The following objects are constructed by meeting planes (also using the wedge-operator).

| object | grade | formula |
| :----- | :--- | -------- |
| axis (meet line) | 2 |  $\displaystyle l=\pi_2\wedge\pi_1=\vec{n}^{\ast}-(\vec{x}\cdot\vec{n}^{\ast})\mathord{\epsilon_0}$ |
| point | 3 | $\displaystyle p=\pi_3\wedge\pi_2\wedge\pi_1=\pi\wedge l=E_3 + \vec{x}\epsilon_0 E_3$ |

Axes correspond to axial vectors and can describe movement velocity (rotations (finite) and translations (idial)).

### CGA - Conformal Geometric Algebra Cl(4,1,0)

This algebra contains flat and round elements.

#### Geometric objects with intrinsic orientation type

The orientation type of these objects corresponds with the so called outer product null space representation (OPNS), sometimes also named as "direct" representation.

Round points can be created from euclidean parameters/coordinates:

| object | grade |  formula |
| :---------- | :---- | :----------------- |
| round point | 1 |  $\displaystyle \vec{p}=\vec{x}+\frac{1}{2}\vec{x}^2\epsilon_\infty+\epsilon_0$ |

Joining round points only (using the wedge-operator) produces further round objects. That´s why these geometric objects are called "point-based".

| object | grade | formula |
| :---------- | :------ | :-------- |
| dipole (oriented point pair) | 2 |  p1&#8743;p2 |
| circle | 3 |  p1&#8743;p2&#8743;p3 |
| sphere | 4 |  p1&#8743;p2&#8743;p3&#8743;p4 |

Joining round points with the point in infinity creates the flat objects:

| object | grade | formula |
| :---------- | :------ |  :-------- |
| flat (homogeneous) point |  2 |  p&#8743;&#x03B5;&#7522; |
| spear (join line) | 3 | p1&#8743;p2&#8743;&#x03B5;&#7522; |
| plane | 4 |  p1&#8743;p2&#8743;p3&#8743;&#x03B5;&#7522;|

An oriented point can be created from euclidean parameters/coordinates:

| object | grade | type | formula |
| :---------- | :---- | :----| :-------------------- |
| oriented point | 3 | round | 	$$\vec{Q}=\vec{m}\wedge\vec{v}+(\frac{1}{2}\vec{v}^2\vec{m}-\vec{v}(\vec{v}\cdot\vec{m}))\epsilon_\infty+\vec{m}\epsilon_0-\vec{m}\cdot\vec{v}E_0$$|

#### Geometric objects with extrinsic orientation type

The orientation type of these objects corresponds with the inner product null space representation (IPNS), sometimes named as "dual" representation.

Spheres can be created from euclidean parameters/coordinates:

| object | grade |  formula |
| :---------- | :---- |  :---------- |
| sphere | 1 |  P-0.5r&sup2;&#x03B5;&#7522; |

Further round objects are constructed by intersection of spheres (using the wedge-operator). That´s why these geometric objects are called "sphere-based".

| object | grade |  formula |
| :---------- | :------ | :-------------|
| circle  | 2 |  s1&#8743;s2 |
| point pair | 3 |  s1&#8743;s2&#8743;s3 |
| point  | 4 |  s1&#8743;s2&#8743;s3&#8743;s4 |

Different to PGA there are spheres which do not intersect and further flat geometric objects are determined otherwise.

| object | grade |  formula |
| :---------- | :------ |  :-------------|
| plane  | 1 |  n+d&#x03B5;&#7522; |
| axis (meet line) | 2 |  p1&#8743;p2 |


## Dev Docs / Implementation notes
### How to create a new Builtin?
In `DSL4GA_Impl_Truffle`:

- Go to package: `de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins`
	- Make a new class similar to the existing ones.

- Go to class: `de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.runtime.BuiltinRegistry`
	- Register yor Builtin in `installBuiltins()` similar to the existing ones.

If the name of the Builtin class is “Abs”, the Builtin function in the DSL will be “abs”.


## Next Steps
- adding builtins for symbolic (implemented with Maxima) and numeric (implementy with Casadi) zerofinding, e.g. for singularity detection in robotics
- symbolically optimizing expressions with many trigometric functions
- adding operators and built-ins for symbolic derivation and algorithmic differentiation
- adding more smart-editing features based on the language-agnostic LSP from GraalVM, completion of the implementation of a language-specific LSP
- adding more debugging features e.g. showing the complete stacktrace polyglot till inside the native [CasADi](https://web.casadi.org/) libraries by building to LLVM
- completing the design of a type-system and its implementation
- extending the syntax with multidimensional arrays, if-statements
- Hyperwedge product implementation following [DeKeninck2020] to speed up program execution
- C-code export and parallelization with CasADi
- execution speed benchmarks, espcially to compare FAST- and TRUFFLE-implementation, autogenerated C-Code, ...

