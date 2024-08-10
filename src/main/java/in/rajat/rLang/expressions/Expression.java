package in.rajat.rLang.expressions;

public interface Expression {

    interface Visitor<R> {
        //        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(BinaryExpr expr);

        //        R visitCallExpr(Call expr);
//        R visitGetExpr(Get expr);
        R visitGroupingExpr(GroupingExpr expr);

        R visitLiteralExpr(LiteralExpr expr);

        //        R visitLogicalExpr(Logical expr);
//        R visitSetExpr(Set expr);
//        R visitSuperExpr(Super expr);
//        R visitThisExpr(This expr);
        R visitUnaryExpr(UnaryExpr expr);
//        R visitVariableExpr(Variable expr);
    }

    <R> R accept(Visitor<R> visitor);

}
