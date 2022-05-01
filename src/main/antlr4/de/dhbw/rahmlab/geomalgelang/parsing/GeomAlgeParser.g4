parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

program
	: expr (EOF | NEWLINE)
	;

expr
	:	L_PARENTHESIS
		expr
		R_PARENTHESIS				#DummyLabel
	|	<assoc=right>
		left=expr
		op=	(MINUS_SIGN
			|SUPERSCRIPT_MINUS__SUPERSCRIPT_ONE
			|ASTERISK
			|SMALL_TILDE
			|DAGGER
			|SUPERSCRIPT_MINUS__ASTERISK
			|SUPERSCRIPT_TWO
			)						#UnaryOp
	|	left=expr
		op=	(SPACE
			|DOT_OPERATOR
			|LOGICAL_AND
			|UNION
			|INTERSECTION
			|R_FLOOR
			|L_FLOOR
			|LOGICAL_OR
			)
		right=expr					#BinaryOp
	|	left=expr
		op=SOLIDUS
		right=expr					#BinaryOp
	|	left=expr
		op=	(PLUS_SIGN
			|HYPHEN_MINUS
			)
		right=expr					#BinaryOp
	|	exprLiteral					#DummyLabel
	|	<assoc=right>
		expr SPACE+?				#DummyLabel
	|	<assoc=right>
		SPACE+?	expr				#DummyLabel
	;


exprLiteral
	:	value=	DECIMAL_LITERAL		#LiteralDecimal
	|	name=	IDENTIFIER			#VariableReference
	|	value=	(INFINITY
				|SMALL_EPSILON__SUBSCRIPT_ONE
				|SMALL_EPSILON__SUBSCRIPT_TWO
				|SMALL_EPSILON__SUBSCRIPT_THREE
				|SMALL_PI
				)					#LiteralCGA
	;

