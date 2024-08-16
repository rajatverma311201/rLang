package in.rajat.rLang.expressions;

public class LiteralExpr implements Expression {
    public final Object value;

    public LiteralExpr(Object value) {
        this.value = value;
    }

    @Override
    public <R> R accept(Expression.Visitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }

}