parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

program
	: expr (EOF | NEWLINE)
	;

// Precedence Higher <=> Earlier
// Precedencce Higher <=> Higher in parse tree

expr
	: L_PAREN expr R_PAREN			#Unused
	|	<assoc=right>
		left=expr
		op=	(SUPERSCRIPT_MINUS_ONE
			|SUPERSCRIPT_MINUS_STAR
			|SUPERSCRIPT_TILDE
			|DAGGER
			)						#UnaryOp
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
		(SPACE op=STAR
		|(~SPACE+)? op=STAR (~SPACE+)?
		|op=SLASH
		)
		right=expr					#BinaryOp
	|	left=expr
		op=	(PLUS
			|MINUS
			)
		right=expr					#BinaryOp
	|	<assoc=right>
		left=expr
		op=STAR						#UnaryOp
	|	value=	DECIMAL_LITERAL		#LiteralDecimal
	|	varName=IDENTIFIER			#VariableReference
	|	value=	(INFINITY
				|EPSILON_ONE
				|EPSILON_TWO
				|EPSILON_THREE
				)					#LiteralCGA
	|	<assoc=right>
		expr SPACE+?				#Unused
	|	<assoc=right>
		SPACE+?	expr				#Unused
	;

