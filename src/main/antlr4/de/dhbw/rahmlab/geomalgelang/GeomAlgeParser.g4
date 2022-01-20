parser grammar GeomAlgeParser;

options { tokenVocab=GeomAlgeLexer; }

file        : element* ;

attribute   : ID '=' STRING ; 

content     : TEXT ;

element     : (content | tag) ;

tag         : '[' ID attribute? ']' element* '[' '/' ID ']' ;
