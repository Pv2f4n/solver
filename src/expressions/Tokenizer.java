package expressions;

import java.util.Iterator;

/**
 * A wrapper class for a tokenizer iterator that allows for accessing the current (last returned)
 * token from the iterator.
 */
public class Tokenizer {

    /**
     * The token iterator that this class wraps.
     */
    private Iterator<Token> tokenizer;

    /**
     * The current token. Null if and only if next() is called after all elements have been
     * returned.
     */
    private Token curTok;

    /**
     * Initialize the tokenizer iterator to the given `tokenizer` and curTok to the first token
     * produced by next(). Requires that tokenizer has at least one token to return.
     */
    public Tokenizer(Iterator<Token> tokenizer) {
        assert tokenizer.hasNext();
        this.tokenizer = tokenizer;
        curTok = tokenizer.next();
    }

    /**
     * Returns the current token.
     */
    public Token curTok() {
        return curTok;
    }

    /**
     * Returns if this tokenizer's iterator still has more elements to return.
     */
    public boolean hasNext() {
        return tokenizer.hasNext();
    }
    /**
     * Returns the next token and updates curTok accordingly. If hasNext() is false, sets curTok
     * to null.
     */
    public Token next() {
        if (!hasNext()) {
            curTok = null;
            return null;
        } else {
            Token tok = tokenizer.next();
            curTok = tok;
            return tok;
        }
    }

}
