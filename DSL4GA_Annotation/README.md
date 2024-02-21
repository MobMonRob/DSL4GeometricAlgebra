The `@GAFILES` annotation generates a wrapper to conveniently invoke ga code.

For more usage examples, see [DSL4GA_Test](../DSL4GA_Test/).


## Variants
1. `@GAFILES(<Class which extends iProgramFactory>.class)`
2. `@GAFILES(value = <Class which extends iProgramFactory>.class, path = <Either an absolute or a relative path>)`

The 1. variant will default the path to the package of the annotated interface. This is equivalent to `path = "./"`.


## Usage
```
@GAFILES(<Variant>)
public interface <Name> {
	List<SparseDoubleColumnVector> <Filename without ending>(<Parameter-List>);

	// ...
}
```

- Each Parameter consists of: `SparseDoubleColumnVector <Arbitrary identifier>`
- The annotation-processor will generate a class `<Filename with uppercase first letter>Program` within a subpackage of the annotated interface `.gen.<toLowerCase(Annotated interface name)>`.
- Overloads of the methods in the annotated interface are prohibited.
- The given class must have been compiled beforehand.
- Changes of the annotation require a clean rebuild to come into effect.
- Absolute paths start with a leading "`/`".
- Valid paths are package names with "`/`" as a separator instad of "`.`".
- Relative paths start in the package of the annotated interface and lead to the package of the .ocga file.
- Place your .ocga file within an arbitrary place in the resources directory.


## pom.xml
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

For testing, place the following code lines into your pom.xml at the appropriate place within the xml-node "build". The given directory will be the package root. To be able to use the files in the non-test build, adjust them accordingly.
```xml
<testResources>
	<testResource>
		<directory>src/test/cga</directory>
	</testResource>
</testResources>
```

