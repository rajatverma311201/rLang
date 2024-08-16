package in.rajat.rLang.expressions;

import in.rajat.rLang.models.Token;

public class BinaryExpr implements Expression {
    public final Expression left;
    public final Token operator;
    public final Expression right;

    public BinaryExpr(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R> R accept(Expression.Visitor<R> visitor) {
        return visitor.visitBinaryExpr(this);
    }
}
