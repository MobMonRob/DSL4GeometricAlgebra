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
	:	SPACE* vizAssigned=vizAssignedR SPACE* ASSIGNMENT SPACE* exprCtx=expr SPACE*		#AssgnStmt
	|	SPACE* vizAssigned+=vizAssignedR SPACE* (COMMA SPACE* vizAssigned+=vizAssignedR SPACE*)* ASSIGNMENT SPACE* callCtx=callExpr SPACE*		#TupleAssgnStmt
	|	SPACE* assigned=IDENTIFIER SPACE* L_EDGE_BRACKET R_EDGE_BRACKET SPACE* ASSIGNMENT SPACE* L_CURLY_BRACKET SPACE* arrayCtx=arrayExpr? SPACE* R_CURLY_BRACKET SPACE* # ArrayInitStmt
	|	SPACE* FOR_INDICATOR SPACE* L_PARENTHESIS SPACE* loopVar=IDENTIFIER SPACE* SEMICOLON SPACE* beginning=indexCalc SPACE* SEMICOLON SPACE* ending=indexCalc SPACE* SEMICOLON SPACE* step=indexCalc SPACE* R_PARENTHESIS (SPACE | WHITE_LINE)* L_CURLY_BRACKET (SPACE | WHITE_LINE)* loopBody R_CURLY_BRACKET SPACE* #LoopStmt
	;

vizAssignedR
	: viz=COLON? assigned=(IDENTIFIER|LOW_LINE)
	| viz=COLON? assigned=IDENTIFIER SPACE* L_EDGE_BRACKET SPACE* index=indexCalc SPACE* R_EDGE_BRACKET
	;

// The list-form (1) needs iteration in the transformer while the tree-form (2) don't.
// enterRetExprStmt inverses the order if retExpr is left-recursive.
retExpr
	//:	exprContext+=expr (COMMA exprContext+=expr)*	#RetExprStmt
	:	exprContext=expr				#RetExprStmtExpr
	|	exprContext=expr COMMA retExpr	#RetExprStmtExpr
	;


///////////////////////////////////////////////////////////////////////////
// ArrayExpr
///////////////////////////////////////////////////////////////////////////

arrayExpr
	:   nonEmptyArrayExpr
//	| 
	;

nonEmptyArrayExpr
    : expr								#ArrayExprStmtExpr
    | expr COMMA nonEmptyArrayExpr		#ArrayExprStmtExpr
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
// IndexCalculation
///////////////////////////////////////////////////////////////////////////

indexCalc
	: integer=INTEGER_LITERAL	
	| id=IDENTIFIER			
    | id=IDENTIFIER (op=PLUS_SIGN|op=HYPHEN_MINUS) integer=INTEGER_LITERAL 
	| len=LENGTH_INDICATOR SPACE* L_PARENTHESIS SPACE* id=IDENTIFIER SPACE* R_PARENTHESIS SPACE* ((op=PLUS_SIGN|op=HYPHEN_MINUS) SPACE* integer=INTEGER_LITERAL)? 
    ;

///////////////////////////////////////////////////////////////////////////
// Loop
///////////////////////////////////////////////////////////////////////////

loopBody : (insideLoopStmt WHITE_LINE+)+;

insideLoopStmt
	: SPACE* assigned=IDENTIFIER SPACE* L_EDGE_BRACKET SPACE* index=indexCalc SPACE* R_EDGE_BRACKET SPACE* ASSIGNMENT SPACE* assignments=loopAssignment SPACE*
	;

loopAssignment
	: arrayExprCtx=arrayAccessExpr 
	| literalExprCtx=loopLiteralExpr
	| (arrayExprCtx=arrayAccessExpr | literalExprCtx=loopLiteralExpr ) SPACE * (plusOp=PLUS_SIGN | minusOp=HYPHEN_MINUS) SPACE* loopAssignment
	;

loopLiteralExpr
	: int=INTEGER_LITERAL	
    | dec=DECIMAL_LITERAL	
    | id=IDENTIFIER			
    ;

///////////////////////////////////////////////////////////////////////////
// UnOp | singleSideRecursive
///////////////////////////////////////////////////////////////////////////

unOpExpr
	:	unOpRExpr //Precedence 6
	|	unOpLExpr //Precedence 5System.out.println(ctx.ops);
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
	|	arrayAccessExpr
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
	|	value=	INTEGER_LITERAL		#LiteralInteger
	;

parenExpr
	:	L_PARENTHESIS expr R_PARENTHESIS
	;

arrayAccessExpr
	: array=IDENTIFIER SPACE* L_EDGE_BRACKET SPACE* index=indexCalc SPACE* R_EDGE_BRACKET
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
