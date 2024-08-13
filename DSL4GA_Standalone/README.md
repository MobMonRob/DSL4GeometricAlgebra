# The DSL4GA language standalone build

By default building with `mvn package` will build a jvm standalone version of DSL4GA language that uses the JDK on the JAVA_HOME.
By running `mvn package -Pnative` it will also automatically create a native image of the language.
To use the standalone build either run `target/ga` or `target/ganative` depending on whether the native build was created.

The standalone language starter adds the options `-debug`, `-dump`, `-lsp` and `-disassemble`.
         
