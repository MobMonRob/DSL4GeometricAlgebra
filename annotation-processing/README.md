# Common
For more usage examples, see [DSL4GeometricAlgebra-Test](../DSL4GeometricAlgebra-Test/).

- The [@CGA annotation](#cgaarbitrary-cga-code) generates a wrapper to invoke the provided cga code with the interface that the annotated method defines. Only a valid function body is allowed as parameter, but not entire function definitions.
- The [@CGAPATH annotation](#cgapath-annotation) generates a wrapper to invoke the cga code within the provided .ocga file with the interface that the annotated method defines. Arbitrary valid CGA code is allowed within these files.


### Usage
```
public interface <Name> {
	<CGA or CGAPATH Annotation>
	<Return type> <Methodname>(<Parameter-List>);

	// ...
}
```

Return type: \
Same as a return type of one of the methods from the [Result class](../GeomAlgeLang/src/main/java/de/dhbw/rahmlab/geomalgelang/api/Result.java).

Each Parameter consists of: \
`<Type> <Identifier>`

Parameter.Identifier: \
`<Name of CGA Variable>_<Method name from Arguments class><Arbitrary String>`

Each Parameter.Type must match the type or a subtype of the (n+1)-th parameter of the corresponding “Method name from [Arguments class](../GeomAlgeLang/src/main/java/de/dhbw/rahmlab/geomalgelang/api/Arguments.java)”, with n as the n-th occurence of the same “Name of the CGA Variable”.

Dataflow will be: \
Parameters -> Methods of Arguments.java -> Interpreter execution -> Method of Result.java

The generated code will be deposited in a subpackage named "gen" and a class with the same name as the interface and the suffix "Gen".


### Features
Changes to Arguments.java and Result.java are automatically taken into account by the annotation-processor. \
However, GeomAlgeLang needs to be rebuild for that.


### Peculiarities
- In Result.java it could make sense to add methods with the same return types as existing ones or to add methods with parameters. \
However this is not supported by the annotation-processor. \
A solution to work around that in these cases is to create new classes which themselves hold or compute the desired return types and to use these new classes as return types.

- There is currently no optimization for many consecutive invokations of generated code. \
If this causes performance issues, the following ideas to fix that could be considered:
	- Caching of the internal Program instance (speed up only for consecutive executions of the same CGA code)
	- Sharing the same inner Context instance between multiple internal Program instances.

- Due to a workaround to support Netbeans' in-line error reporting, in order to support additional Parameter.Type's in the user of the annotation it is necessary to add a dependency to the annotation-processor which contains the defintion of these types.


### Dependency
To use it as a dependency, add the following lines to the depencencies list in your Maven pom.xml:
```xml
<dependency>
	<groupId>de.dhbw.rahmlab</groupId>
	<artifactId>annotation-processing</artifactId>
	<version>1.0-SNAPSHOT</version>
	<scope>provided</scope>
	<exclusions>
		<exclusion>
			<artifactId>*</artifactId>
			<groupId>*</groupId>
		</exclusion>
	</exclusions>
</dependency>

<dependency>
	<groupId>de.dhbw.rahmlab</groupId>
	<artifactId>GeomAlgeLang</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```


# `@CGA("<Arbitrary CGA Code>")`
### Example usage
```java
package de.dhbw.rahmlab.geomalgelang.test.common;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
public interface Wrapper {
	@CGA("normalize(a b)")
	double targetMethod1(double a_scalar, double b_scalar);
}
```

Arguments.java contains the method:
```java
public Arguments scalar(String argName, double scalar)
```

Result.java contains the method:
```java
public double decomposeScalar()
```


### Example generated code usage
```java
import de.dhbw.rahmlab.geomalgelang.test.common.gen.WrapperGen;
public class AnnotationTest {
	void annotationTest() {
		double out = WrapperGen.INSTANCE.targetMethod1(1.0, 2.0);
		System.out.println("out: " + out);
	}
}
```


# CGAPATH Annotation
For testing, place the following code lines into your pom.xml at the appropriate place within the xml-node "build". To be able to use the files in the non-test build, adjust them accordingly.
```xml
<testResources>
	<testResource>
		<directory>src/test/cga</directory>
	</testResource>
</testResources>
```


## `@CGAPATH`
To use this variant, place your .ocga file within the resources directory in the same package as the Java file which contains the annotated method. \
The file name before the filename extension has to be the same as the name of the annotated method.


## `@CGAPATH("<Path to .ocga file>")`
To use this variant, place your .ocga file within an arbitrary place in the resources directory. \
A leading slash ("/") will indicate an absolute path within the directory provided in the pom.xml. \
Otherwise, especially with a leading dot-slash ("./"), it will be a path relative to the package of the Java file which contains the annotated method.

