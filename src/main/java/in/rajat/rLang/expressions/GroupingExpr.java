package in.rajat.rLang.expressions;

public class GroupingExpr implements Expression {
    final Expression expression;

    GroupingExpr(Expression expression) {
        this.expression = expression;
    }

    @Override
    public <R> R accept(Expression.Visitor<R> visitor) {
        return visitor.visitGroupingExpr(this);
    }

}