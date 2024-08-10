package in.rajat.rLang;

import in.rajat.rLang.enums.TokenType;
import in.rajat.rLang.interfaces.RScannerIF;
import in.rajat.rLang.models.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RScanner implements RScannerIF {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    RScanner(String source) {
        this.source = source;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    public List<Token> scanTokens() {

        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }


    private void scanToken() {
        char c = nextChar();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;

            case '!':
                addToken(matchNextChar('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(matchNextChar('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(matchNextChar('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(matchNextChar('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;

            case '/':
                if (matchNextChar('/')) {
                    // A comment goes until the end of the line.
                    while (lookAheadChar() != '\n' && !isAtEnd()) {
                        nextChar();
                    }

                } else {
                    addToken(TokenType.SLASH);
                }

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                line++;
                break;

            case '"':
                stringTokenHandler();
                break;


            default:

                if (isDigit(c)) {
                    numberTokenHandler();
                } else if (isAlpha(c)) {
                    identifierTokenHandler();
                } else {
                    RLang.error(line, "Unexpected character [` " + c + " `]");
                }
                break;
        }
    }

    private char nextChar() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private boolean matchNextChar(char expected) {
        if (isAtEnd()) return false;

        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private char lookAheadChar() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char lookAheadNextChar() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }


    private void stringTokenHandler() {
        while (lookAheadChar() != '"' && !isAtEnd()) {
            if (lookAheadChar() == '\n') line++;
            nextChar();
        }

        if (isAtEnd()) {
            RLang.error(line, "Unterminated string.");
            return;
        }

        // The closing string character ".
        nextChar();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void numberTokenHandler() {
        while (isDigit(lookAheadChar())) nextChar();

        // Look for a fractional part.
        if (lookAheadChar() == '.' && isDigit(lookAheadNextChar())) {

            /*
            // Consume the "." character
                nextChar();
                while (isDigit(lookAheadChar())) {
                    nextChar();
                }
            */

//            loop runs first time then consumes "." character
//            and then check if nextchar is digit or not

            do {
                nextChar();
            } while (isDigit(lookAheadChar()));


        }

        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void identifierTokenHandler() {
        while (isAlphaNumeric(lookAheadChar())) nextChar();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            addToken(TokenType.IDENTIFIER);
        }
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }


}
