//header
grammar toya;

// --------- RULES ---------
compilation: function*;
function: functionSignature '{' statement* '}';
functionSignature: 'function' name '('functionArgument* (','functionArgument)*')' ('->' type)?;
functionArgument: name ':' type (ASSERT expression)?;

expression : functionCall #FunCall
           | value #Val
           | reference #VarReference
           | ifExpression #If
           | matchExpression #Match
           |  '('expression '*' expression')' #Multiply
           | expression '*' expression  #Multiply
           | '(' expression '/' expression ')' #Divide
           | expression '/' expression #Divide
           | '(' expression '+' expression ')' #Add
           | expression '+' expression #Add
           | '(' expression '-' expression ')' #Subtract
           | expression '-' expression #Subtract;

type: 'int'ARRAY*
     | 'short'ARRAY*
     | 'long'ARRAY*
     | 'float'ARRAY*
     | 'double'ARRAY*
     | 'char'ARRAY*
     | 'byte'ARRAY*
     | 'string'ARRAY*
     | 'void'ARRAY*;

nonArrayType: 'int'
            | 'short'
            | 'long'
            | 'float'
            | 'double'
            | 'char'
            | 'byte'
            | 'string'
            | 'void';

reference: ID;
name: ID;
value: INT
     | FLOAT
     | STRING;

statement: variableDeclaration
         | printStatement
         | functionCall
         | returnStatement
         | ifExpression
         | forStatement
         | matchExpression;

arrayDeclaration: 'new' nonArrayType'['expression']'; // new int[8+1]
variableDeclaration: VARIABLE name ASSERT (expression|arrayDeclaration); // var c = a+b+3
printStatement: PRINT expression;
functionCall: name'('expression? (',' expression)*')';
returnStatement: 'return' #ReturnVoid
               | ('return')? expression #ReturnValue;

// if
ifExpression: ifCondition ifBranch (ELSE ifBranch (ELSE ifCondition ifBranch)*)?;
ifCondition: IF'(' expression ')';
ifBranch: flexStatementExpression;

// for
forStatement: forHead '{' statement* '}';
forHead: FOR'(' variableDeclaration? ';' forCondition? ';' incrementExpression? ')';
forCondition: expression;
incrementExpression: expression;

// switch
matchExpression: matchHead '{' matchBranch* matchDefault '}';
matchHead: MATCH'('expression')';
matchBranch: value '->' flexStatementExpression;
matchDefault: 'default' '->' flexStatementExpression;

flexStatementExpression: ('{'(statement|expression)*'}'|expression);


// --------- TOKENS ---------
fragment LOWERCASE : [a-z];
fragment UPPERCASE : [A-Z];
fragment DIGIT : [0-9];
fragment EQUALS: '=';

ARRAY : '['']';
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
PRINT: 'print';

ASSERT: EQUALS;
STRING: '"'.*?'"';

INT: DIGIT+;
FLOAT: DIGIT+'.'DIGIT+;
ID: (LOWERCASE|UPPERCASE|DIGIT|'_')+;
WS: [ \t\n\r]+ -> skip;
