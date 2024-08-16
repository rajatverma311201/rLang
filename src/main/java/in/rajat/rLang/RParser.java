package in.rajat.rLang;

import in.rajat.rLang.enums.TokenType;
import in.rajat.rLang.expressions.*;
import in.rajat.rLang.models.Token;

import java.util.List;


/**
 * CONTEXT FREE GRAMMAR
 * PARSER   RECURSIVE DESCENT PARSING
 * <p>
 * expression     → equality ;
 * <br>
 * equality       → comparison ( ( "!=" | "==" ) comparison )* ;
 * <br>
 * comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
 * <br>
 * term           → factor ( ( "-" | "+" ) factor )* ;
 * <br>
 * factor         → unary ( ( "/" | "*" ) unary )* ;
 * <br>
 * unary          → ( "!" | "-" ) unary | primary ;
 * <br>
 * primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
 */
public class RParser {
    private final List<Token> tokens;
    private int current = 0;

    RParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expression parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private static class ParseError extends RuntimeException {
    }

    private ParseError error(Token token, String message) {
        RLang.error(token, message);
        return new ParseError();
    }

    private Expression expression() {
        return equality();
    }


    private Expression equality() {
        Expression expr = comparison();

        while (matchTokenTypes(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expression comparison() {
        Expression expr = term();

        while (matchTokenTypes(TokenType.LESS, TokenType.GREATER, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expression term() {
        Expression expr = factor();

        while (matchTokenTypes(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expression right = factor();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() {
        Expression expr = unary();

        while (matchTokenTypes(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expression right = unary();
            expr = new BinaryExpr(expr, operator, right);

        }
        return expr;
    }

//      unary      →   ( "!" | "-" ) unary | primary ;

    private Expression unary() {

        if (matchTokenTypes(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new UnaryExpr(operator, right);
        }
        return primary();
    }

    //  primary  → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
    private Expression primary() {
        if (matchTokenTypes(TokenType.FALSE)) return new LiteralExpr(false);
        if (matchTokenTypes(TokenType.TRUE)) return new LiteralExpr(true);
        if (matchTokenTypes(TokenType.NULL)) return new LiteralExpr(null);

        if (matchTokenTypes(TokenType.NUMBER, TokenType.STRING)) {
            return new LiteralExpr(previous().literal);
        }
        if (matchTokenTypes(TokenType.LEFT_PAREN)) {
            Expression expr = expression();

            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");

            return new GroupingExpr(expr);
        }
//        System.out.println(peek());

        throw error(peek(), "Expect expression.");
    }


    private Token consume(TokenType type, String message) {
        if (checkTokenType(type)) return nextToken();

        throw error(peek(), message);
    }

    private boolean checkTokenType(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return type == peek().type;
    }

    private boolean matchTokenTypes(TokenType... types) {
        for (TokenType type : types) {
            if (checkTokenType(type)) {
                nextToken();
                return true;
            }
        }
        return false;
    }

    private Token nextToken() {
        if (!isAtEnd()) {
            current++;
        }
        return peek();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void synchronize() {
        nextToken();

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            nextToken();
        }
    }
}
