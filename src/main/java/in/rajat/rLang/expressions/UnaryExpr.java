package in.rajat.rLang.expressions;

import in.rajat.rLang.models.Token;

public class UnaryExpr implements Expression {
    public final Token operator;
    public final Expression right;

    public UnaryExpr(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R> R accept(Expression.Visitor<R> visitor) {
        return visitor.visitUnaryExpr(this);
    }

}
