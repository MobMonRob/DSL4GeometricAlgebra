lexer grammar GeomAlgeLexer;

///////////////////////////////////////////////////////////////////////////
// Policy
///////////////////////////////////////////////////////////////////////////
/*

- The name of the corresponding function above the symbol name is the same as in ICGAMultivector_Processor.
- The name of a symbol is essentially the same as in unicode.
- The sequence of symbol definitions is the same as in the README.md.

*/
///////////////////////////////////////////////////////////////////////////
// Symbols for binaryOp's
///////////////////////////////////////////////////////////////////////////

// Precedence 3
// geometric_product
// \u0020
SPACE
	: ' '
	;

// Precedence 3
// inner_product
// \u22C5
DOT_OPERATOR
	: '⋅'
	;

// Precedence 3
// outer_product
// \u2227
LOGICAL_AND
	: '∧'
	;

// Precedence 1
// addition
// \u002B
PLUS_SIGN
	: '+'
	;

// Precedence 1
// subtraction
// \u002D
HYPHEN_MINUS
	: '-'
	;

// Precedence 3
// meet (intersection)
// \u2229
INTERSECTION
	: '∩'
	;

// Precedence 3
// join (union)
// \u222A
UNION
	: '∪'
	;

// Precedence 3
// right_contraction
// \u230B
R_FLOOR
	: '⌋'
	;

// Precedence 3
// left_contraction
// \u230A
L_FLOOR
	: '⌊'
	;

// Precedence 3
// regressive_product
// \u2228
LOGICAL_OR
	: '∨'
	;

// Precedence 2
// division (inverse geometric product)
// \u002F
SOLIDUS
	: '/'
	;

///////////////////////////////////////////////////////////////////////////
// Symbols for unaryOp's left sided
///////////////////////////////////////////////////////////////////////////

// Precedence 4
// negate
// \u2212
MINUS_SIGN
	: '−'
	;

///////////////////////////////////////////////////////////////////////////
// Symbols for unaryOp's right sided
///////////////////////////////////////////////////////////////////////////

// Precedence 4
// general_inverse
// \u207B\u00B9
SUPERSCRIPT_MINUS__SUPERSCRIPT_ONE
	: '⁻¹'
	;

// Precedence 4
// dual
// \u002A
ASTERISK
	: '*'
	;

// Precedence 4
// reverse
// \u02DC
SMALL_TILDE
	: '˜'
	;

// Precedence 4
// clifford conjugate
// \u2020
DAGGER
	: '†'
	;

// Precedence 4
// undual
// \u207B\u002A
SUPERSCRIPT_MINUS__ASTERISK
	: '⁻*'
	;

// Precedence 4
// square
// \u00B2
SUPERSCRIPT_TWO
	: '²'
	;

// Precedence 4
// involute
// \u005E
CIRCUMFLEX_ACCENT
	: '^'
	;

///////////////////////////////////////////////////////////////////////////
// Symbols for composite operators
///////////////////////////////////////////////////////////////////////////

// Precedence 4

// \u003C
LESS_THAN_SIGN
	: '<'
	;

// \u003E
GREATER_THAN_SIGN
	: '>'
	;

// \u2080
SUBSCRIPT_ZERO
	: '₀'
	;

// \u2081
SUBSCRIPT_ONE
	: '₁'
	;

// \u2082
SUBSCRIPT_TWO
	: '₂'
	;

// \u2083
SUBSCRIPT_THREE
	: '₃'
	;

// \u2084
SUBSCRIPT_FOUR
	: '₄'
	;

// \u2085
SUBSCRIPT_FIFE
	: '₅'
	;

///////////////////////////////////////////////////////////////////////////
// Symbols for constants
///////////////////////////////////////////////////////////////////////////

// base_vector_origin
// \u03B5\u2080
SMALL_EPSILON__SUBSCRIPT_ZERO
	: 'ε₀'
	;

// base_vector_infinity
// \u03B5\u1D62
SMALL_EPSILON__SUBSCRIPT_SMALL_I
	: 'εᵢ'
	;

// base_vector_x
// \u03B5\u2081
SMALL_EPSILON__SUBSCRIPT_ONE
	: 'ε₁'
	;

// base_vector_y
// \u03B5\u2082
SMALL_EPSILON__SUBSCRIPT_TWO
	: 'ε₂'
	;

// base_vector_z
// \u03B5\u2083
SMALL_EPSILON__SUBSCRIPT_THREE
	: 'ε₃'
	;

///////////////////////////////////////////////////////////////////////////
// Symbols for constants (non-trivially constructed)
///////////////////////////////////////////////////////////////////////////

// pi
// \u03C0
SMALL_PI
	: 'π'
	;

// base_vector_infinity_dorst
// \u221E
INFINITY
	: '∞'
	;

// base_vector_origin_dorst
// \u006F
SMALL_O
	: 'o'
	;

// base_vector_infinity_doran
// \u006E
SMALL_N
	: 'n'
	;

// base_vector_origin_doran
// \u00F1
SMALL_N_TILDE
	: 'ñ'
	;

// minkovsky_bi_vector
// \u0045\u8320
CAPITAL_E__SUBSCRIPT_ZERO
	: 'E₀'
	;

///////////////////////////////////////////////////////////////////////////
// Various
// Identifier Symbol needs to be defined after o (origin vector) and n (infinity vector) due to lexer precedence rules!
///////////////////////////////////////////////////////////////////////////

fragment LETTER
	: [a-zA-Z]
	;

fragment DIGIT
	: [0-9]
	;

DECIMAL_LITERAL
	: DIGIT+ (',' DIGIT+)?
	;

IDENTIFIER
	: LETTER+
	;

L_PARENTHESIS
	: '('
	;

R_PARENTHESIS
	: ')'
	;

///////////////////////////////////////////////////////////////////////////
// Technical
///////////////////////////////////////////////////////////////////////////

NEWLINE: '\r'? '\n';

WHITESPACE_OTHER
	: [\t\u000C]+ -> skip
	;

// Catchall Rule https://tomassetti.me/antlr-mega-tutorial/#chapter56
ANY
	: .
	;
