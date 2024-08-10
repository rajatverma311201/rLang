package in.rajat.rLang.expressions;

public class LiteralExpr implements Expression {
    final Object value;

    LiteralExpr(Object value) {
        this.value = value;
    }

    @Override
    public <R> R accept(Expression.Visitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }

}