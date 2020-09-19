package wsg.tools.common.constant;

/**
 * Enum for characters of ASCII.
 *
 * @author Kingen
 * @since 2020/9/17
 */
public enum SignEnum {

    /**
     * Printable characters
     */
    SPACE(' '),
    EXCLAMATION('!'),
    QUOTES('"'),
    HASH('#'),
    DOLLAR('$'),
    PERCENT('%'),
    AND('&'),
    QUOTE('\''),
    LPAREN('('),
    RPAREN(')'),
    ASTERISK('*'),
    PLUS('+'),
    COMMA(','),
    MINUS('-'),
    DOT('.'),
    SLASH('/'),
    COLON(':'),
    SEMI(';'),
    LT('<'),
    EQUAL('='),
    GT('>'),
    QUESTION('?'),
    AT('@'),
    LBRACKET('['),
    BACKSLASH('\\'),
    RBRACKET(']'),
    CARET('^'),
    UNDERSCORE('_'),
    ACCENT('`'),
    LBRACE('{'),
    BAR('|'),
    RBRACE('}'),
    TILDE('~'),
    ;

    public static final SignEnum[] NOT_PERMIT_SIGNS_FOR_FILENAME = new SignEnum[]{
            COLON, ASTERISK, QUESTION, QUOTES, LT, GT, BAR
    };

    private final char c;

    SignEnum(char c) {this.c = c;}

    public char getC() {
        return c;
    }

    @Override
    public String toString() {
        return String.valueOf(c);
    }
}
