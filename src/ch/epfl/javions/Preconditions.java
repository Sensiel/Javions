package ch.epfl.javions;

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
