package in.rajat.rLang;

import in.rajat.rLang.enums.TokenType;
import in.rajat.rLang.expressions.*;


public class Interpreter implements Expression.Visitor<Object> {

    @Override
    public Object visitGroupingExpr(GroupingExpr expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr expr) {
        Object right = evaluate(expr.right);

        Object ans;

        ans = switch (expr.operator.type) {
            case BANG -> !isTruthy(right);
            case MINUS -> -(double) right;
            default -> null;
        };
        return ans;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr expr) {

        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            // Relational Operators
            case GREATER:
                return (double) left > (double) right;
            case GREATER_EQUAL:
                return (double) left >= (double) right;
            case LESS:
                return (double) left < (double) right;
            case LESS_EQUAL:
                return (double) left <= (double) right;

            // Arithmetic Operators
            case MINUS:
                return (double) left + (double) right;
            case SLASH:
                return (double) left / (double) right;
            case STAR:
                return (double) left * (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String || right instanceof String) {
                    return left.toString() + right.toString();
                }

                break;

            // Equality Check operators
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }
        return null;
    }


    private Object evaluate(Expression expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Boolean) {
            return ((boolean) obj);
        }

        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

}
