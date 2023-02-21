## Annotation usage
```
public interface <Arbitrary Name> {
	@CGA("<Arbitrary CGA Code>")
	<Return type> <arbitrary methodname>(<Parameter-List>);

	// ...
}
```
Return type: \
Same as a return type of one of the methods from the Result class: \
https://github.com/MobMonRob/DSL4GeometricAlgebra/blob/fabian/GeomAlgeLang/src/main/java/de/dhbw/rahmlab/geomalgelang/api/Result.java

Each Parameter consists of: \
`<Type> <Identifier>`

Parameter.Identifier: \
`<Name of CGA Variable>_<Method name from Arguments class><Arbitrary String>`

Each Parameter.Type must match the type of the (n+1)-th parameter of the corresponding “Method name from Arguments class”, with n as the n-th occurence of the same “Name of the CGA Variable”. \
https://github.com/MobMonRob/DSL4GeometricAlgebra/blob/fabian/GeomAlgeLang/src/main/java/de/dhbw/rahmlab/geomalgelang/api/Arguments.java

Dataflow will be: \
Parameters -> Methods of Arguments.java -> Interpreter execution -> Method of Result.java

The generated code will be in a subpackage named "gen" and a class with the same name as the interface and the suffix "Gen".


## Example annotation usage
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


## Example generated code usage
```java
import de.dhbw.rahmlab.geomalgelang.test.common.gen.WrapperGen;
public class AnnotationTest {
	void annotationTest() {
		double out = WrapperGen.INSTANCE.targetMethod1(1.0, 2.0);
		System.out.println("out: " + out);
	}
}
```


## Features
Changes to Arguments.java and Result.java are automatically taken into account by the annotation-processor. \
However, GeomAlgeLang needs to be rebuild for that.


## Particularities
In Result.java it could make sense to add methods with the same return types as existing ones or to add methods with parameters. \
However this is not supported by the annotation-processor. \
A solution to work around that in these cases is to create new classes which themselves hold or compute the desired return types and use these new classes as return types.

There is currently not optimization for many consecutive invokations of generated code. \
If this causes performance issues, we could consider following ideas to fix that: \
- Caching of the internal Program instance (speed up only for consecutive executions of the same CGA code)
- Sharing the same inner Context instance between multiple internal Program instances.


## Dependency
To use it as a dependency, add the following lines to the depencencies list in your maven pom.xml:
```xml
<dependency>
	<groupId>de.dhbw.rahmlab</groupId>
	<artifactId>annotation-processing</artifactId>
	<version>${annotation-processing.version}</version>
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
