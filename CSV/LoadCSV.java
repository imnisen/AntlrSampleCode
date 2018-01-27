import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class LoadCSV {
    public static class Loader extends CSVBaseListener {
        public static final String EMPTY = "";

        List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        List<String> header;
        List<String> currentRowFieldValues;

        public void exitHeader(CSVParser.HeaderContext ctx) {
            header = new ArrayList<String>();
            header.addAll(currentRowFieldValues);
        }

        public void enterRow(CSVParser.RowContext ctx) {
            currentRowFieldValues = new ArrayList<String>();
        }

        public void exitRow(CSVParser.RowContext ctx) {
            if (ctx.getParent().getRuleIndex() == CSVParser.RULE_header ) {
                return;
            } else {
                Map<String, String> m = new LinkedHashMap<String, String>();
                int i = 0;
                for (String v: currentRowFieldValues) {
                    m.put(header.get(i), v);
                    i++;
                }
                rows.add(m);
            }
        }

        public void exitString(CSVParser.StringContext ctx) {
            currentRowFieldValues.add(ctx.STRING().getText());
        }

        public void exitText(CSVParser.TextContext ctx) {
            currentRowFieldValues.add(ctx.TEXT().getText());
        }

        public void exitEmpty(CSVParser.EmptyContext ctx) {
            currentRowFieldValues.add(EMPTY);
        }

    }

    public static void main(String[] args) throws Exception {
        String inputfile = null;
        if (args.length>0) inputfile = args[0];
        InputStream is = System.in;
        if (inputfile != null) is = new FileInputStream(inputfile);

        CSVLexer lexer = new CSVLexer(new ANTLRInputStream(is));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSVParser parser = new CSVParser(tokens);

        parser.setBuildParseTree(true);
        ParseTree tree = parser.file();

        ParseTreeWalker walker = new ParseTreeWalker();
        Loader loader = new Loader();
        walker.walk(loader, tree);
        System.out.println(loader.rows);



    }
}
