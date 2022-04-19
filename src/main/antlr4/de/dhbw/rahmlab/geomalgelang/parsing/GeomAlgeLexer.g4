lexer grammar GeomAlgeLexer;

fragment LETTER
	: [a-zA-Z]
	;

fragment DIGIT
	: [0-9]
	;

L_PAREN
	: '('
	;

R_PAREN
	: ')'
	;

DECIMAL_LITERAL
	: DIGIT+ (',' DIGIT+)?
	;

IDENTIFIER
	: LETTER+
	;

INFINITY
	: '∞'
	;

EPSILON_ONE
	: 'ε₁'
	;

EPSILON_TWO
	: 'ε₂'
	;

EPSILON_THREE
	: 'ε₃'
	;

DAGGER
	: '†'
	;

SUPERSCRIPT_TILDE
	: '˜'
	;

SUPERSCRIPT_MINUS_STAR
	: '⁻*'
	;

STAR
	: '*'
	;

SUPERSCRIPT_MINUS_ONE
	: '⁻¹'
	;

CUP
	: '∪'
	;

CAP
	: '∩'
	;

L_CORNER
	: '⌊'
	;

R_CORNER
	: '⌋'
	;

VEE
	: '∨'
	;

SLASH
	: '/'
	;

SPACE
	: ' '
	;

DOT
	: '⋅'
	;

WEDGE
	: '∧'
	;

PLUS
	: '+'
	;

MINUS
	: '-'
	;

NEWLINE: '\r'? '\n';

WHITESPACE_OTHER
	: [\t\u000C]+ -> skip
	;

// Catchall Rule https://tomassetti.me/antlr-mega-tutorial/#chapter56
ANY
	: .
	;
