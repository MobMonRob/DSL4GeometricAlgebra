lexer grammar GeomAlgeLexer;

fragment LETTER
    : [a-zA-Z]
    ;

fragment DIGIT
    : [0-9]
    ;

DECIMAL_LITERAL
    : DIGIT+ (',' DIGIT+)?
    ;

ADD
    : '+'
    ;

SUB
    : '-'
    ;

MUL
    : '*'
    ;

DIV
    : '/'
    ;

L_PAREN
    : '('
    ;

R_PAREN
    : ')'
    ;

WHITESPACE
    :  [ \t\r\n\u000C]+ -> skip
    ;

NEWLINE: '\r'? '\n';

// Catchall Rule https://tomassetti.me/antlr-mega-tutorial/#chapter56
ANY
    : .
    ;
