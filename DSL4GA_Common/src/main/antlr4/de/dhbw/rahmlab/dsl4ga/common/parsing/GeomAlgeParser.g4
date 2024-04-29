parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

///////////////////////////////////////////////////////////////////////////
// Policy
///////////////////////////////////////////////////////////////////////////
/*

- If there is a choice of a sequence of subexpressions "(a|b) vs (b|a)"", always the sequence is chosen such that the precedence will be preseved. This prevents inconsistent precedences throughout the parser.

*/
///////////////////////////////////////////////////////////////////////////
// sourceUnit
///////////////////////////////////////////////////////////////////////////

sourceUnit
	:	(WHITE_LINE* functions+=function WHITE_LINE*)+
		EOF // https://stackoverflow.com/a/61402548
	;

///////////////////////////////////////////////////////////////////////////
// Function
///////////////////////////////////////////////////////////////////////////

function
	:	functionHead
		SPACE* L_CURLY_BRACKET functionBody R_CURLY_BRACKET
		#Function_
	;

functionHead
	:	SPACE* FUNCTION_INDICATOR
		SPACE+ name=IDENTIFIER
		SPACE* L_PARENTHESIS formalParameterList R_PARENTHESIS
		#FunctionHead_
	;

formalParameterList
	:	SPACE* (params+=formalParameter (SPACE* COMMA SPACE* params+=formalParameter SPACE*)*)? SPACE*
	;

formalParameter
	:	name=IDENTIFIER	#FormalParameter_
	;

functionBody
	:	WHITE_LINE*
		(stmt WHITE_LINE+)*
		retExpr
		WHITE_LINE*
	;

///////////////////////////////////////////////////////////////////////////
// Stmt
///////////////////////////////////////////////////////////////////////////

stmt
 	:	SPACE* exprContext=expr SPACE*	#ExprStmt // Needed currently only for LastListReturn
	|	SPACE* viz=COLON? assigned=IDENTIFIER SPACE* ASSIGNMENT SPACE* exprCtx=expr SPACE*		#AssgnStmt
	|	SPACE* assigned+=(IDENTIFIER|LOW_LINE) SPACE* (COMMA SPACE* assigned+=(IDENTIFIER|LOW_LINE) SPACE*)* ASSIGNMENT SPACE* callCtx=callExpr SPACE*		#TupleAssgnStmt
	;

// The list-form (1) needs iteration in the transformer while the tree-form (2) don't.
// enterRetExprStmt inverses the order if retExpr is left-recursive.
retExpr
	//:	exprContext+=expr (COMMA exprContext+=expr)*	#RetExprStmt
	:	exprContext=expr				#RetExprStmt
	|	exprContext=expr COMMA retExpr	#RetExprStmt
	;

///////////////////////////////////////////////////////////////////////////
// Expr
///////////////////////////////////////////////////////////////////////////

expr
	:	SPACE+ expr
	|	expr SPACE+
	|	nonOuterRecursiveExpr
	|	unOpExpr
	|	binOpExpr
	;

///////////////////////////////////////////////////////////////////////////
// UnOp | singleSideRecursive
///////////////////////////////////////////////////////////////////////////

unOpExpr
	:	unOpRExpr //Precedence 6
	|	unOpLExpr //Precedence 5
	;

// rightSideRecursive (sic)
unOpLExpr
	:	<assoc=right>
		op=	HYPHEN_MINUS
		SPACE*
		(
			nonOuterRecursiveExpr
			|unOpExpr
		)
		#UnOpL
	;

// leftSideRecursive (sic)
unOpRExpr
	:	nonOuterRecursiveExpr
		unOpRSymbolExpr //Without this seemingly redundant line occurs ambiguity
	|	unOpRExpr
		unOpRSymbolExpr
	;

unOpRSymbolExpr
	:	op=	(SUPERSCRIPT_MINUS__SUPERSCRIPT_ONE
			|ASTERISK
			|SMALL_TILDE
			|DAGGER
			|SUPERSCRIPT_MINUS__ASTERISK
			|SUPERSCRIPT_TWO
			|CIRCUMFLEX_ACCENT
			)
		#UnOpR
	;

///////////////////////////////////////////////////////////////////////////
// BinOp | doubleSideRecursive
///////////////////////////////////////////////////////////////////////////

binOpExpr
	:	nonOuterRecursiveExpr	#BinOpExprDummy //Closure
	|	unOpExpr				#BinOpExprDummy //Closure
	|	lhs=binOpExpr
		spaces+=SPACE*
		(
			nonOuterRecursiveExpr
			|unOpRExpr	// "a-b" evaluates to subtraction(a, b) instead of gp(a, negate(b))
		)					#GP		//Precedence 4
	|	binOpExpr
		SPACE*
		op=	(DOT_OPERATOR
			|LOGICAL_AND
			|INTERSECTION
			|UNION
			|R_CONTRACTION
			|L_CONTRACTION
			|LOGICAL_OR
			)
		SPACE*
		binOpExpr			#BinOp	//Precedence 3
	|	binOpExpr
		SPACE*
		op=	SOLIDUS
		SPACE*
		binOpExpr			#BinOp	//Precedence 2
	|	binOpExpr
		SPACE*
		op=	(PLUS_SIGN
			|HYPHEN_MINUS
			)
		SPACE*
		binOpExpr			#BinOp	//Precedence 1
	;


///////////////////////////////////////////////////////////////////////////
// Various
///////////////////////////////////////////////////////////////////////////

callExpr
	:	name=IDENTIFIER L_PARENTHESIS (expr (COMMA expr)*)? R_PARENTHESIS	#Call
	;

///////////////////////////////////////////////////////////////////////////
// composite / abstract / higherOrder / meta Expr
///////////////////////////////////////////////////////////////////////////

// literalExpr are the only nonRecursive Expr
nonOuterRecursiveExpr
	:	callExpr
	|	literalExpr
	|	innerRecursiveExpr
	;

innerRecursiveExpr
	:	parenExpr
	|	gradeExtractionExpr
	;

///////////////////////////////////////////////////////////////////////////
// atomic / terminal / firstOrder Expr
///////////////////////////////////////////////////////////////////////////

literalExpr
	:	type=	(SMALL_EPSILON__SUBSCRIPT_ZERO
				|SMALL_EPSILON__SUBSCRIPT_SMALL_I
				|SMALL_EPSILON__SUBSCRIPT_ONE
				|SMALL_EPSILON__SUBSCRIPT_TWO
				|SMALL_EPSILON__SUBSCRIPT_THREE
				|SMALL_EPSILON__SUBSCRIPT_PLUS
				|SMALL_EPSILON__SUBSCRIPT_MINUS
				|SMALL_PI
				|INFINITY
				|SMALL_O
				|SMALL_N
				|SMALL_N_TILDE
				|CAPITAL_E__SUBSCRIPT_ZERO
				|CAPITAL_E__SUBSCRIPT_THREE
				|CAPITAL_E
				)					#LiteralConstant
	|	value=	DECIMAL_LITERAL		#LiteralDecimal
	|	name=	IDENTIFIER			#VariableReference
	;

parenExpr
	:	L_PARENTHESIS expr R_PARENTHESIS
	;

gradeExtractionExpr
	:	LESS_THAN_SIGN
		expr
		GREATER_THAN_SIGN
		grade=	(SUBSCRIPT_ZERO
				|SUBSCRIPT_ONE
				|SUBSCRIPT_TWO
				|SUBSCRIPT_THREE
				|SUBSCRIPT_FOUR
				|SUBSCRIPT_FIVE
				)
		#gradeExtraction
	;
