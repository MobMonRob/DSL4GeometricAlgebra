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
| Name | implementation class | setting from outside possible |
| :-------- | :---- | ----- |
| double | Double | x |
| Tuple3d | org.jogamp.vecmath.Tuple3d | x |
| Multivector | de.orat.math.cga.impl1.CGA1Multivector | |

## Operators

### Dual operators
#### Base operators
| precedence | symbol | latex | unicode | name | implementation | hints |
| :--------: | :----: | ------- | ----- | ---- | -------------- | ----- |
| 3 | space  |  | \u0020 | geometric product | multivector1.gp(multivector2) | Exactly one space character is interpreted as the operator. |
| 3 | &#8901;   | \cdot | \u22C5 | inner product | multivector1.ip(multivector2, LEFT_CONTRACTION) | In the default configuration equal to left contraction. |
| 3 | &#8743; | \wedge | \u2227 | outer product | multivector1.op(multivector2), not used for double, for tuple3d it makes sense but actually no implementation is available | |
| 2 | &#42;  | * | \u002A | scalar product | multivector1.scp(multivector2), double1 * double2, Multivector.createBasisVector(int, double) for e.g. &#949;&#8321; * double or double * &#949;&#8321; tuple3d1.add(tuple3d2) | |
| 1 | &#43;  | + | \u002B | sum | multivector1.add(multivector2), double1 + double2 | |
| 1 | &#45; | - | \u002D| difference | multivector1.sub(multivector2), double1 - double2 | |

#### Additional operators (for more convenience only)
| precedence | symbol | latex | unicode | description | implementation |
| :--------: | :----: | ------- | ----- | ----------- | -------------- |
| 3 | &#8746;   | \cup  | \u222A | meet | multivector1.meet(multivector2) |
| 3 | &#8745;   | \cap  | \u2229 | join | multivector1.join(multivector2) or multivector2* &#8901; multivector1 or (multivector2* &#8743; multivector1*)*|
| 3 | &#8970; | \llcorner | \u230B | left contraction | multivetor1.ip(multivector2, LEFT_CONTRACTION) |
| 3 | &#8971; | \lrcorner | \u230A | right contraction | multivector1.ip(multivector1, RIGHT_CONTRACTION) |
| 3 | &#8744; | \vee | \u2228 | | multivector1.vee(multivector2) or (multivector1* &#8743; multivector2*)* |
| 2 | &#47;  | \StrikingThrough | \u002F | division | multivector1.div(multivector2), double.div(double) |

### Monadic operators (placed all on right side)
| precedence | symbol        | latex                         | unicode      | description |
| :--------: | :-----------: | ----------------------------- | ------------ | ----------- |
| 4          | &#8315;&#185; | \textsuperscript{-1}          | \u207B\u00B9 | general inverse |
| 4          | *             | \textsuperscript{*}           | \u002A       | dual |
| 4          | &#8315;*      | \textsuperscript{-*}          | \u207B\u002A | undual |
| 4          | &#732;        | \textsuperscript{\tilde}      | \u02DC       | reverse |
| 4          | &#8224;       | \textsuperscript{\textdagger} | \u2020       | clifford conjugate |

### Buildin functions

#### Base functions
| precedence | symbol | latex | description | implementation |
| :--------: | ------:| ----- | ----------- | -------------- |
| 4 | exp()         | \exp{} | exponential | multivector.exp() |
| 4 | involute()    |  | grade inversion | multivector.gradeInversion() |
| 4 | abs()         |  | absolute value | Math.abs(double) |
| 4 | sqrt()         |  | square root | Math.sqrt(double) |
| 4 | atan2()         |  | arcus tansgens 2 | Math.atan2(double, double) |

#### Additional functions (for more convenience only)
| precedence | symbol | latex | description | implementation |
| :--------: | ------:| ----- | ----------- | -------------- |
| 4 | reverse(multivector)     |  \textsuperscript{\tilde} | reverse | multivector.reverse() |
| 4 | conjugate(multivector)   | \textsuperscript{\textdagger} | clifford conjugate | multivector.conjugate() |
| 4 | normalize(multivector)        | | normalize | multivector.unit() |

#### Additional functions (2) to define geometric objects (for more convenience only)

Outer product null space representations are called dual. Corresponding regular expressions are in the inner product null space representations. Be careful in the "older" literature this is often defined reverse.

| precedence | symbol | description | implementation |
| :--------: | :------ | ----------- | -------------- |
| 4 | point(tuple3d)        |  creates a conformal point from an 3d-tuple | createPoint(tuple3d) |
| 4 | dualPointPair(tuple3d, tuple3d)        |  creates a conformal dual point pair from two 3d-tuple | createDualPointPair(tuple3d,tuple3d) |
| 4 | dualLine(tuple3d, tuple3d)        |  creates a conformal line from 3d-tuples defining a point and a direction or a second point | createDualLine(tuple3d, tuple3d) |
| 4 | dualSphere(tuple3d, tuple3d, tuple3d, tuple3d)        |  creates a conformal sphere from four 3d-tuple | createDualSphere(tuple3d, tuple3d, tuple3d, tuple3d) |
| 4 | sphere(tuple3d, double)        |  creates a conformal sphere from a 3d-tuple and the radius| createSphere(tuple3d, double) |
| 4 | plane(tuple3d, tuple3d)        |  creates a conformal plane from a 3d-tuple defining a point on the plane and another 3d-tuple defining the normal vector | createPlane(tuple3d, tuple3d) |

### Base vector symbols
| symbol        | latex         | Unicode      | description |
| :-----------: | ------------- | ------------ | ----------- |
| o             |               | \u006F       | base vector representing the origin |
| &#8734;       |               | \u221E       | base vector representing the infinity |
| &#949;&#8321; | \textepsilon  | \u03B5\u2081 | base vector representing x direction |
| &#949;&#8322; | \textepsilon  | \u03B5\u2082 | base vector representing y direction |
| &#949;&#8323; | \textepsilon  | \u03B5\u2083 | base vector representing z direction |

## Important formulae
| description | formula |
| :---------- | :------ |
| location of a round or a tangent represented in 3d coordinates | -0.5 (X &#8734; X) / sqr(&#8734; &#8901; X) |
| direction vector of a line represented in 3d coordinates | (L &#8901; o) &#8901; &#8734; |

