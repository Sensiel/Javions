package ch.epfl.javions.aircraft;

import java.util.Objects;

public record AircraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {
    /**
     * Compact Constructor
     * @param registration of the aircraft
     * @param typeDesignator of the aircraft
     * @param model of the aircraft
     * @param description of the aircraft
     * @param wakeTurbulenceCategory of the aircraft
     * @throws NullPointerException if one of the arguments is null
     */
    public AircraftData{
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
