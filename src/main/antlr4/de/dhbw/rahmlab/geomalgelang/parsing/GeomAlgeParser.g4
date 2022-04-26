parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

program
	: expr (EOF | NEWLINE)
	;

expr
	: L_PAREN expr R_PAREN			#DummyLabel
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
				|EPSILON_ONE
				|EPSILON_TWO
				|EPSILON_THREE
				)					#LiteralCGA
	;

