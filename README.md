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


## Types
| Name | implementation class | setting from outside possible | hints |
| :-------- | :---- | ----- | ------|
| double | Double | x | |
| Tuple3d | org.jogamp.vecmath.Tuple3d | x | Automatic casting to a multivector is not possible because multiple object types are based on a Tuple3d, e.g. a point, a sphere together with an additional double etc. |
| Quat4d | org.jogamp.vecmath.Quat4d | x | Casting to a multivector only. No other operations possible inside the language. 
| Multivector | de.orat.math.cga.impl1.CGA1Multivector | | |

## Operators

### Dual operators
#### Base operators
| precedence | symbol | latex | unicode | name | implementation | hints | 
| :--------: | :----: | ------- | ----- | ---- | -------------- | ----- |
| 3 | space  |  | \u0020 | geometric product | multivector1.gp(multivector2) | Exactly one space character is interpreted as the operator. |
| 3 | &#8901;   | \cdot | \u22C5 | inner product | multivector1.ip(multivector2, RIGHT_CONTRACTION), new Tuple3d(tuple3d1.x*tuple3d2.x, tuple3d1.y*tuple3d2.y, tuple3d1.z*tuple3d2.z) | In the default configuration equal to left contraction (corresponding to Ganja.js). But this looks to be incompatible with some formulas in [Kleppe], which work only with the usage of right contraction. |
| 3 | &#8743; | \wedge | \u2227 | outer product | multivector1.op(multivector2), not used for double, for tuple3d it makes sense but actually no implementation is available | |
| 2 | &#42;  | * | \u002A | scalar product | multivector1.scp(multivector2), double1 * double2, Multivector.createBasisVector(int, double) for e.g. &#949;&#8321; * double or double * &#949;&#8321; tuple3d.scale(double) | |
| 1 | &#43;  | + | \u002B | sum | multivector1.add(multivector2), double1 + double2, tuple3d1.add(tuple3d2) | |
| 1 | &#45; | - | \u002D| difference | multivector1.sub(multivector2), double1 - double2, tuple3d1.sub(tuple3d1) | |

#### Additional operators (for more convenience only)
| precedence | symbol | latex | unicode | description | implementation |
| :--------: | :----: | ------- | ----- | ----------- | -------------- |
| 3 | &#8746;   | \cup  | \u222A | meet | multivector1.meet(multivector2) |
| 3 | &#8745;   | \cap  | \u2229 | join | multivector1.join(multivector2) or multivector2* &#8901; multivector1 or (multivector2* &#8743; multivector1*)*|
| 3 | &#8970; | \llcorner | \u230B | right contraction | multivetor1.ip(multivector2, RIGHT_CONTRACTION) |
| 3 | &#8971; | \lrcorner | \u230A | left contraction | multivector1.ip(multivector1, LEFT_CONTRACTION); where the grade operator for negative grades is zero. This implies that `something of higher grade cannot be contracted onto something of lower grade'. |
| 3 | &#8744; | \vee | \u2228 | regressive product | multivector1.vee(multivector2) or (multivector1* &#8743; multivector2*)* |
| 2 | &#47;  | \StrikingThrough | \u002F | division | multivector1.div(multivector2), double.div(double) |

### Monadic operators (placed all on right side)
#### Base monadic operators
| precedence | symbol        | latex                         | unicode      | description | implementation |
| :--------: | :-----------: | ----------------------------- | ------------ | ----------- | -------------- |
| 4          | &#8315;&#185; | \textsuperscript{-1}          | \u207B\u00B9 | general inverse | multivector.generalInverse() |
| 4          | *             | \textsuperscript{*}           | \u002A       | dual | multivector.dual() |
| 4          | &#732;        | \textsuperscript{\tilde}      | \u02DC       | reverse | multivector.reverse() |
| 4          | &#8224;       | \textsuperscript{\textdagger} | \u2020       | clifford conjugate | multivector.conjugate() |

There exist three types of involution operations: Space inversion, reversion and the combination of both the clifford conjugation.

#### Additional monatic operators (for more convenience only) 
| precedence | symbol        | latex                         | unicode      | description | implementation |
| :--------: | :-----------: | ----------------------------- | ------------ | ----------- | -------------- |
| 4          | &#8315;*      | \textsuperscript{-*}          | \u207B\u002A | undual | multivector.undual() or -multivector.dual() |
| 4          | &sup2; | | \u00B2 | square | multivector.gp(multivector), sqr(double) |

### Composite operators
| precedence | symbol        | latex                         | unicode      | description | implementation |
| :--------: | :-----------: | ----------------------------- | ------------ | ----------- | -------------- |
| 4          | &#60;Multivector&#8322;  |                               |              | grade extraction | multivector.extractGrade(double)   |

### Buildin functions

#### Base functions
| precedence | symbol | latex | description | implementation |
| :--------: | :------ | ----- | ----------- | -------------- |
| 4 | exp()         | \exp{} | exponential | multivector.exp() |
| 4 | involute()    |  | grade inversion | multivector.gradeInversion() |
| 4 | abs()         |  | absolute value | Math.abs(double) |
| 4 | sqrt()         |  | square root | Math.sqrt(double) |
| 4 | atan2()         |  | arcus tansgens 2 | Math.atan2(double, double) |

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

### Base vector symbols
| symbol        | latex         | Unicode      | description |
| :-----------: | ------------- | ------------ | ----------- |
| o             |               | \u006F       | base vector representing the origin |
| &#8734;       |               | \u221E       | base vector representing the infinity |
| &#949;&#8321; | \textepsilon  | \u03B5\u2081 | base vector representing x direction |
| &#949;&#8322; | \textepsilon  | \u03B5\u2082 | base vector representing y direction |
| &#949;&#8323; | \textepsilon  | \u03B5\u2083 | base vector representing z direction |

## Important formulae
### Formulae to create conformal objects
| description | formula |
| :---------- | :------ |
| Dual point from four conformal spheres (s1, s3, s3, s4) | s1 &#8743; s2 &#8743; s3 &#8743; s4 |
| Line from two conformal planes (p1, p2) | p1 &#8743; p2 |
| Circle from two conformal spheres (s1, s2) | s1 &#8743; s2 |
| Point pair from  three conformal spheres (s1, s2, s3) | s1 &#8743; s2 &#8743; s3 |

### Formulae to decompose conformal object representations
| description | formula |
| :---------- | :------ |
| Location of a round (X) or a tangent (X) represented in 3d coordinates | -0.5 (X &#8734; X) / (&#8734; &#8901; X)&sup2; |
| Direction vector (attitude) of a dual line (L*) represented as 3d coordinates of (&#949;&#8321;, &#949;&#8322;, &#949;&#8323;). | (L* &#8901; o) &#8901; &#8734; |

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
