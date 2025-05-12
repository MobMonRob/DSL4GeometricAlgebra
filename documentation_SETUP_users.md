# Set up the project using the setup script
## Prerequisites
For the first step, your OS needs to support the `apt` package manager. **If it doesn't, make sure you have the packages from the first step installed on your machine.**

_If apt is not supported, you need to have these installed:_
<details>
<summary>Prerequisites</summary>

- git
- wget
- tar
- build-essential
- mingw-w64
- mingw-w64-tools
- swig

</details>

## Usage
```shell
./setup.sh /path/to/where/the/dependencies/should/be/installed
```

You can run the script by executing the command above. **It is recommended to supply a path where the folders for the dependencies should be created**. If no path is supplied, they will be created in the folder where the script has been executed from.

The setup takes you through the following steps. If you have already done a step, you can skip it by typing `s [+ Enter/Return]` or cancel the script altogether by using `Cmd + C` when prompted. The step will be executed after 5 seconds without input or as soon as you hit `Enter/Return`.

### Steps
1. **Installation of dependencies**
	- This step installs the dependencies mentioned in the [Prerequisites section](#Prerequisites) 
		>**_NOTE:_** You need to have root rights to execute this step! **The other steps don't need it.**
2. **Installation of GraalVM and Maven**
	- This steps downloads and installs compatible versions of GraalVM and Maven. 
		>**_NOTE:_** If you don't skip this step, these versions will be used for the build step, even if you have other versions of GraalVM or Maven installed on your system.
3. **Building of the dependencies**
	- The dependencies are built in the right order. 
	- After this step, you should be able to build [the project](.) without issues.
		>**_NOTE:_** Make sure to use the same Maven installation when building the project as you did when building the dependencies. For example, Netbeans uses a bundled version of Maven, which will not have the dependencies in cache. 
		<details>
		<summary>How to change which Maven installation Netbeans uses</summary>
		
		Go to `Tools>Options>Java>Maven` inside of Netbeans and change the "Maven Home" to the path of the correct Maven folder. 
		
		</details>
			
## Notes
The script does **not** install the [syntax highlighting](https://github.com/orat/netbeans-ocga/tree/master) or [insertion of special characters](https://github.com/orat/netbeans-cgasymbols) plugins for Netbeans. 

<details>
<summary>How to install the Netbeans plugins</summary>

To install them, clone the repositories and use `mvn install` inside of them to build `nbm` files, which are then located in the `./target/` directories of the respective projects. The plugins can then be installed by going to `Tools>Plugins>Downloaded>Add Plugin` inside of Netbeans and then choosing the respective `nbm` files.

</details>
