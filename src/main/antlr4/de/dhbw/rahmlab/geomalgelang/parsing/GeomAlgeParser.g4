parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

program
	: expr (EOF | NEWLINE)
	;

//Precedence Higher=EARLIER

// SPACE | DOT

expr
	: L_PAREN expr R_PAREN	#Unused
	|	<assoc=right>
		left=expr
		op=	(SUPERSCRIPT_MINUS_ONE
			|STAR
			|SUPERSCRIPT_MINUS_STAR
			|SUPERSCRIPT_TILDE
			|DAGGER
			)				#UnaryOp
	|	left=expr
		op=	(SPACE
			|DOT
			|WEDGE
			|CUP
			|CAP
			|L_CORNER
			|R_CORNER
			|VEE
			)
		right=expr					#BinaryOp
	|	left=expr
		op=	(STAR
			|SLASH
			)
		right=expr					#BinaryOp
	|	left=expr
		op=	(PLUS
			|MINUS
			)
		right=expr					#BinaryOp
	|	value=	DECIMAL_LITERAL		#LiteralDecimal
	|	varName=IDENTIFIER			#VariableReference
	|	value=	(INFINITY
				|EPSILON_ONE
				|EPSILON_TWO
				|EPSILON_THREE
				)					#LiteralCGA
	;
