# DSL4GeometricAlgebra

This repository contains code to work with multivector expressions of conformal algebra. The code is used as a reference implementation to demonstrate and test the software pipeline of truffle/graal in the context of geometric algebra and underlaying multivector implementations.

The code is in very early stage.

## GraalVM Setup
Download the [GraalVM](https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.0.0.2/graalvm-ce-java17-linux-amd64-22.0.0.2.tar.gz)

Extract the downloaded archive to an arbitrary location.

### Netbeans configuration
Add a new java platform with the name "GraalVM 17". \
Within the Netbeans 15 IDE you can do this if you follow these steps:
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

## Dependencies Setup
The project depends on the vecmath library in the refactured version of jogamp. Your can find this library [here](https://jogamp.org/deployment/java3d/1.7.1-build-20200222/vecmath.jar). Unfortunately there is no maven repository available. That is why you need to download the jar file manually and add it as a local depency of the project. To do this in the nebeans ide: Right-click on the depencies of the project and add the dependency manually. The group id is "org.jogamp.java3d", the artifactId is "vecmath" and the type is "jar". \
Alternatively clone it from [GitHub](https://github.com/JogAmp/vecmath/tree/dev1.7.1), update the compiler version in it's pom.xml and build it.

Clone [GeometricAlgebra](https://github.com/orat/GeometricAlgebra/tree/181f2d7492b2c612339427d34a75271717edc06a) and [ConformalGeometricAlgebra](https://github.com/orat/ConformalGeometricAlgebra/tree/00ad2c32f311074e6b31ebd337da622bb099b9c2) and build those projects to have them available in your local Maven cache.

## Run
In order to run the example invokation in the package 'de.dhbw.rahmlab.geomalgelang.App' make sure you successfully executed the steps [GraalVM Setup](https://github.com/MobMonRob/DSL4GeometricAlgebra#graalvm-setup) and [Dependencies Setup](https://github.com/MobMonRob/DSL4GeometricAlgebra#dependencies-setup) beforehand. \
If you use an IDE other than Netbeans and execute the generated .class files directly rather than the generated .jar file, it might be necessary to configure the Maven execution in your IDE with the same properties set in the [nbactions.xml](https://github.com/MobMonRob/DSL4GeometricAlgebra/blob/main/nbactions.xml) file.

## Types to use from outside the language
| Name | implementation class | hints |
| :-------- | :---- | ------|
| double | Double |  |
| Tuple3d | org.jogamp.vecmath.Tuple3d |  |
| Quat4d | org.jogamp.vecmath.Quat4d |  |
| DualQuat4d | de.orat.math.vecmath.ext.DualQuat4d | |
| DualNumber | de.orat.math.vecmath.ext.DualNumber | |
| ComplexNumber | de.orat.math.vecmath.ext.ComplexNumber | |

All of these types are automatically casted into a multivector inside the language. No other operations possible based on these types inside the language.

## Types inside the language

### CGA types
| Name | implementation class | hints |
| :-------- | :---- | ------|
| CGAMVec&#x003C;representation&#x003E; | de.orat.math.cga.api.CGAMultivector | All types set from outside the language can be casted to this Multivector class. But there are more specific implementations which can be used for more efficiency. |
| CGAScalar&#x003C;representation&#x003E;| de.orat.math.api.CGAScalar extends CGAMultivector| |
| CGAVecE3&#x003C;representation&#x003E;| extends CGAMultivector |
| CGASphere&#x003C;representation&#x003E;| extends CGAMultivector |
| CGAPointPair&#x003C;representation&#x003E; | extends CGAMultivector |
| CGACircle&#x003C;representation&#x003E;| extends CGAMultivector |
| CGAPoint&#x003C;representation&#x003E; | extends CGAMultivector |

&#x003C;representation&#x003E; = IPNS | OPNs
  
## Operators
Hint: Operator precedence determines how operators are parsed concerning each other. Operators with higher precedence become the operands of operators with lower precedence.

### Dual operators
#### Base operators
| precedence | symbol           | latex    | unicode | name | implementation | hints |
| :--------: | :--------------: | -------- | ------- | ---- | -------------- | ----- |
| 4          | &#x0020; (space) |          | \u0020  | geometric product | multivector1.gp(multivector2) | Zero or more space characters are interpreted as the operator. |
| 3          | &#x22C5;         | \cdot    | \u22C5  | inner product | multivector1.ip(multivector2, RIGHT_CONTRACTION) | Decreasing dimensions or contracting a subspace. In the default configuration equal to left contraction (corresponding to Ganja.js). But this looks to be incompatible with some formulas in [Kleppe], which work only with the usage of right contraction. In CLUscript this corresponds to ".". |
| 3          | &#x2227;         | \wedge   | \u2227  | outer product (join) | multivector1.op(multivector2), not used for double, for tuple3d it makes sense but actually no implementation is available | joining linearily independend vectors/two disjoint subspaces |
| 1          | &#x002B;         | +        | \u002B  | sum | multivector1.add(multivector2) | |
| 1          | &#x002D;         | -        | \u002D  | difference | multivector1.sub(multivector2) | |

#### Additional operators (for more convenience only)
| precedence | symbol   | latex            | unicode | description | implementation | CLUscript |
| :--------: | :------: | ---------------- | ------- | ----------- | -------------- | :----- |
| 3          | &#x2229; | \cap             | \u2229  | meet (intersection) | multivector1.meet(multivector2) | \& |
| 3          | &#x222A; | \cup             | \u222A  | join  (union) | multivector1.join(multivector2) or multivector2* &#8901; multivector1 or (multivector2* &#8743; multivector1*)* | \| |
| 3          | &#x230B; | \llcorner        | \u230B  | right contraction | multivetor1.ip(multivector2, RIGHT_CONTRACTION) |  |
| 3          | &#x230A; | \lrcorner        | \u230A  | left contraction | multivector1.ip(multivector1, LEFT_CONTRACTION); where the grade operator for negative grades is zero. This implies that `something of higher grade cannot be contracted onto something of lower grade`. | |
| 3          | &#x2228; | \vee             | \u2228  | regressive product (meet or intersection) | multivector1.vee(multivector2) or (multivector1* &#8743; multivector2*)* |  |
| 2          | &#x002F; | \StrikingThrough | \u002F  | division (inverse geometric product) | multivector1.div(multivector2) |  |

### Monadic/unary operators (placed all on right side)
The unary operators have the highest precedence, so they are executed before any other operations. The '-' os the only left-side operator. All the others are right-sided. Except dual/undual the operators cancel itself so if your write X&#732;&#732; no reverse is executed.

#### Base monadic operators
| precedence | symbol           | latex                         | unicode      | description | implementation | CLUscript |
| :--------: | :--------------: | ----------------------------- | ------------ | ----------- | -------------- | :------- |
| 5          | &#x002D;         | -                             | \u002D       | negate | (-1 cast to multivector).gp(multivector) | - |
| 6          | &#x207B;&#x00B9; | \textsuperscript{-1}          | \u207B\u00B9 | general inverse | multivector.generalInverse() | ! |
| 6          | &#x002A;         | \textsuperscript{\*}          | \u002A       | dual | multivector.dual() | |
| 6          | &#x02DC;         | \textsuperscript{\tilde}      | \u02DC       | reverse/adjoint: reverse all multiplications (a sign change operation) | multivector.reverse() | &#732; |
| 6          | &#x2020;         | \textsuperscript{\textdagger} | \u2020       | clifford conjugate (a sign change operation) | multivector.conjugate() | |

There exist three types of involution operations: Space inversion, reversion and the combination of both the clifford conjugation.

#### Additional monatic operators (for more convenience only)
| precedence | symbol           | latex                 | unicode      | description | implementation |
| :--------: | :--------------: | --------------------- | ------------ | ----------- | -------------- |
| 6          | &#x207B;&#x002A; | \textsuperscript{-\*} | \u207B\u002A | undual | multivector.undual() or -multivector.dual() |
| 6          | &#x00B2;         |                       | \u00B2       | square | multivector.gp(multivector), sqr(double) |
| 6          | &#x005E;         |                       | \u005E       | grade involution/inversion (a sign change operation) | multivector.gradeInversion(multivector) |


### Composite operators
| precedence       | symbol                                                                                                   | latex | unicode      | description | implementation |
| :--------------: | :------------------------------------------------------------------------------------------------------: | ----- | ------------ | ----------- | -------------- |
| (not applicable) | &#x003C;multivector&#x003E;&#x209A; (with &#x209A; ∈ {&#x2080;, &#x2081;, &#x2082;, &#x2083;, &#x2084;, &#x2085;}) |       | &#x003C; = \u003C,  &#x003E; = \u003E, &#x2080; = \u2080, &#x2081; = \u2081, &#x2082; = \u2082, &#x2083; = \u2083, &#x2084; = \u2084, &#x2085; = \u2085| grade extraction, grade p=0-5 as subscript | multivector.extractGrade(int grade)   |

### Buildin functions

#### Base functions
| symbol     | latex | description | implementation |
| :--------- | ----- | ----------- | -------------- |
| exp()      | \exp{} | exponential | multivector.exp() |
| normalize(multivector) | | normalize | multivector.unit() |
| abs()      |  | absolute value | Math.abs(CGAScalar) |
| sqrt()     |  | square root | Math.sqrt(CGAScalar) |
| atan2()    |  | arcus tansgens 2 | Math.atan2(CGAScalar, CGAScalar) |
| negate14() |  | negate the signs of the vector- and 4-vector parts of an multivector. Usable to implement gerneral inverse. | multivector.negate14() |

#### Additional functions (2) to define geometric objects (for more convenience only)

Some methods creates geometric objects in inner- and some in outer- product null space representation. Not all combinations are needed. Only the ones which are useful for easy createn objects in the conformal space based on euclean parameters are implemented.

| symbol                                             | description | implementation |
| :------------------------------------------------- | ----------- | -------------- |
| point_ipns(point, weight), point(tuple3d)             | creates a conformal point from a tuple3d representing euclidean points, in IPNS representation, with a given weight or implicit with weight==1d. | new CGARoundPointIPNS(new CGAE3Vector(Tuple3d point), new CGAScalar(double weight) |
| pointpair_opns(point1, weight1, point2, weight2), pointpair_opns(point1, point2)     | creates a conformal point pair in OPNS representation from two tuple3d objects representing euclidean points, with given weights or implicit with weight==1.| new CGAOrientedPointPairIPNS(new CGAE3Vector(Tuple3d point1),  new CGAScalar(double weight1), new CGAE3Vector(Tuple3d point2), new CGAScalar(double weight2) |
| line_opns(point1, weight1, point2, weight2), line_opns(point1, point2) | creates a conformal line in OPNS representation, from a tuple3d representing an euclidean point and an other tuple3d representing a second point, with given weights or implicit with weight==1. | new CGALineOPNS(new CGAE3Vector(Tuple3d point1), new CGAScalar(double weight1), new CGAE3Vector(Tuple3d point2). new CGAScalar(double weight2)) |
| sphere_ipns(point1, radius, weight), sphere_ipns(point, radius) | creates a conformal sphere in IPNS representation based on the euclidean parameters center and radius and optional a weight | new CGASphereIPNS(new CGAE3Vector(Tuple3d point1), new CGAScalar(double radius), new CGAScalar(double weight)) |
| plane_ipns(normal, dist, weight), plane_ipns(normal, dist) | creates a conformal plane in IPNS representation, from a 3d-tuple defining the normal vector of the plane and another and a double defining the distance to the origin (Hesse-Normal-form) | new CGAPlaneIPNS(new CGAE3Vector(Tuple3d normal), new CGAScalar(double dist), new CGAScalar(double weight)) |
| circle_opns(point1, weight1, point2, weight2, point3, weight3), circle_opns(point1, point2, point3)  | creates a conformal circle in OPNS representation, based on three points | new CGAOrientedCircleOPNS(new CGAE3Vector(Tuple3d point1), new CGAScalarVector(double weight1), new CGAE3Vector(Tuple3d point2), new CGAScalar(double weight2), new CGAE3Vector(Tuple3d point3), new CGAScalar(double weight3)) |
| tangent_opns(point1, weight1, direction, weight2), tangent_opns(point1, weight1, direction, weight2)  | creates a tangent bivector in OPNS representation, based on a point and a direction | new CGATangentBivector(new CGAE3Vector(Tuple3d point), new CGAScalarVector(double weight1), new CGAE3Vector(Tuple3d direction), new CGAScalar(double weight2)) |

#### Additional functions (3) to create transformations (for more convenience only)

| symbol                   | description | implementation |
| :----------------------- | ----------- | -------------- |
| translator(tuple3d)      | creates a translation from an 3d-tuple | createTranslation(tuple3d) |
| rotator(tuple3d, double) | creates a rotatio from an 3d-tuple representing the rotation axis and a double representing the angle in radian | createTranslation(tuple3d) |

### Symbols
#### Base vector symbols
| symbol           | latex        | Unicode      | description | implementation |
| :--------------: | ------------ | ------------ | ----------- | -------------- |
| &#x03B5;&#x2080; | \textepsilon | \u03B5\u2080 | base vector representing the origin | createOrigin(1d) |
| &#x03B5;&#x1D62; | \textepsilon | \u03B5\u1D62 | base vector representing the infinity | createInf(1d) |
| &#x03B5;&#x2081; | \textepsilon | \u03B5\u2081 | base vector representing x direction | createEx(1d) |
| &#x03B5;&#x2082; | \textepsilon | \u03B5\u2082 | base vector representing y direction | createEy(1d) |
| &#x03B5;&#x2083; | \textepsilon | \u03B5\u2083 | base vector representing z direction | createEz(1d) |

### Further symbols (for more convenience only)
| symbol           | latex         | Unicode      | description | implementation |
| :--------------: | ------------- | ------------ | ----------- | -------------- |
| &#x03B5;&#x208A; | \textepsilon | \u03B5\u208A |  | &#x03B5;&#x2080; + 0.5&#x03B5;&#x1D62;  | 
| &#x03B5;&#x208B; | \textepsilon | \u03B5\u208B |  | 0.5&#x03B5;&#x1D62; - &#x03B5;&#x2080; | 
| &#x03C0;         | \pi           | \u03C0       | Ludolphs- or circle constant | Math.PI |
| &#x221E;         |               | \u221E       | corresponding to infinity vector in Dorst, Fontijne & Mann 2007 | 2&#x03B5;&#8320;  |
| &#x006F;         | o             | \u006F       | corresponding to origin vector in Dorst, Fontijne & Mann 2007 | 0.5&#x03B5;&#7522;  |
| &#x006E;         | n             | \u006E       | corresponding to infinity vector in Doran & Lasenby | &#x03B5;&#7522;  |
| &#x00F1;         |               | \u00F1       | corresponding to origin vector in Doran & Lasenby | -2&#x03B5;&#8320; |
| &#x0045;&#x2080; |               | \u0045\u2080 | Minkovsky bi-vector | &#x03B5;&#7522; &#x2227; &#x03B5;&#8320;|
| &#x0045;&#x2083; |               | \u0045\u2083 | Euclidean pseudoscalar | &#x03B5;&#x2081; &#x2227; &#x03B5;&#x2082; &#x2227; &#x03B5;&#x2083;     |
| &#x0045;         |               | \u0045       | Pseudoscalar | &#x03B5;&#x2080; &#x2227; &#x03B5;&#x2081; &#x2227; &#x03B5;&#x2082; &#x2227; &#x03B5;&#x2083; &#x2227; &#x03B5;&#x1D62; |

### Useful equations

&#x03B5;&#x2080;&#x0045;&#x2080;=-&#x03B5;&#x2080;, &#x0045;&#x2080;&#x03B5;&#x2080;=&#x03B5;&#x2080;, &#x03B5;&#x1D62;&#x0045;&#x2080;=&#x03B5;&#x1D62;, &#x0045;&#x2080;&#x03B5;&#x1D62;=-&#x03B5;&#x1D62;, &#x0045;&#x2080;&#x00B2;=1


## Important formulae
### Formulae to compose conformal geometric objects

#### Geometric objects in outer product null space representation
| description | formula | grade | class |
| :---------- | :------ | :----| :-------- |
| Point pair from  two conformal points (p1, p2) | p1&#8743;p2 | 2 | round |
| (Flat) Finite-infinite point pair or Flat point from  one conformal point (p) | p&#8743;&#x03B5;&#7522; | 2 | flat |
| Circle from three conformal Points (p1, p2, p3) | p1&#8743;p2&#8743;p3 | 3 |  round |
| Line from two conformal planes (p1, p2) | p1&#8743;p2&#8743;&#x03B5;&#7522; | 3 | flat |
| Sphere from four conformal points (p1, p2, p3, p4) | p1&#8743;p2&#8743;p3&#8743;p4| 4 |  round |
| Plane from three conformal points (p1, p2, p3) | p1&#8743;p2&#8743;p3&#8743;&#x03B5;&#7522;| 4 | flat |

The conformal points in the table above have to be given in inner product null space represenation.

#### Geometric objects in inner product null space representation (dual)
| description | formula | grade |
| :---------- | :------ | :----|
| Point from euclidian vector (x) | x+0.5x&sup2;&#x03B5;&#7522;+&#x03B5;&#8320; | 1 |
| Sphere from conformal point (P) and radius (r) | P-0.5r&sup2;&#x03B5;&#7522; | 1 |
| Plane from euclidian normal vector (n) and distance to origin (d) | n+d&#x03B5;&#7522; | 1 |
| Circle from two conformal spheres (s1, s2) | s1&#8743;s2 | 2 |
| Line from two conformal planes (p1, p2) | p1&#8743;p2 | 2 |
| Point pair from  three conformal spheres (s1, s2, s3) | s1&#8743;s2&#8743;s3 | 3 |

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
| Matrix free implementation of the inverse | x&#x207B;&#x00B9; =  (x&#x2020; x&#x5e; x&#x02DC; negate14(x)(x x&#x2020; x&#x5e; x&#x02DC;))/(x x&#x2020; x&#x5e; x&#x02DC; negate14(x) (x x&#x2020; x&#x5e; x&#x02DC;)) |

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
| projection | (A &#x230B; B) B&#x207B;&#x00B9; | Projection of A onto B |
| rejection | (A &#x2227; B) B&#x207B;&#x00B9; | Rejection of A from B |
| duality | (A &#x230B; B)* = A ∧ B* | |
| duality | (A ∧ B)* = A &#x230B; B* | |
|| A &#x230B; (B &#x230B; C) = (A ∧ B) &#x230B; C ||
|| (A &#x230B; B) &#x230B; C = A ∧ (B &#x230B; C) | if C contains A|
|down projection| (&#x03B5;&#x1D62; ∧ &#x03B5;&#x2080;) &#x230B; (X ∧ (&#x03B5;&#x1D62; ∧ &#x03B5;&#x2080;))| extracts the pure euclidean part of the given multivector |
