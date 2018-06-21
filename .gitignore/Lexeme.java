import java.util.regex.Pattern;

public enum Lexeme {
    TYPE(Pattern.compile("^int|set$")),
    ADD(Pattern.compile("^add$")),
    REMOVE(Pattern.compile("^remove$")),
    CONTAINS(Pattern.compile("^contains$")),
    ISEMPTY(Pattern.compile("^isempty$")),
    CLEAR(Pattern.compile("^clear$")),
    GET(Pattern.compile("^get$")),
    FOR(Pattern.compile("^for$")),
    VAR(Pattern.compile("^[a-z]+$")),
    ASSIGN_OP(Pattern.compile("^=$")),//+
    DIGIT(Pattern.compile("^0|[1-9][0-9]*")),
    ARITHMETIC_OP(Pattern.compile("^\\+|-|\\*|/$")),
    LOGIC_OP(Pattern.compile("^<|>|<=|>=|!=|==$")),
    INVERSE(Pattern.compile("!")),
    WS(Pattern.compile("^\\s+$")),
    L_F_B(Pattern.compile("^\\{$")),
    R_F_B(Pattern.compile("^}$")),
    L_R_B(Pattern.compile("^\\($")),
    R_R_B(Pattern.compile("^\\)$")),
    SEMICOLON(Pattern.compile("^;$"));

    Pattern pattern;

    Lexeme(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
