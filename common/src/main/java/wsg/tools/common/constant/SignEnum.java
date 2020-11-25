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

    public static final String FILE_EXTENSION_SEPARATOR = DOT.toString();
    public static final String PARAMETER_SEPARATOR = AND.toString();
    public static final String URL_PATH_SEPARATOR = SLASH.toString();

    public static final char[] NOT_PERMIT_CHARS_FOR_FILENAME = new char[]{
            ':', '*', '?', '"', '<', '>', '|'
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
