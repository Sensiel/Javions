package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * Collect the fixed data of an aircraft
 * @author Zablocki Victor (361602)
 * @param registration : the registration of the aircraft
 * @param typeDesignator : the typeDesignator of the aircraft
 * @param model : the model of the aircraft
 * @param description : the description of the aircraft
 * @param wakeTurbulenceCategory : the WTC of the aircraft
 */
public record AircraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {
    /**
     * Compact Constructor
     * @param registration : the registration of the aircraft
     * @param typeDesignator : the typeDesignator of the aircraft
     * @param model : the model of the aircraft
     * @param description : the description of the aircraft
     * @param wakeTurbulenceCategory : the WTC of the aircraft
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
