# DSL4GeometricAlgebra

This repository contains code to work with multivector expressions of conformal algebra. The code is used as a reference implementation to demonstrate and test the software pipeline of truffle/graal in the context of geometric algebra and underlaying multivector implementations.

The code is in very early stage.

## GraalVM Setup
Download the [GraalVM](https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.0.0.2/graalvm-ce-java17-linux-amd64-22.0.0.2.tar.gz)

Extract the downloaded archive to an arbitrary location.

### Netbeans configuration
Add a new java platform with the name "GraalVM 17". \
Within the Netbeans 13 IDE you can do this if you follow these steps:
- open project properties via right-click on the project
- navigate to Build / Compile
- click "Manage Java Platforms..."

or navigate to this point via the Tools main menu.

- click "Add Platform..."
- In the poping-up wizard:
  - select platform type "Java Standard Edition"
  - choose the platform folder within the extracted archive.
  - name it "GraalVM 17"

### Netbeans project configuration
- open project properties via right-click on the project
- navigate to Build / Compile
- in the drop-down list labeled "Java Platform" choose "GraalVM 17"

### Dependencies
The project depends on the vecmath library in the refactured version of jogamp. Your can find this library [here](https://jogamp.org/deployment/java3d/1.7.0-final/). Unfortunately there is no maven repository available. That is why you need to download the jar file manually and add it as a local depency of the project. To do this in the nebeans ide: Right-click on the depencies of the project and add the dependency manually. The group id is "org.jogamp.java3d", the artifactId is "vecmath" and the type is "jar".

## Types
| Name | implementation class | setting from outside possible | hints |
| :-------- | :---- | ----- | ------|
| double | Double | x | |
| Tuple3d | org.jogamp.vecmath.Tuple3d | x | |
| Quat4d | org.jogamp.vecmath.Quat4d | x | |
| Multivector | de.orat.math.cga.impl1.CGA1Multivector | | |

Quad4d, Tuple3d and double are automatically casted into a multivector. No other operations possible based on these types inside the language. 
## Operators

### Dual operators
#### Base operators
| precedence | symbol           | latex   | unicode | name | implementation | hints | 
| :--------: | :-------------:  | ------- | ------- | ---- | -------------- | ----- |
| 3          | &#x0020; (space) |         | \u0020  | geometric product | multivector1.gp(multivector2) | Exactly one space character is interpreted as the operator. |
| 3          | &#x22C5;         | \cdot   | \u22C5  | inner product | multivector1.ip(multivector2, RIGHT_CONTRACTION) | Decreasing dimensions or contracting a subspace. In the default configuration equal to left contraction (corresponding to Ganja.js). But this looks to be incompatible with some formulas in [Kleppe], which work only with the usage of right contraction. In CLUscript this corresponds to ".". |
| 3          | &#x2227;         | \wedge  | \u2227  | outer product (meet) | multivector1.op(multivector2), not used for double, for tuple3d it makes sense but actually no implementation is available | joining linearily independend vectors/two disjoint subspaces |
| 1          | &#x002B;         | +       | \u002B  | sum | multivector1.add(multivector2) | |
| 1          | &#x002D;         | -       | \u002D  | difference | multivector1.sub(multivector2) | |

#### Additional operators (for more convenience only)
| precedence | symbol   | latex            | unicode | description | implementation | CLUscript |
| :--------: | :------: | ---------------- | ------- | ----------- | -------------- | :----- |
| 3          | &#x2229; |  \cap            | \u2229  | meet (intersection) | multivector1.meet(multivector2) | \& |
| 3          | &#x222A; |  \cup            | \u222A  | join  (union) | multivector1.join(multivector2) or multivector2* &#8901; multivector1 or (multivector2* &#8743; multivector1*)* | \| |
| 3          | &#x230B; | \llcorner        | \u230B  | right contraction | multivetor1.ip(multivector2, RIGHT_CONTRACTION) |  |
| 3          | &#x230A; | \lrcorner        | \u230A  | left contraction | multivector1.ip(multivector1, LEFT_CONTRACTION); where the grade operator for negative grades is zero. This implies that `something of higher grade cannot be contracted onto something of lower grade`. | |
| 3          | &#x2228; | \vee             | \u2228  | regressive product (join) | multivector1.vee(multivector2) or (multivector1* &#8743; multivector2*)* |  |
| 2          | &#x002F; | \StrikingThrough | \u002F  | division (inverse geometric product) | multivector1.div(multivector2) |  |

### Monadic/unary operators (placed all on right side)
The unary operators have the highest precedence, so they are executed before any other operations. The '-' os the only left-side operator. All the others are right-sided. Except dual/undual the operators cancel itself so if your write X&#732;&#732; no reverse is executed.

#### Base monadic operators
| precedence | symbol           | latex                         | unicode      | description | implementation | CLUscript |
| :--------: | :--------------: | ----------------------------- | ------------ | ----------- | -------------- | :------- |
| 4          | &#x2212;         | -                             | \u2212       | negate | (-1 cast to multivector).gp(multivector) | - |
| 4          | &#x207B;&#x00B9; | \textsuperscript{-1}          | \u207B\u00B9 | general inverse | multivector.generalInverse() | ! |
| 4          | &#x002A;         | \textsuperscript{*}           | \u002A       | dual | multivector.dual() | |
| 4          | &#x02DC;         | \textsuperscript{\tilde}      | \u02DC       | reverse | multivector.reverse() | &#732; |
| 4          | &#x2020;         | \textsuperscript{\textdagger} | \u2020       | clifford conjugate | multivector.conjugate() | |

There exist three types of involution operations: Space inversion, reversion and the combination of both the clifford conjugation.

#### Additional monatic operators (for more convenience only) 
| precedence | symbol           | latex                | unicode      | description | implementation |
| :--------: | :--------------: | ---------------------| ------------ | ----------- | -------------- |
| 4          | &#x207B;&#x002A; | \textsuperscript{-*} | \u207B\u002A | undual | multivector.undual() or -multivector.dual() |
| 4          | &#x00B2;         |                      | \u00B2       | square | multivector.gp(multivector), sqr(double) |
| 4          | &#x5e;           |                      | \u005E       | involute | multivector.gradeInversion(multivector) |


### Composite operators
| precedence | symbol                                                                                                   | latex | unicode      | description | implementation |
| :--------: | :------------------------------------------------------------------------------------------------------: | ----- | ------------ | ----------- | -------------- |
| 4          | &#x003C;multivector&#x003E;&#x209A; (with &#x209A; ∈ {&#x2080;, &#x2081;, &#x2082;, &#x2083;, &#x2084;, &#x2085;}) |       | &#x003C; = \u003C,  &#x003E; = \u003E, &#x2080; = \u2080, &#x2081; = \u2081, &#x2082; = \u2082, &#x2083; = \u2083, &#x2084; = \u2084, &#x2085; = \u2085| grade extraction, grade p=0-5 as subscript | multivector.extractGrade(int grade)   |

### Buildin functions

#### Base functions
| precedence | symbol | latex | description | implementation |
| :--------: | :------ | ----- | ----------- | -------------- |
| 4 | exp()         | \exp{} | exponential | multivector.exp() |
| 4 | involute()    |  | grade inversion | multivector.gradeInversion() |
| 4 | abs()         |  | absolute value | Math.abs(multivector) |
| 4 | sqrt()        |  | square root | Math.sqrt(double) |
| 4 | atan2()       |  | arcus tansgens 2 | Math.atan2(double/scalar as multivector, double/scalar as multivector) |
| 4 | negate14()    |  | negate the signs of the vector- and 4-vector parts of an multivector. Usable to implement gerneral inverse. |

#### Additional functions (for more convenience only)
| precedence | symbol | latex | description | implementation |
| :--------: | :-----| ----- | ----------- | -------------- |
| 4 | reverse(multivector)     |  \textsuperscript{\tilde} | reverse | multivector.reverse() |
| 4 | conjugate(multivector)   | \textsuperscript{\textdagger} | clifford conjugate | multivector.conjugate() |
| 4 | normalize(multivector)        | | normalize | multivector.unit() |
| 4 | sqr(multivector) | | square | multivector.gp(multivector) or multivector.sqr() |

#### Additional functions (2) to define geometric objects (for more convenience only)

Outer product null space representations are called dual. Corresponding regular expressions are in the inner product null space representations. Be careful in the "older" literature this is often defined reverse.

| precedence | symbol | description | implementation |
| :--------: | :------ | ----------- | -------------- |
| 4 | point(tuple3d)        |  creates a conformal point from an 3d-tuple | createPoint(tuple3d) |
| 4 | dualPointPair(tuple3d1, tuple3d2)        |  creates a conformal dual point pair from two 3d-tuple | createDualPointPair(tuple3d1,tuple3d2) |
| 4 | dualLine(tuple3d1, tuple3d2)        |  creates a conformal line from 3d-tuples defining a point and a direction or a second point | createDualLine(tuple3d1, tuple3d2) |
| 4 | dualSphere(tuple3d1, tuple3d2, tuple3d3, tuple3d4)        |  creates a conformal sphere from four 3d-tuple | createDualSphere(tuple3d1, tuple3d2, tuple3d3, tuple3d4) |
| 4 | sphere(tuple3d, double)        |  creates a conformal sphere from a 3d-tuple and the radius| createSphere(tuple3d, double) |
| 4 | plane(tuple3d1, tuple3d2)        |  creates a conformal plane from a 3d-tuple defining a point on the plane and another 3d-tuple defining the normal vector | createPlane(tuple3d, tuple3d) |
| 4 | plane(tuple3d, double) | creates a conformal plane based on its normal vector and the distance to the origin (Hesse normal form) | createPlane(tuple3d, double) |
| 4 | dualPlane(tuple3d1, tuple3d2, tuple3d3) | creates a conformal dual plane based on three points | createDualPlane(tuple3d1, tuple3d2, tuple3d3) |
| 4 | dualCircle(tuple3d1, tuple3d2, tuple3d3) | creates a conformal dual circle based on three points | createDualCircle(tuple3d1, tuple3d2 tuple3d3) |
| 4 | dualPointPair(tuple3d1, tuple3d2) | create a conformal dual point pair based on three points | createDualPointPair(tuple3d1, tuple3d2) |

#### Additional functions (3) to create transformations (for more convenience only)

| precedence | symbol | description | implementation |
| :--------: | :------ | ----------- | -------------- |
| 4 | translator(tuple3d)        |  creates a translation from an 3d-tuple | createTranslation(tuple3d) |
| 4 | rotator(tuple3d, double)        |  creates a rotatio from an 3d-tuple representing the rotation axis and a double representing the angle in radian | createTranslation(tuple3d) |
### Symbols
#### Base vector symbols
| symbol           | latex         | Unicode      | description | implementation |
| :--------------: | ------------- | ------------ | ----------- | -------------- |
| &#x03B5;&#8320;  | \textepsilon  | \u03B5\u2080       | base vector representing the origin | createOrigin(1d) |
| &#x03B5;&#7522;  | \textepsilon  | \u03B5\\u1062     | base vector representing the infinity | createInf(1d) |
| &#x03B5;&#x2081; | \textepsilon  | \u03B5\u2081 | base vector representing x direction | createEx(1d) |
| &#x03B5;&#x2082; | \textepsilon  | \u03B5\u2082 | base vector representing y direction | createEy(1d) |
| &#x03B5;&#x2083; | \textepsilon  | \u03B5\u2083 | base vector representing z direction | createEz(1d) |

### Further symbols
| symbol          | latex         | Unicode       | description | implementation |
| :-------------: | ------------- | ------------- | ----------- | -------------- |
| &#x03C0;        | \pi           | \u03C0        | Ludolphs- or circle constant | Math.PI |
| &#8734;         |               | \u221E        | corresponding to infinity vector in Dorst, Fontijne & Mann 2007 | 2&#x03B5;&#8320;  |
| &#x6F;          | o             | \u004F        | corresponding to origin vector in Dorst, Fontijne & Mann 2007 | 0.5&#x03B5;&#7522;  |
| &#110;          | n             | \u006E        | corresponding to infinity vector in Doran & Lasenby | &#x03B5;&#7522;  |
| &#x00f1;        |               | \u00F1        | corresponding to origin vector in Doran & Lasenby | -2&#x03B5;&#8320; |
| E&#8320;        |               | E\u8320       | Minkovsky bi-vector | &#x03B5;&#7522; &#x2227; &#x03B5;&#8320;|

## Important formulae
### Formulae to create conformal geometric objects

#### Geometric objects in outer product null space representation (dual)
| description | formula | grade |
| :---------- | :------ | :----|
| Sphere(point) from four conformal points (p1, p2, p3, p4) | p1&#8743;p2&#8743;p3 &#8743;p4| 4 |
| Plane from three conformal points (p1, p2, p3) | p1&#8743;p2&#8743;p3&#8743;&#x03B5;&#7522;| 4 |
| Circle from three conformal Points (p1, p2, p3) | p1&#8743;p2&#8743;p3 | 3 |
| Line from two conformal planes (p1, p2) | p1&#8743;p2 | 3 |
| Point pair from  two conformal points (p1, p2) | p1&#8743;p2 | 2 |
| Point from euclidian vector (x) | x+0.5x&sup2;&#x03B5;&#7522;+&#x03B5;&#8320; | 1 |

#### Geometric objects in inner product null space representation
| description | formula | grade |
| :---------- | :------ | :----|
| Point from euclidian vector (x) | x+0.5x&sup2;&#x03B5;&#7522;+&#x03B5;&#8320; | 1 |
| Sphere from conformal point (P) and radius (r) | P-0.5r&sup2;&#x03B5;&#7522; | 1 |
| Plane from euclidian normal vector (n) and distance to origin (d) | n+d&#x03B5;&#7522; | 1 |
| Circle from two conformal spheres (s1, s2) | s1&#8743;s2 | 2 |
| Line from two conformal planes (p1, p2) | p1&#8743;p2 | 2 |
| Point pair from  three conformal spheres (s1, s2, s3) | s1&#8743;s2&#8743;s3 | 3 |
| Point(sphere) from four conformal points (p1, p2, p3, p4) | p1&#8743;p2&#8743;p3 &#8743;p4 | 4 |

### Formulae to decompose conformal object representations
| description | formula | 
| :---------- | :------ |
| Backprojection of a conformal point (P) into an euclidian vector. The formula in the first bracket normalizes the point. Then this normalized point is rejected from the minkowski plane. | (P/(P&#x22C5;&#x03B5;&#7522;))&#x2227;E&#8320;E&#8320;&#x207B;&#x00B9; |
| Location of a round (X) or a tangent (X) represented in 3d coordinates | -0.5(X&#x03B5;&#7522;X)/(&#x03B5;&#7522;&#8901;X)&sup2; | 
| Direction vector (attitude) of a dual line (L*) represented as 3d coordinates of (&#949;&#8321;, &#949;&#8322;, &#949;&#8323;). | (L*&#8901;&#x03B5;&#8320;)&#8901;&#x03B5;&#7522; |
| Radius (r) of a conformal sphere (S) | r&#x00B2; = (S&#x002A;)&#x00B2; = S&#x002A;&#x22C5;S&#x002A; |
| Distance (d) between the the center of a conformal sphere (S) and a conformal point (P) | d&#x00B2; = S&#x22C5;S-2S&#x22C5;P |

### Formulae to implement base functionalitity of CGA

| description | formula | 
| :---------- | :------ |
| Matrix free implementation of the inverse | x&#x207B;&#x00B9; =  (x&#x2020; involute(x) x&#x02DC; negate14(x)(x x&#x2020; involute(x) x&#x02DC;))/(x x&#x2020; involute(x) x&#x02DC; negate14(x) (x x&#x2020; involute(x) x&#x02DC;)) |

### General useful equations
| name | equation | description |
| :---------- | :------------------ | ---------------------- |
| anticommutivity | u &#8743; v = - (v &#8743; u) | |
| distributivity | u &#8743; (v + w) = u &#8743; v + u &#8743; w | |
| associativity | u &#8743; (v &#8743; w) = (u &#8743; v) &#8743; w | |
| | (A &#8970; B)&#732; = B&#732; C&#8743; A&#732; | |
| | A &#8743; B * C = A * (B &#8971; C) | |
| | C * (B &#8743; A) = (C &#8970; B) * A | |
| intersection | (A &#8745; B)* = B* &#8743; A* | Intersection = outer product in the dual representation; B* &#8743; A* means computing the union of everything which is not B and everything that is not A. The dual of that must be what have A and B in common.|
| projection | (A&#x230B;B)B&#x207B;&#x00B9; | Projection of A from B |
| rejection | (A&#x2227;B)B&#x207B;&#x00B9; | Rejection of A from B |
