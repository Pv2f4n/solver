package expressions.exceptions;

/**
 * Indicates that a character not corresponding to any allowed token types is present in an
 * input expression during lexing.
 */
public class UnreadableCharacterException extends Exception {

    /**
     * The non-readable character.
     */
    private final Character character;

    /**
     * Create a new UnreadableCharacterException indicating that the character `character` was
     * present in the input expression but does not correspond to any allowed token types.
     */
    public UnreadableCharacterException(Character character) {
        super("Character" + character + "does not correspond to an allowed token type");
        this.character = character;
    }

    /**
     * Returns the character that led to this exception.
     */
    public Character character() {return character;}
}
