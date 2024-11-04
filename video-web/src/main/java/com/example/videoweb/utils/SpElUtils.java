package com.example.videoweb.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Spring EL表达式解析工具类
 * 提供方法来解析方法参数中的EL表达式，并获取方法的唯一键
 */
public class SpElUtils {
    // 表达式解析器
    private static final ExpressionParser parser = new SpelExpressionParser();
    // 参数名发现器，用于获取方法参数名
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 解析EL表达式
     *
     * @param method 方法对象
     * @param args 方法参数
     * @param spEl EL表达式字符串
     * @return 解析后的字符串
     */
    public static String parseSpEl(Method method, Object[] args, String spEl) {
        // 解析参数名，如果无法解析则使用空数组
        String[] params = Optional.ofNullable(parameterNameDiscoverer.getParameterNames(method)).orElse(new String[]{});
        // 创建EL表达式解析上下文
        EvaluationContext context = new StandardEvaluationContext();
        // 将方法参数及其对应的值放入上下文中
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        // 解析EL表达式
        Expression expression = parser.parseExpression(spEl);
        // 获取解析后的值并转换为字符串返回
        return expression.getValue(context, String.class);
    }

    /**
     * 获取方法的唯一键
     *
     * @param method 方法对象
     * @return 方法的唯一键
     */
    public static String getMethodKey(Method method) {
        // 拼接方法所在类和方法名作为唯一键
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
