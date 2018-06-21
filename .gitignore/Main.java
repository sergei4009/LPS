public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        StackMachine stackMachine = new StackMachine();

        final String string = "set a; set c; int b; for (b =4; b<5; b=b+1) { a add 4; a add 3; c add 7; c remove 7; }";
        System.out.println();
        System.out.println("Входная строка: \n" + string);
        System.out.println();
        System.out.println(parser.lang(lexer.recognize(string)));
        System.out.println("POLIS: \n" + parser.tokensRPN);
        System.out.println();
        System.out.println(parser.printTOV(stackMachine.stackMachine(parser)));
    }
}