//header
grammar toya;

// --------- RULES ---------
compilation: (function|variableDeclaration)* EOF;
function: functionSignature '{' statement* '}';
functionSignature: 'function' name '('functionArgument* (','functionArgument)*')' ('->' type)?;
functionArgument: name ':' type (ASSERT expression)?;

expression : '(' expression ')' #ParenthesisExpression
           | '('expression MUL expression')' #Multiply
           | expression MUL expression  #Multiply
           | '(' expression DIV expression ')' #Divide
           | expression DIV expression #Divide
           | '(' expression PLUS expression ')' #Add
           | expression PLUS expression #Add
           | '(' expression MINUS expression ')' #Subtract
           | expression MINUS expression #Subtract
           | NOT expression #NotExpression
           | '(' expression (comparator|AND|OR) expression ')' #BooleanExpression
           | expression (comparator|AND|OR) expression #BoolExpression
           | value #Val
           | reference #VarReference
           | ifExpression #If
           | matchExpression #Match
           | arrayDeclaration #ArrDeclaration
           | arrayAccess #ArrAccess
           | functionCall #FunCall;

comparator: GT | GE | EQ | LE | LT | NE;
boolean: TRUE | FALSE;

type: 'int'ARRAY*
    | 'short'ARRAY*
    | 'long'ARRAY*
    | 'float'ARRAY*
    | 'double'ARRAY*
    | 'char'ARRAY*
    | 'byte'ARRAY*
    | 'boolean' ARRAY*
    | 'string'ARRAY*
    | 'void'ARRAY*;

arrayType: 'int'
            | 'short'
            | 'long'
            | 'float'
            | 'double'
            | 'char'
            | 'byte'
            | 'boolean'
            | 'string';

reference: ID;
name: ID;
value: INT
     | FLOAT
     | STRING
     | boolean;

statement: variableDeclaration
         | variableAssertion
         | returnStatement
         | forStatement
         | expression;

arrayDeclaration: 'new' arrayType arrayDimension+; // new int[8+1]
arrayAccess: name arrayDimension+;
arrayDimension: '[' expression ']';

variableDeclaration: VARIABLE name ASSERT expression; // var c = a+b+3
variableAssertion: name ('['expression ']')? ASSERT expression;
functionCall: name'('expression? (',' expression)*')';
returnStatement: 'return' #ReturnVoid
               | ('return')? expression #ReturnValue;

// if
ifExpression: ifCondition ifBranch /*(ELSE elseIfCondition elseIfBranch)**/ (ELSE elseBranch)?;
ifCondition: IF'(' expression ')';
ifBranch: branch;
elseBranch: branch;
//elseIfCondition: ifCondition;
//elseIfBranch: ifBranch;

// for
forStatement: forHead '{' statement* '}';
forHead: FOR'(' variableDeclaration? ';' forCondition? ';' incrementExpression? ')';
forCondition: expression;
incrementExpression: expression;

// switch
matchExpression: matchHead '{' matchBranch* matchDefault '}';
matchHead: MATCH'('expression')';
matchBranch: value '->' branch;
matchDefault: 'default' '->' branch;

branch: '{'statement*'}' #Block
      | expression #Exp;

// --------- TOKENS ---------
fragment LOWERCASE : [a-z];
fragment UPPERCASE : [A-Z];
fragment DIGIT : [0-9];
fragment EQUALS: '=';

ARRAY : '[]';
COMMENT : '//' .*? '\n' -> skip;

// arithmetic operators
MUL : '*';
DIV : '/';
PLUS: '+';
MINUS: '-';
MOD: '%';

// boolean operators
// -- infix
GT: '>';
GE: '>=';
LT: '<';
LE: '<=';
EQ: '==';
NE: '!=';
AND: '&&';
OR: '||';
// -- prefix
NOT: '!';

//keywords
MATCH: 'match';
FOR: 'for';
IF: 'if';
ELSE: 'else';
VARIABLE: 'var';
TRUE: 'true';
FALSE: 'false';

ASSERT: EQUALS;
STRING: '"'.*?'"';

INT: '-'? DIGIT+;
FLOAT: INT'.'DIGIT+;
ID: (LOWERCASE|UPPERCASE|DIGIT|'_')+;
WS: [ \t\n\r]+ -> skip;
