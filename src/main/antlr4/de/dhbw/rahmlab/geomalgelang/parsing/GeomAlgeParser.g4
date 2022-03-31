parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

program
    : expr (EOF | NEWLINE)					#TopExpr
    ;

expr
    : L_PAREN expr R_PAREN					#Unused
    | left=expr op=(MUL|DIV) right=expr		#BinaryOp
    | left=expr op=(ADD|SUB) right=expr		#BinaryOp
    | value=DECIMAL_LITERAL					#LiteralDecimal
	| varName=IDENTIFIER					#VariableReference
    ;
