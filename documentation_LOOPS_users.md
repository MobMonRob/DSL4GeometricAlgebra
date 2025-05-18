# Loops
Loops are used to iterate through a code segment multiple times. They utilize an iterator variable which is manipulated after each iteration and can be used inside the code to dynamically access [arrays](documentation_ARRAYS_users.md). In loops, the immutability of the language is lifted, so it is possible to reassign multivectors. This documentation should contain enough information to fully understand and use arrays when writing programs with the DSL. For more detailed information or when developing on the project, it is recommended to also refer to the [developer documentation](documentation_LOOPS_developers.md).

# Usage
## Initializing loops
Loops can be initialized anywhere, including inside another loop (see the [developer documentation](documentation_LOOPS_developers.md)), using the `for` keyword with the following structure:
```
for (VARIABLE_NAME; BEGINNING; ENDING; STEP){
	// loop body
}
```
### Loop parameters
- `VARIABLE_NAME`: The name of the iterator variable. It can only be referenced inside of the loop and can't have the same name as another, already existing, variable. It also can't be reassigned. However, it is allowed to initialize a variable with the same name after the loop.
- `BEGINNING`: The first value the iterator should have. This uses the same syntax as [array indices](documentation_ARRAYS_users.md), which means it can be an integer literal, a [built-in length function of an array](documentation_ARRAYS_users.md), or a simple addition or subtraction between the two. 
- `ENDING`: The **exclusive** limit for the loop. The loop stops **before** the iterator has reached (or exceeded) this value. It has the same syntax as `BEGINNING`. 
- `STEP`: The value which is added to the iterator after each iteration. It also has the same syntax as `BEGINNING`.
- If the loop is contained inside of another loop, the outer loop's iterator can also be used as the inner loop's `BEGINNING`, `ENDING`, or `STEP` value. 

<details>
<summary>Examples</summary>

### Examples
- Simple forward loop:
```
for (i; 0; 3; 1) {
	// i will be:
	// First iteration:		0
	// Second iteration:	1
	// Third iteration: 	2
	// Stop after that, because it would reach the ENDING, 3
}
```

- Simple backwards loop:
```
for (i; 3; 0; -1) {
	// i will be:
	// First iteration:		3
	// Second iteration:	2
	// Third iteration: 	1
	// Stop after that, because it would reach the ENDING, 0
}
```

- Skipping forward loop:
```
a[] = {1, 2, 3, 4, 5}
for (i; 0; len(a)+1; 2) {
	// i will be:
	// First iteration:		0
	// Second iteration:	2
	// Third iteration: 	4
	// Stop after that, because it would reach the ENDING, len(a)+1 = 6
}
```

>**_NOTE:_** Expanding array `a` inside the loop would not change the ending of the loop. The size of the array **before** the loop starts is used as parameter. In nested loops, it is possibble to use the expansion of an array in the outer loop to vary parameters of the inner loop, see [NestedLoopsTest](DSL4GA_Test/src/test/java/de/dhbw/rahmlab/dsl4ga/test/loops/NestedLoopsTest.java).`usingVaryingArrayLengthAsInnerParameter()`.

- Skipping backwards loop:
```
for (i; 3; -4; -3) {
	// i will be:
	// First iteration:		3
	// Second iteration:	0
	// Third iteration: 	-3
	// Stop after that, because it would exceed the ENDING, -4
}
```

- Nested loop:
```
for (i; 1; 4; 1) {
	for (e; 0; i; 1){
		// ENDING will be:
		// Outer loop's first iteration:	1
		// 		-> e will be: 0
		// Outer loop's second iteration:	2
		// 		-> e will be: 0, 1
		// Outer loop's third iteration:	3
		// 		-> e will be: 0, 1, 2
	}
}
```

</details>

## Loop body
> At the moment, there are only certain statements allowed within loops. It is only allowed to create and reassign multivectors, reassign array elements, call functions with one return value and initialize inner loops. Consequently, it is not allowed to create arrays or call functions with more than one return value. This is likely to change in the futurein order to elimintate syntactical differences between loop and function bodies. 

### Reassigning variables
It is allowed to reassign all multivector variables and array elements. After the loop has been executed, they have the last value which was assigned to them in the loop: 

- Reassigning a multivector variable:
```
fn main() {
	v = 0
	for (i; 0; 3; 1) {
		v = v + 1
	}
	v // is now 3
}
```

- Reassigning array elements:
```
fn main() {
	a[] = {0, 1, 2}
	for (i; 0; 3; 1) {
		a[i] = a[i] + 1
	}
	a[0], a[1], a[2] // a is now {1, 2, 3}
}
```

- Expanding an array:
```
fn main() {
	a[] = {} // len(a) = 0
	a2[] = {0, 1, 2, 3}
	for (i; 0; 3; 1) {
		a[i] = a2[i+1]
	}
	a[0], a[1], a[2] // a is now {0, 1, 2}; len(a) = 3
}
```

### Creating variables
It is allowed to create variables inside of loops (as long as they're not arrays). They are then loop-scoped, meaning they can't be accessed outside of the loop in which they were created.
- Loop-scoped variable:
```
fn main() {
	a[] = {1, 2, 3}
	for (i; 2; 5; 1) {
		v = a[i-2] // v is reassigned in each iteration.
		[...]
	}
	// v cannot be accessed here anymore.
	v = a[2] // A new variable with the same name can be created.
	[...]
}
```
- Loop-scoped variables in outer nested loop: 
```
fn main() {
	a[] = {1, 2, 3}
	for (i; 0; 3; 1) {
		v = a[i] // v is reassigned in each iteration.
		for (e; 0; 3; 1){
			v = v a[e] // v can be reassigned inside of the inner loop.
			[...]
		}
		a[i] = v // v has the value it was assigned in the inner loop.
	}
	// v cannot be accessed here anymore.
	v = a[2] // A new variable with the same name can be created.
	[...]
}
```
- Loop-scoped variables in both nested loops: 
```
fn main() {
	a[] = {1, 2, 3}
	for (i; 0; 3; 1) {
		v = a[i] // v is reassigned in each iteration.
		for (e; 0; 3; 1){
			v2 = v 2 // v can be accessed inside of the inner loop.
			[...]
		}
		// v2 can't be accessed here, outside of the inner loop, anymore.
		v2 = v 2 // A new variable with the same name can be created.
	}
	// v and v2 cannot be accessed here anymore.
	v = a[2] // A new variable with the same name can be created.
	[...]
}
```