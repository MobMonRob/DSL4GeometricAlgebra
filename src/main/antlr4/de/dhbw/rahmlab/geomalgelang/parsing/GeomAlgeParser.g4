parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

program
	:	expr (EOF | NEWLINE)
	;

///////////////////////////////////////////////////////////////////////////
// Expr
///////////////////////////////////////////////////////////////////////////

expr
	// Precedence max
	: parenExpr						#DummyLabel
	|	gradeExtractionExpr			#extractGrade
	|	literalExpr					#DummyLabel
	// Precedence 6
	|	<assoc=right>
		left=expr
		op=	(SUPERSCRIPT_MINUS__SUPERSCRIPT_ONE
			|ASTERISK
			|SMALL_TILDE
			|DAGGER
			|SUPERSCRIPT_MINUS__ASTERISK
			|SUPERSCRIPT_TWO
			|CIRCUMFLEX_ACCENT
			)						#UnaryOpR
	// Precedence 5
	|	op=	HYPHEN_MINUS
		right=expr					#UnaryOpL
	// Precedence 4
	|	left=expr
		op=	SPACE
		right=expr					#BinaryOp
	// Precedence 3
	|	left=expr
		op=	(DOT_OPERATOR
			|LOGICAL_AND
			|INTERSECTION
			|UNION
			|R_FLOOR
			|L_FLOOR
			|LOGICAL_OR
			)
		right=expr					#BinaryOp
	// Precedence 2
	|	left=expr
		op=SOLIDUS
		right=expr					#BinaryOp
	// Precedence 1
	|	left=expr
		op=	(PLUS_SIGN
			|HYPHEN_MINUS
			)
		right=expr					#BinaryOp
	// Precedence min
	|	<assoc=right>
		expr SPACE+?				#DummyLabel
	|	<assoc=right>
		SPACE+?	expr				#DummyLabel
	;

///////////////////////////////////////////////////////////////////////////
// Sub Expr
///////////////////////////////////////////////////////////////////////////

literalExpr
	:	value=	(SMALL_EPSILON__SUBSCRIPT_ZERO
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
				)					#LiteralCGA
	|	value=	DECIMAL_LITERAL		#LiteralDecimal
	|	name=	IDENTIFIER			#VariableReference
	;

parenExpr
	:	L_PARENTHESIS
		expr
		R_PARENTHESIS
	;

gradeExtractionExpr
	:	LESS_THAN_SIGN
		inner=expr
		GREATER_THAN_SIGN
		grade=	(SUBSCRIPT_ZERO
				|SUBSCRIPT_ONE
				|SUBSCRIPT_TWO
				|SUBSCRIPT_THREE
				|SUBSCRIPT_FOUR
				|SUBSCRIPT_FIVE
				)
	;

