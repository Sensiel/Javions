package ch.epfl.javions;

/**
 * Used to verify method's preconditions (i.e. conditions that must be satisfied by arguments)
 * @author Imane Raihane (362230)
 */
public final class Preconditions {
    private Preconditions(){

    }

    /**
     * Check if the given boolean is true
     * @param shouldBeTrue : the condition needed to be verified
     * @throws IllegalArgumentException if shouldBeTrue isn't true
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue) throw new IllegalArgumentException();
    }
}
