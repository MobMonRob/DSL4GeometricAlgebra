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
// Symbols for multiple Op's
///////////////////////////////////////////////////////////////////////////

// binOp subtraction, unOpL negate
// \u002D
HYPHEN_MINUS
	: '-'
	;

///////////////////////////////////////////////////////////////////////////
// Symbols for binaryOp's
///////////////////////////////////////////////////////////////////////////

// geometric_product
// \u0020
SPACE
	: ' '
	;

// inner_product
// \u22C5
DOT_OPERATOR
	: '⋅'
	;

// outer_product
// \u2227
LOGICAL_AND
	: '∧'
	;

// addition
// \u002B
PLUS_SIGN
	: '+'
	;

// meet (intersection)
// \u2229
INTERSECTION
	: '∩'
	;

// join (union)
// \u222A
UNION
	: '∪'
	;

// right_contraction
// \u230B
R_FLOOR
	: '⌋'
	;

// left_contraction
// \u230A
L_FLOOR
	: '⌊'
	;

// regressive_product
// \u2228
LOGICAL_OR
	: '∨'
	;

// division (inverse geometric product)
// \u002F
SOLIDUS
	: '/'
	;

///////////////////////////////////////////////////////////////////////////
// Symbols for unaryOp's right sided
///////////////////////////////////////////////////////////////////////////

// general_inverse
// \u207B\u00B9
SUPERSCRIPT_MINUS__SUPERSCRIPT_ONE
	: '⁻¹'
	;

// dual
// \u002A
ASTERISK
	: '*'
	;

// reverse
// \u02DC
SMALL_TILDE
	: '˜'
	;

// clifford conjugate
// \u2020
DAGGER
	: '†'
	;

// undual
// \u207B\u002A
SUPERSCRIPT_MINUS__ASTERISK
	: '⁻*'
	;

// square
// \u00B2
SUPERSCRIPT_TWO
	: '²'
	;

// involute
// \u005E
CIRCUMFLEX_ACCENT
	: '^'
	;

///////////////////////////////////////////////////////////////////////////
// Symbols for composite operators
///////////////////////////////////////////////////////////////////////////

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
SUBSCRIPT_FIVE
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

SMALL_EPSILON__SUBSCRIPT_PLUS
	: 'ε₊'
	;

SMALL_EPSILON__SUBSCRIPT_MINUS
	: 'ε₋'
	;

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
// \u0045\u2080
CAPITAL_E__SUBSCRIPT_ZERO
	: 'E₀'
	;

CAPITAL_E__SUBSCRIPT_THREE
	: 'E₃'
	;

CAPITAL_E
	: 'E'
	;

///////////////////////////////////////////////////////////////////////////
// Various
// Symbol 'IDENTIFIER' needs to be defined after o (origin vector) and n (infinity vector) due to lexer precedence rules!
///////////////////////////////////////////////////////////////////////////

fragment LETTER
    : [a-zA-Z_]
    ;

fragment DIGIT
	: [0-9]
	;

fragment LETTER_OR_DIGIT
    : LETTER
    | DIGIT
    ;

DECIMAL_LITERAL
	: DIGIT+ ('.' DIGIT+)?
	;

IDENTIFIER
	: LETTER LETTER_OR_DIGIT*
	;

L_PARENTHESIS
	: '('
	;

R_PARENTHESIS
	: ')'
	;

COMMA
	: ','
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
