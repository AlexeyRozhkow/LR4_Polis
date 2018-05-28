import java.util.List;

public class LR4 {
    public static void main(String[] args) {

        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        StackC stackC = new StackC();
        String input = "int z = 16; int s = 9; int k=1; for (int i=1; i < 8 ; i=i+2) {k= (s+1)/i; s = s*2; z=s%(k+5);}";
        List<Token> tokens = lexer.recognize(input);

        /*System.out.println('\n');
        for (Token token : tokens) {
            System.out.println(token);
        }*/
        System.out.println('\n');
        System.out.println("[ " + input + " ]");
        System.out.println('\n');
        System.out.println(parser.lang(tokens));
        System.out.println('\n');
        System.out.println(stackC.stackC(parser));

    }
}
