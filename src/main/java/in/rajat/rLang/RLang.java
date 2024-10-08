package in.rajat.rLang;

import in.rajat.rLang.enums.TokenType;
import in.rajat.rLang.expressions.Expression;
import in.rajat.rLang.models.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RLang {
    static boolean hadError = false;


    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: rlang [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
            System.out.println("FILE RUNNER");
        } else {
            runPrompt();
            System.out.println("PROMPT RUNNER");
        }
    }


    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.print("> ");
            String line = reader.readLine();
//            System.out.println(line);
            if (line == null || line.equals("exit")) break;
            run(line);
        }
    }

    private static void run(String source) {
        RScanner scanner = new RScanner(source);
        List<Token> tokens = scanner.scanTokens();

        RParser parser = new RParser(tokens);
        Expression expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    private static void report(int line, String where, String message) {
        System.err.println(
                "[line "
                        + line
                        + "] Error"
                        + where
                        + ": "
                        + message);

        hadError = true;
    }

}

