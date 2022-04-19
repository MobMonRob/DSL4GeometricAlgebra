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

## Operators

### Dual operators
#### Base operators
| precedence | symbol | latex | unicode | name | implementation | hints |
| :--------: | :----: | ------- | ----- | ---- | -------------- | ----- |
| 3 | space  |  | \u0020 | geometric product | gp(Multivector) | Exactly one space character is interpreted as the operator. |
| 3 | &#8901;   | \cdot | \u22C5 | inner product | ip(Multivector, LEFT_CONTRACTION) | In the default configuration equal to left contraction. |
| 3 | &#8743; | \wedge | \u2227 | outer product | op(Multivector) | |
| 2 | &#42;  | * | \u002A | scalar product | scp(Multivector) | |
| 1 | &#43;  | + | \u002B | sum | add(Multivector) | |
| 1 | &#45; | - | \u002D| difference | sub(Multivector) | |

#### Additional operators (for more convenience only)
| precedence | symbol | latex | unicode | description | implementation |
| :--------: | :----: | ------- | ----- | ----------- | -------------- |
| 3 | &#8746;   | \cup  | \u222A | meet | meet(Multivector) |
| 3 | &#8745;   | \cap  | \u2229 | join | join(Multivector) |
| 3 | &#8970; | \llcorner | \u230B | left contraction | ip(Multivector, LEFT_CONTRACTION) |
| 3 | &#8971; | \lrcorner | \u230A | right contraction | ip(Multivector, RIGHT_CONTRACTION) |
| 3 | &#8744; | \vee | \u2228 | (X* &#8743; Y*)* | vee(Multivector) |
| 2 | &#47;  | \StrikingThrough | \u002F | division | div(Multivector) |

### Monadic operators (placed all on right side)
| precedence | symbol        | latex                         | Unicode      | description |
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
| 4 | exp()         | \exp{} | exponential | exp(Multivector) |
| 4 | involute()    |  | grade inversion | gradeInversion() |

#### Additional functions (for more convenience only)
| precedence | symbol | latex | description | implementation |
| :--------: | ------:| ----- | ----------- | -------------- |
| 4 | reverse()     |  \textsuperscript{\tilde} | reverse | reverse() |
| 4 | conjugate()   | \textsuperscript{\textdagger} | clifford conjugate | conjugate() |
| 4 | normalize()        | | normalize | unit() |

### Base vector symbols
| symbol        | latex         | Unicode      | description |
| :-----------: | ------------- | ------------ | ----------- |
| o             |               | \u006F       | base vector representing the origin |
| &#8734;       |               | \u221E       | base vector representing the infinity |
| &#949;&#8321; | \textepsilon  | \u03B5\u2081 | base vector representing x direction |
| &#949;&#8322; | \textepsilon  | \u03B5\u2082 | base vector representing y direction |
| &#949;&#8323; | \textepsilon  | \u03B5\u2083 | base vector representing z direction |
