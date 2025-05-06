import re
from os import listdir
from os.path import isfile, join
import nbformat as nbf

MININDENTS = 5

def extract_java_functions(java_file_path):
	try:
		# Read the file content
		with open(java_file_path, 'r', encoding='utf-8') as file:
			content = file.read()



		# Pattern to match Java method declarations
		# This handles various access modifiers, return types, method names, parameters
		method_pattern = r'(?:public|protected|private|static|final|native|synchronized|abstract|transient)?\s*(?:<.*?>)?\s*(?:(?:[\w\<\>\[\]]+)\s+)+([\w]+)\s*\([^)]*\)\s*(?:throws\s+[\w\s,]+)?\s*\{(?:[^{}]|(?:\{(?:[^{}]|(?:\{[^{}]*\}))*\}))*\}'

		# Find all method matches
		methods = re.finditer(method_pattern, content)

		# Extract function name and content
		extracted_functions = []
		for method in methods:
			method_content = method.group(0)
			method_name = re.search(r'([\w]+)\s*\(', method_content).group(1)
			extracted_functions.append((method_name, method_content))

		return extracted_functions

	except Exception as e:
		print(f"Error processing file {java_file_path}: {e}")
		return []


def dsl_to_python_from_original(dsl, name):
	safetybuffer = "0, " * MININDENTS
	safetybuffer=safetybuffer[:-2]
	lines = dsl.strip().splitlines()
	result = []
	indent_stack = [""]  # Start ohne Indent
	current_indent = ""
	index = 1
	lastLine = len(lines)
	function = "main"
	returns = ""

	for line in lines:
		index+=1
		stripped = line.strip()

		if stripped.startswith("fn main"):
			current_indent = "    "
			indent_stack.append(current_indent)
			function = name
		elif stripped == "}":
			if len(indent_stack) > 1:
				indent_stack.pop()
			current_indent = indent_stack[-1]
		elif stripped.startswith("for"):
			parts = stripped[stripped.find("(")+1:stripped.rfind(")")].split(";")
			var, start, end, step = map(str.strip, parts)
			result.append(current_indent + f"for {var} in range({start}, {end}, {step}):")
			current_indent += "    "
			indent_stack.append(current_indent)
		elif "[]=" in stripped.replace(" ", ""):
			var, val = stripped.replace("[]", "").split("=", 1)
			val = val.replace("{}", "{0}")
			val = val.replace("{", "[").replace("}", f", {safetybuffer}]")
			result.append(current_indent + f"{var.strip()} = {val.strip()}")
		elif index==lastLine:
			returns = stripped
		elif stripped:
			result.append(current_indent + stripped)

	return function, "\n".join(result).replace("//", "#"), returns

def extractSingleFile(folderName, fileName):
	fns = extract_java_functions(join(folderName, f"{fileName}.java"))
	functionsFile = ""
	notebookFile = nbf.v4.new_notebook()

	for fn in fns:
		firstStr = fn[1].find("\"\"\"") + 3
		lastStr = fn[1][firstStr:].find("\"\"\";") + firstStr
		code = fn[1][firstStr:lastStr]
		conv = dsl_to_python_from_original(code, fn[0])
		print(conv)
		functionsFile += f"def {conv[0]}():\n{conv[1]}\n    return {conv[2]}\n\n"
		inter = conv[1].replace("        ", "§INDENT§") + f"\n{conv[2]}"
		notebookFile["cells"] += [nbf.v4.new_markdown_cell(f"# {conv[0]}"),
				   nbf.v4.new_code_cell(inter.replace("    ", "").replace("§INDENT§", "    "))]


	with open(join("extracted", f"functions_{fileName}.py"), "w") as f:
		f.write(functionsFile)

	with open(join("extracted", f"notebook_{fileName}.ipynb"), "w") as f:
		nbf.write(notebookFile, f)



folderName = "../" #../arrays"

onlyfiles = [f for f in listdir(folderName) if isfile(join(folderName, f))]

for file in onlyfiles:
	print(file.replace(".java", ""))
	extractSingleFile(folderName, file.replace(".java", ""))


