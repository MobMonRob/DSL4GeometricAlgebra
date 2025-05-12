# Arrays
Arrays are used to store multiple multivectors, which can be accessed and changed by addressing them with their indices, inside of a single variable. Since all data types are immutable (except if they are used [in loops](documentation_LOOPS_users.md)), the only option to change a multivector, while keeping its way of addressing it, is to put it in an array. This documentation should contain enough information to fully understand and use arrays when writing programs with the DSL. For more detailed information or when developing on the project, it is recommended to also refer to the [developer documentation](documentation_ARRAYS_developers.md).

# Indices
Because every variable is either a multivector or an array, **you cannot use variables as indices**. You can only use integer literals, the built-in length function of arrays, or simple additions and subtractions between the two as indices. (See [Modification](#modifying-or-setting-array-elements) and the [developer documentation](documentation_ARRAYS_developers.md))

# Usage
## Initializing arrays
>_To initialize an array, you don't have to supply a fixed size or limit how many elements the array can/must have. Instead, you simply supply the initial values, which can then be modified._

- Creating an empty array:
	```
	a[] = {}
	```

	>**_NOTE:_** If you initialize an array this way, you can at first access no elements within it (since it has none), and you can first only set `a[0]`. (See [Modification](#modifying-or-setting-array-elements))
- Creating an array with initial values:
	```
	a[] = {1, 4 + n (2 x), v}
	```

	>**_NOTE:_** If you initialize an array this way, you can access `a[0], a[1], a[2]` and set `a[0], a[1], a[2], a[3]`. (See [Modification](#modifying-or-setting-array-elements))

## Modifying or setting array elements
>_You can only modify or set one array element at a time, except if you assign the multiple return values of a function call to different elements of an array._

- Modifying by direct reassignment, using an integer literal:
	```
	a[0] = 1
	```
- Modifying by calling a function, using an integer literal:
	```
	a[0] = fn()
	```
- Modifying by direct reassignment, using the length of another array:
	```
	a2[] = {1,2,3}
	a[len(a2)] = 1 // Set a[3]
	```
- Modifying by calling a function, using the modified length of another array:
	```
	a2[] = {1,2,3}
	a[len(a2)-1] = fn() // Set a[2]
	```
- Modifying multiple elements by calling a function: 
	```
	a[] = {0, 1, 2, 3}
	a[0], a[3], a[1], a[2] = fn() 
	```
- Setting an array element (= expanding the array)
	```
	a[] = {} // len(a) = 0 now
	a[0] = 1 // len(a) = 1 now
	```
- Setting multiple array elements
	```
	a[] = {} 						// len(a) = 0 now
	a[0], a[1], a[2], a[3] = fn() 	// len(a) = 4 now
	```
	>**_NOTE:_** This only works because the assignments are in the correct order!

## Accessing and returning array elements
>_Accessing array elements works just like modifying them. You can also use the same mathematical operations as with multivectors on them. However, you cannot access or return whole arrays at once._ 

- Simply accessing an array element:
	```
	v = a[0]
	```
- Doing mathematical operations with array elements:
	```
	v2 = a[0] v // Multiplication of a[0] and v
	```
- Reassigning an array element with a modified version of itself:
	```
	a[0] = a[0] 2 // Doubling a[0]
	```
- Returning an array element:
	```
	fn main (){
		a[] = {0}
		a[0]
	}
	```
- Returning a mathematical operation with an array element:
	```
	fn main (){
		a[] = {0}
		2 a[0] 1 // Doubling a[0]
	}
	```