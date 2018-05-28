import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Parser {

    Map<String, Integer> tableOfVariables = new HashMap<>();
    List<String> tokens_polis = new ArrayList<>();
    private Stack<String> stack = new Stack<>();
    private List<Token> tokens = new ArrayList<>();
    private int position = 0;
    private int p1;
    private int p2;

    boolean lang(List<Token> tokens) {
        boolean lang = false;
        for (Token token : tokens) {
            if (token.getLexeme() != Lexeme.WS) {
                this.tokens.add(token);
            }
        }
        while (this.tokens.size() != position) {
            if (!expr()) {
                System.err.println(" Error: Syntax mistake ");
                System.exit(4);
            } else
                lang = true;
        }
        System.out.println(tokens_polis);
        return lang;
    }

    private boolean expr() {
        boolean expr = false;

        if (init() || assign() || for_loop()) {
            expr = true;
        }
        return expr;
    }

    private boolean init() {
        boolean init = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.TYPE) {
            if (assign_op()) {
                if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                    init = true;
                }
            }
        }
        position = init ? position : old_position;
        return init;
    }

    private boolean assign() {
        boolean assign = false;
        int old_position = position;

        if (assign_op()) {
            if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                assign = true;
            }
        }
        position = assign ? position : old_position;
        return assign;
    }

    private boolean assign_op() {
        boolean assign_op = false;
        int old_position = position;
        boolean add = false;
        String var = null;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokens_polis.add(getLastTokenValue());
            var = getLastTokenValue();
            if (getCurrentTokenLexemeInc() == Lexeme.ASSIGN_OP) {
                stack.push(getLastTokenValue());
                if (value()) {
                    assign_op = true;
                    tableOfVariables.put(var, 0);
                }
            }
        }
        if (add && !assign_op) {
            tokens_polis.remove(tokens_polis.size()-1);
        }
        if (assign_op) {
            while (!stack.empty()) {
                tokens_polis.add(stack.pop());
            }
        }
        position = assign_op ? position : old_position;
        return assign_op;
    }

    private boolean value() {
        boolean value = false;

        if (val()) {
            while (OPval()) {
            }
            value = true;
        }
        return value;
    }

    private boolean OPval() {
        boolean OPval = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.OP) {
            String arthOp = getLastTokenValue();
            while (getPriority(arthOp) <= getPriority(stack.peek())) {
                tokens_polis.add(stack.pop());
            }
            stack.push(arthOp);
            if (val()) {
                OPval = true;
            }
        }
        position = OPval ? position : old_position;
        return OPval;
    }

    private boolean val() {
        boolean val = false;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            tokens_polis.add(getLastTokenValue());
            if (!tableOfVariables.containsKey(getLastTokenValue())) {
                System.err.println("Error: Variety " + getLastTokenValue() + " not initialize");
                System.exit(6);
            }
            return true;
        } else {
            position--;
        }
        if (getCurrentTokenLexemeInc() == Lexeme.DIGIT) {
            tokens_polis.add(getLastTokenValue());
            return true;
        } else {
            position--;
        }
        if (break_value()) {
            return true;
        }
        return val;
    }

    private boolean break_value() {
        boolean break_value = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_R_SQU) {
            stack.push(getLastTokenValue());
            if (value()) {
                if (getCurrentTokenLexemeInc() == Lexeme.R_R_SQU) {
                    while (!stack.peek().equals("(")) {
                        tokens_polis.add(stack.pop());
                    }
                    stack.pop();

                    break_value = true;
                }
            }
        }
        position = break_value ? position : old_position;
        return break_value;
    }

    private boolean for_loop() {
        boolean for_loop = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.FOR) {
            if (for_expr()) {
                if (for_body()) {
                    for_loop = true;
                    tokens_polis.set(p1 ,String.valueOf(tokens_polis.size()+2));
                    tokens_polis.add(String.valueOf(p2));
                    tokens_polis.add("!");
                }
            }
        }
        position = for_loop ? position : old_position;
        return for_loop;
    }

    private boolean for_body() {
        boolean for_body = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_F_SQU) {
            while (for_form_square()) {
            }
            if (getCurrentTokenLexemeInc() == Lexeme.R_F_SQU) {
                for_body = true;
            }
        }
        position = for_body ? position : old_position;
        return for_body;
    }

    private boolean for_form_square() {
        boolean for_form_square = false;

        if (init() || assign()) {
            for_form_square = true;
        }
        return for_form_square;
    }

    private boolean for_expr() {
        boolean for_expr = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_R_SQU) {
            if (start_expr()) {
                if (log_expr()) {
                    if (assign_op()) {
                        if (getCurrentTokenLexemeInc() == Lexeme.R_R_SQU) {
                            for_expr = true;
                        }
                    }
                }
            }
        }
        position = for_expr ? position : old_position;
        return for_expr;
    }

    private boolean log_expr() {
        boolean log_expr = false;
        int old_position = position;

        p2 = tokens_polis.size();
        if (assign_op() || value()) {
            if (getCurrentTokenLexemeInc() == Lexeme.LOG_OP) {
                String log_op = getLastTokenValue();
                if (assign_op() || value()) {
                    if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                        log_expr = true;
                        tokens_polis.add(log_op);
                        p1 = tokens_polis.size();
                        tokens_polis.add("p1");
                        tokens_polis.add("!F");
                    }
                }
            }
        }
        position = log_expr ? position : old_position;
        return log_expr;
    }

    private boolean start_expr() {
        boolean start_expr = false;

        if (init() || assign()) {
            start_expr = true;
        }
        return start_expr;
    }

    private Lexeme getCurrentTokenLexemeInc() {
        try {
            return tokens.get(position++).getLexeme();
        } catch (IndexOutOfBoundsException ex) {
            System.err.println("Error: Lexeme \"" + Lexeme.TYPE + "\" expected");
            System.exit(3);
        }
        return null;
    }

    private String getLastTokenValue() {
        return tokens.get(position-1).getValue();
    }

    private int getPriority(String str) {
        switch (str) {
            case "+":
                return 1;
            case "*":
                return 2;
            case "^":
                return 2;
            case "-":
                return 1;
            case "/":
                return 2;
            case "%":
                return 2;
            case "=":
                return 0;
            case "(":
                return 0;
            default:
                System.err.println("Error: In symbol " + str);
                System.exit(5);
                return 0;
        }
    }
}