package ch.epfl.javions;

import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WakeTurbulenceCategoryTest {
    @Test
    void workOnTheRightAbbreviations(){
        assertEquals(WakeTurbulenceCategory.HEAVY,WakeTurbulenceCategory.of("H"));
        assertEquals(WakeTurbulenceCategory.LIGHT,WakeTurbulenceCategory.of("L"));
        assertEquals(WakeTurbulenceCategory.MEDIUM,WakeTurbulenceCategory.of("M"));
    }

    @Test
    void failsToFindTheWTC(){
        assertEquals(WakeTurbulenceCategory.UNKNOWN,WakeTurbulenceCategory.of(""));
        assertEquals(WakeTurbulenceCategory.UNKNOWN,WakeTurbulenceCategory.of("R"));

    }

}
