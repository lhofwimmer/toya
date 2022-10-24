//header
grammar toya;

// --------- RULES ---------
compilation: (function|variableDeclaration)* EOF;
function: functionSignature '{' statement* '}';
functionSignature: 'function' name '('functionArgument* (','functionArgument)*')' (ARROW type)?;
functionArgument: name ':' type (ASSERT expression)?;

expression : functionCall #FunCall
           | null #NullExpression
           |  '(' expression ')' #ParenthesisExpression
           | '('expression MUL expression')' #Multiply
           | expression MUL expression  #Multiply
           | '(' expression DIV expression ')' #Divide
           | expression DIV expression #Divide
           | '(' expression PLUS expression ')' #Add
           | expression PLUS expression #Add
           | '(' expression MINUS expression ')' #Subtract
           | expression MINUS expression #Subtract
           | NOT expression #NotExpression
           | '(' expression AND expression ')' #AndExpression
           | expression AND expression #AndExpression
           | '(' expression OR expression ')' #OrExpression
           | expression OR expression #OrExpression
           | '(' expression comparator expression ')' #BooleanExpression
           | expression comparator expression #BooleanExpression
           | value #Val
           | reference #VarReference
           | ifExpression #If
           | matchExpression #Match
           | arrayAccess #ArrAccess;

comparator: GT | GE | EQ | LE | LT | NE;

null: NULL;

boolean: TRUE | FALSE;

type: 'int'ARRAY?
    | 'double'ARRAY?
    | 'char'ARRAY?
    | 'boolean' ARRAY?
    | 'string'ARRAY?
    | 'void'ARRAY?;

arrayType: 'int'
         | 'double'
         | 'char'
         | 'boolean'
         | 'string';

reference: ID;
name: ID;
value: INT
     | FLOAT
     | STRING
     | boolean;

statement: expression
         | arrayDeclaration
         | variableDeclaration
         | variableAssertion
         | returnStatement
         | forStatement;

arrayDeclaration: VARIABLE name ASSERT 'new' arrayType arrayDimension; // new int[8+1]
arrayAccess: name arrayDimension;
arrayDimension: '[' expression ']';

variableDeclaration: VARIABLE name ASSERT expression; // var c = a+b+3
variableAssertion: name ('['arrayExpression ']')? ASSERT expression;
arrayExpression: expression;
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
matchBranch: value ARROW branch;
matchDefault: 'default' ARROW branch;

branch: '{'statement*'}' #Block
      | expression #Exp;

// --------- TOKENS ---------
fragment LOWERCASE : [a-z];
fragment UPPERCASE : [A-Z];
fragment DIGIT : [0-9];
fragment EQUALS: '=';

ARRAY : '[]';
COMMENT : '//' .*? '\n' -> skip;
ARROW: '->';

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
NULL: 'null';

ASSERT: EQUALS;
STRING: '"'.*?'"';

INT: '-'? DIGIT+;
FLOAT: INT'.'DIGIT+;
ID: (LOWERCASE|UPPERCASE|DIGIT|'_')+;
WS: [ \t\n\r]+ -> skip;
