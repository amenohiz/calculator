package com.calculator.handler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class Calculator {

    private BigDecimal curTotal;

    private BigDecimal newNum;

    private String operator;

    /**
     * 默认精度 保留2位小数
     */
    private static final Integer DEFAULT_SCALE = 2;

    /**
     * 默认取余方式 四舍五入
     */
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    private static final Stack<BigDecimal> undoStack = new Stack<>();

    private static final Stack<BigDecimal> redoStack = new Stack<>();

    /**
     * 计算
     */
    public int calc() {
        if (this.operator == null) {
            System.out.println("ERR:请输入运算符");
            return 0;
        }

        newNum = newNum == null ? BigDecimal.ZERO : newNum;
        if ("/".equals(operator) && BigDecimal.ZERO.compareTo(newNum) == 0) {
            System.out.println("ERR:除数不能为零或者空");
            return 0;
        }
        curTotal = curTotal == null ? BigDecimal.ZERO : curTotal;
        undoStack.add(curTotal);
        curTotal = calcTwoNum(curTotal, newNum, operator);
        return 1;
    }

    private BigDecimal calcTwoNum(BigDecimal curTotal, BigDecimal newNum, String operator) {
        BigDecimal res;
        switch (operator) {
            case "+":
                res = curTotal.add(newNum);
                break;
            case "-":
                res = curTotal.subtract(newNum);
                break;
            case "*":
                res = curTotal.multiply(newNum).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
                break;
            case "/":
                res = curTotal.divide(newNum, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
                break;
            default:
                throw new RuntimeException("运算符有误");
        }
        return res;
    }

    /**
     * 打印结果
     */
    public void display () {
        if (curTotal == null) {
            System.out.println("还未有计算结果");
        } else {
            System.out.println("计算结果:" + curTotal);
        }
    }

    /**
     * 撤销
     */
    public void undo() {
        if (undoStack.size() == 0) {
            System.out.println("无法撤销");
            return;
        }
        redoStack.add(curTotal);
        curTotal = undoStack.pop();
        System.out.println("撤销结果:" + curTotal);
    }

    /**
     * 回撤
     */
    public void redo() {
        if (redoStack.size() == 0) {
            System.out.println("无法回撤");
            return;
        }
        undoStack.add(curTotal);
        curTotal = redoStack.pop();
        System.out.println("撤销结果:" + curTotal);
    }

    /**
     * 键入计算值
     * @param newNum
     */
    public void setNewNum(BigDecimal newNum) {
        if (curTotal == null) {
            curTotal = newNum;
        } else {
            this.newNum = newNum;
        }
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
        curTotal = null;
        newNum = null;
        operator = null;
    }

    /**
     * ====================================================================================================
     * =======================================test begin===================================================
     * ====================================================================================================
     */

    private final static String CALC = "=";
    private final static List<String> OPERATORS = Arrays.asList("+", "-", "*", "/");
    private final static String DISPLAY = "display";
    private final static String EXIT = "exit";
    private final static String REDO = "redo";
    private final static String UNDO = "undo";
    private final static String CLEAR = "clear";
    private final static String NUMERIC_REGEX = "^\\d+(\\.\\d+)?$";


    public static void main(String[] args) {
        System.out.println("===================================计算器=================================");
        System.out.println("请输入数字(支持小数)、运算符(+、-、*、/),输入'='进行计算,过程中可使用'undo'或'redo'命令进行结果撤回或回撤");
        Calculator calculator = new Calculator();
        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile(NUMERIC_REGEX);
        while (scanner.hasNext()) {
            String next = scanner.next();
            if (pattern.matcher(next).matches()) {
                calculator.setNewNum(new BigDecimal(next));
            } else if (CALC.equals(next)) {
                int result = calculator.calc();
                if (1 == result) {
                    calculator.display();
                }
            } else if (DISPLAY.equals(next)) {
                calculator.display();
            } else if (OPERATORS.contains(next)) {
                calculator.setOperator(next);
            } else if (REDO.equals(next)) {
                calculator.redo();
                System.out.println("redo:" + redoStack.toString());
                System.out.println("undo:" + undoStack.toString());
            } else if (UNDO.equals(next)) {
                calculator.undo();
                System.out.println("redo:" + redoStack.toString());
                System.out.println("undo:" + undoStack.toString());
            } else if (EXIT.equals(next)) {
                System.out.println("结束！");
                break;
            } else if (CLEAR.equals(next)) {
                calculator.clear();
                System.out.println("clear success..");
            } else {
                System.out.println("输入有误请重新输入");
            }
        }
    }

    /**
     * ====================================================================================================
     * =======================================test end===================================================
     * ====================================================================================================
     */
}
