import java.util.*;

class Parser {

    Map<String, Object[]> tableOfVariables = new HashMap<>();
    List<String> tokensRPN = new ArrayList<>();
    private Stack<String> stack = new Stack<>();
    private List<Token> tokens = new ArrayList<>();
    private int position = 0;
    private int p1;
    private int p2;

    boolean lang(List<Token> tokens)
    {
        boolean lang = false;
        int majorTokens = 1;

        for (Token token : tokens)
        {
            if (token.getLexeme() != Lexeme.WS)
            {
                this.tokens.add(token);
            }
        }
        while (this.tokens.size() != position)
        {

            if (!expression())
            {
                System.err.println("Ошибка синтаксиса: " + majorTokens);
                System.exit(4);
            } else {
                majorTokens++;
                lang = true;
            }
        }

        return lang;
    }

    private boolean expression()
    {
        return init() || assign() || setAssign() || forModule();
    }

    private boolean assign()
    {
        boolean assign = false;
        int posittion = position;

        if (assignOperation())
        {
            if (getCurrentTokenLexemeInc() == Lexeme.SEMICOLON)
            {
                assign = true;
            }
        }
        position = assign ? position : posittion;
        return assign;
    }

    private boolean assignOperation() {
        boolean assignOperation = false;
        int posittion = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR)
        {
            add = tokensRPN.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.ASSIGN_OP)
            {
                if (!tableOfVariables.get(var)[0].equals("set"))
                {
                    op = getLastTokenValue();
                    if (value()) {
                        while (!stack.empty())
                        {
                            tokensRPN.add(stack.pop());
                        }
                        tokensRPN.add(op);
                        assignOperation = true;
                    }
                } else
                    {
                    System.err.println("Ошибка:Попытка присваивания значения переменной");
                    System.exit(321);
                }
            }
        }
        if (!assignOperation)
        {
            position = posittion;
            if (add)
            {
                tokensRPN.remove(tokensRPN.size() - 1);
            }
        }
        return assignOperation;
    }

    private boolean init()
    {
        boolean init = false;
        int posittion = position;
        String type, var;

        if (getCurrentTokenLexemeInc() == Lexeme.TYPE)
        {
            type = getLastTokenValue();
            if (getCurrentTokenLexemeInc() == Lexeme.VAR)
            {
                var = getLastTokenValue();
                if (getCurrentTokenLexemeInc() == Lexeme.SEMICOLON)
                {
                    if (type.equals("set"))
                    {
                        tableOfVariables.put(var, valueCreate(type, new MyLinkedList()));
                    } else
                        {
                        tableOfVariables.put(var, valueCreate(type, ""));
                    }
                    init = true;
                }
            }
        }
        position = init ? position : posittion;
        return init;
    }

    private boolean value() {
        if (val()) {
            while (valueOperation())
            {
            }
            return true;
        }
        return false;
    }

    private boolean valueOperation() {
        boolean valueOperation = false;
        int posittion = position;

        if (getCurrentTokenLexemeInc() == Lexeme.ARITHMETIC_OP) {
            String arithmeticOP = getLastTokenValue();
            if (!stack.empty()) {
                while (getPriority(arithmeticOP) <= getPriority(stack.peek())) {
                    tokensRPN.add(stack.pop());
                    if (stack.empty()) {
                        break;
                    }
                }
            }
            stack.push(arithmeticOP);
            if (val()) {
                valueOperation = true;
            }
        }
        position = valueOperation ? position : posittion;
        return valueOperation;
    }

    private boolean val() {
        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            if (!tableOfVariables.containsKey(getLastTokenValue())) {
                System.err.println("Ошибка: Переменная " + getLastTokenValue() + " не иниициализирована");
                System.exit(6);
            }
            tokensRPN.add(getLastTokenValue());
            return true;
        } else {
            position--;
        }
        if (getCurrentTokenLexemeInc() == Lexeme.DIGIT) {
            tokensRPN.add(getLastTokenValue());
            return true;
        } else {
            position--;
        }
        return breakValue();
    }

    private boolean breakValue() {
        boolean breakValue = false;
        int posittion = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_R_B) {
            stack.push(getLastTokenValue());
            if (value()) {
                if (getCurrentTokenLexemeInc() == Lexeme.R_R_B) {
                    while (!stack.peek().equals("(")) {
                        tokensRPN.add(stack.pop());
                    }
                    stack.pop();
                    breakValue = true;
                }
            }
        }
        position = breakValue ? position : posittion;
        return breakValue;
    }

    private boolean setAssign() {
        return setAdd() || setRemove();
    }

    private boolean forModule() {
        boolean forModule = false;
        int posittion = position;

        if (getCurrentTokenLexemeInc() == Lexeme.FOR) {
            if (exprFor()) {
                if (body()) {
                    forModule = true;
                    tokensRPN.set(p1, String.valueOf(tokensRPN.size() + 2));//прыжок через p2&!
                    tokensRPN.add(String.valueOf(p2));
                    tokensRPN.add("!");
                }
            }
        }
        position = forModule ? position : posittion;
        return forModule;
    }

    private boolean exprFor() {
        boolean exprFor = false;
        int posittion = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_R_B) {
            if (assign()) {
                if (logExprFor()) {
                    if (assignOperation()) {
                        if (getCurrentTokenLexemeInc() == Lexeme.R_R_B) {
                            exprFor = true;
                        }
                    }
                }
            }
        }
        position = exprFor ? position : posittion;
        return exprFor;
    }

    private boolean logExprFor() {
        boolean logExprFor = false;
        int posittion = position;

        p2 = tokensRPN.size();
        if (setContains() || setIsEmpty() || logExpr()) {
            if (getCurrentTokenLexemeInc() == Lexeme.SEMICOLON) {
                logExprFor = true;
                p1 = tokensRPN.size();
                tokensRPN.add("p1");
                tokensRPN.add("!F");
            }
        }
        position = logExprFor ? position : posittion;
        return logExprFor;
    }

    private boolean body() {
        boolean body = false;
        int posittion = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_F_B) {
            while (init() || assign() || setAssign()) {
            }
            if (getCurrentTokenLexemeInc() == Lexeme.R_F_B) {
                body = true;
            }
        }
        position = body ? position : posittion;
        return body;
    }

    private boolean logExpr() {
        boolean logExpr = false;
        int posittion = position;
        String op = "";
        ArrayList<String> stackPosittion = new ArrayList<>();

        if (assignOperation() || value()) {
            if (getCurrentTokenLexemeInc() == Lexeme.LOGIC_OP) {
                op = getLastTokenValue();
                while (!stack.empty()) {
                    String pop = stack.pop();
                    tokensRPN.add(pop);
                    stackPosittion.add(pop);
                }
                if (assignOperation() || value()) {
                    while (!stack.empty()) {
                        tokensRPN.add(stack.pop());
                    }
                    logExpr = true;
                    tokensRPN.add(op);
                    stackPosittion.clear();
                }
            }
        }
        if (!logExpr) {
            position = posittion;
            if (op.length() != 0) {
                for (int i = stackPosittion.size() - 1; i >= 0; i--) {
                    stack.push(stackPosittion.get(i));
                    tokensRPN.remove(tokensRPN.size() - 1);
                }
                stackPosittion.clear();
            }
        }
        return logExpr;
    }

    private boolean setIsEmpty() {
        boolean setIsEmpty = false;
        int posittion = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokensRPN.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.ISEMPTY) {
                if (tableOfVariables.get(var)[0].equals("set")) {
                    op = getLastTokenValue();
                    while (!stack.empty()) {
                        tokensRPN.add(stack.pop());
                    }
                    tokensRPN.add(op);
                    setIsEmpty = true;
                } else {
                    System.err.println("Ошибка: попытка добавить или не установить переменную");
                    System.exit(301);
                }
            }
        }
        if (!setIsEmpty) {
            position = posittion;
            if (add) {
                tokensRPN.remove(tokensRPN.size() - 1);
            }
        }
        return setIsEmpty;
    }

    private boolean setContains() {
        boolean setContains = false;
        int posittion = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokensRPN.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.CONTAINS) {
                if (tableOfVariables.get(var)[0].equals("set")) {
                    op = getLastTokenValue();
                    if (value()) {
                        while (!stack.empty()) {
                            tokensRPN.add(stack.pop());
                        }
                        tokensRPN.add(op);
                        setContains = true;

                    }
                } else {
                    System.err.println("Error: Try to contains to not Set variable");
                    System.exit(301);
                }
            }
        }
        if (!setContains) {
            position = posittion;
            if (add) {
                tokensRPN.remove(tokensRPN.size() - 1);
            }
        }
        return setContains;
    }

    private boolean setAdd() {
        boolean setAdd = false;
        int posittion = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokensRPN.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.ADD) {
                if (tableOfVariables.get(var)[0].equals("set")) {
                    op = getLastTokenValue();
                    if (value())
                    {
                        if (getCurrentTokenLexemeInc() == Lexeme.SEMICOLON)
                        {
                            while (!stack.empty()) {
                                tokensRPN.add(stack.pop());
                            }
                            tokensRPN.add(op);
                            setAdd = true;
                        }
                    }
                } else {
                    System.err.println("Error: Try to add to not Set variable");
                    System.exit(301);
                }
            }
        }
        if (!setAdd) {
            position = posittion;
            if (add) {
                tokensRPN.remove(tokensRPN.size() - 1);
            }
        }
        return setAdd;
    }

    private boolean setRemove() {
        boolean setRemove = false;
        int posittion = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokensRPN.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.REMOVE) {
                if (tableOfVariables.get(var)[0].equals("set")) {
                    op = getLastTokenValue();
                    if (value()) {
                        if (getCurrentTokenLexemeInc() == Lexeme.SEMICOLON) {
                            while (!stack.empty()) {
                                tokensRPN.add(stack.pop());
                            }
                            tokensRPN.add(op);
                            setRemove = true;
                        }
                    }
                } else {
                    System.err.println("Error: Try to remove from not Set variable");
                    System.exit(301);
                }
            }
        }
        if (!setRemove) {
            position = posittion;
            if (add) {
                tokensRPN.remove(tokensRPN.size() - 1);
            }
        }
        return setRemove;
    }

    private Lexeme getCurrentTokenLexemeInc() {
        try {
            return tokens.get(position++).getLexeme();
        } catch (IndexOutOfBoundsException ex) {
            position--;
            System.err.println("Ошибка: лексема \"" + tokens.get(--position).getLexeme() + "\" ожидается");
            System.exit(3);
        }
        return null;
    }

    private String getLastTokenValue() {
        return tokens.get(position - 1).getValue();
    }

    Object[] valueCreate(String type, Object value) {
        Object[] ret = new Object[2];

        ret[0] = type;
        ret[1] = value;
        return ret;
    }

    String printTOV(Map<String, Object[]> tov) {
        StringBuilder s = new StringBuilder();
        Set<String> keys = tov.keySet();
        String[] keyss = keys.toArray(new String[0]);

        s.append("[");
        for (int i = 0; i < keyss.length - 1; i++) {
            Object[] values = tov.get(keyss[i]);
            String value0 = values[0].toString();
            String value1 = values[1].toString();


            s.append(keyss[i]);
            s.append(", [");
            s.append(value0);
            s.append(", ");
            if (value1.equals("")) {
                s.append("null");
            } else {
                s.append(value1);
            }
            s.append("], ");
        }
        String value0 = tov.get(keyss[keyss.length - 1])[0].toString();
        String value1 = tov.get(keyss[keyss.length - 1])[1].toString();

        s.append(keyss[keyss.length - 1]);
        s.append(", [");
        s.append(value0);
        s.append(", ");
        if (value1.equals("")) {
            s.append("null");
        } else {
            s.append(value1);
        }
        s.append("]");
        s.append("]");
        return s.toString();
    }

    private int getPriority(String str) {
        switch (str) {
            case "+":
                return 1;
            case "*":
                return 2;
            case "-":
                return 1;
            case "/":
                return 2;
            case "(":
                return 0;
            default:
                System.err.println("Ошибка в символе " + str);
                System.exit(5);
                return 0;
        }
    }
}