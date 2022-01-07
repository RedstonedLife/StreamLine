package net.plasmere.streamline.utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MathUtils {
    public static double eval(String function) {
        Expression expression = new ExpressionBuilder(function).build();
        return expression.evaluate();
    }
}
