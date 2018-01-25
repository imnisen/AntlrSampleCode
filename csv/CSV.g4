grammar CSV;

file: header row+ ;
header: row;
row: field (',' field)* '\r'? '\n';
field
     : TEXT    # text
     | STRING  # string
     |         # empty
     ;

TEXT: ~[,\r\n]+;
STRING: '"' (~'"'|'""')* '"';