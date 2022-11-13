parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

program
	:	SPACE*
		expr
		SPACE*
		(EOF | NEWLINE)
	;

///////////////////////////////////////////////////////////////////////////
// Expr
///////////////////////////////////////////////////////////////////////////

expr
	:	unOpExpr
	|	binOpExpr
	|	nonOuterRecursiveExpr
	;

///////////////////////////////////////////////////////////////////////////
// UnOp | singleSideRecursive
///////////////////////////////////////////////////////////////////////////

unOpExpr
	:	unOpLExpr
	|	unOpRExpr
	;

// rightSideRecursive (sic)
unOpLExpr
	:	<assoc=right>
		op=	HYPHEN_MINUS
		(nonOuterRecursiveExpr | unOpExpr)
		#UnaryOpL
	;

// leftSideRecursive (sic)
unOpRExpr
	:	nonOuterRecursiveExpr
		unOpRSymbolExpr
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
		#UnaryOpR
	;

///////////////////////////////////////////////////////////////////////////
// BinOp | doubleSideRecursive
///////////////////////////////////////////////////////////////////////////

binOpExpr
	:	binOpSpacedExpr
	;

// binOpExpr
binOpSpacedExpr
	:	binOpEndExpr		#Dummy
	|	binOpSpacedExpr
		SPACE*
		op=	(DOT_OPERATOR
			|LOGICAL_AND
			|INTERSECTION
			|UNION
			|R_FLOOR
			|L_FLOOR
			|LOGICAL_OR
			)
		SPACE*
		binOpSpacedExpr		#BinaryOp //Precedence 3
	|	binOpSpacedExpr
		SPACE*
		op=	SOLIDUS
		SPACE*
		binOpSpacedExpr		#BinaryOp //Precedence 2
	|	binOpSpacedExpr
		SPACE*
		op=	(PLUS_SIGN
			|HYPHEN_MINUS
			)
		SPACE*
		binOpSpacedExpr		#BinaryOp //Precedence 1
	;

binOpEndExpr
	:	nonOuterRecursiveExpr
	|	unOpExpr
	;

///////////////////////////////////////////////////////////////////////////
// composite / abstract / higherOrder / meta Expr
///////////////////////////////////////////////////////////////////////////

// literalExpr are the only nonRecursive Expr
nonOuterRecursiveExpr
	:	innerRecursiveExpr
	|	literalExpr
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
				|SMALL_PI
				|INFINITY
				|SMALL_O
				|SMALL_N
				|SMALL_N_TILDE
				|CAPITAL_E__SUBSCRIPT_ZERO
				)					#LiteralConstant
	|	value=	DECIMAL_LITERAL		#LiteralDecimal
	|	name=	IDENTIFIER			#VariableReference
	;

parenExpr
	:	L_PARENTHESIS
		SPACE*
		expr
		SPACE*
		R_PARENTHESIS
	;

gradeExtractionExpr
	:	LESS_THAN_SIGN
		SPACE*
		expr
		SPACE*
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
