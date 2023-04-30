package ch.epfl.javions.aircraft;

import java.io.*;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Represent the micronics database of an aircraft
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class AircraftDatabase {
    private final String fileName;
    private static final int REGISTRATION_INDEX = 1;
    private static final int DESIGNATOR_INDEX = 2;
    private static final int MODEL_INDEX = 3;
    private static final int DESCRIPTION_INDEX = 4;
    private static final int WTC_INDEX = 5;

    /**
     * Public Constructor
     * @param fileName : the name associated to the Zip File
     * @throws NullPointerException if fileName is null
     */
    public AircraftDatabase(String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }

    /**
     * Find the complementary information about an aircraft through its given IcaoAddress
     * @param address : IcaoAddress of a random aircraft
     * @throws IOException if there's an input/output error
     * @return the aircraftData associated to the given IcaoAddress
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        String csvFileName = address.string().substring(4, 6) + ".csv";
        try (ZipFile zipFile = new ZipFile(fileName);
             InputStream fileInZip = zipFile.getInputStream(zipFile.getEntry(csvFileName));
             Reader fileInZipReader = new InputStreamReader(fileInZip, UTF_8);
             BufferedReader b = new BufferedReader(fileInZipReader)) {

            String currLine = "";
            while ((currLine = b.readLine()) != null) {
                if (currLine.compareTo(address.string()) >= 0)
                    break;
            }
            if(currLine == null || !currLine.startsWith(address.string()))
                return null;

            String[] aircraftData = currLine.split(",", -1);
            AircraftRegistration registration = new AircraftRegistration(aircraftData[REGISTRATION_INDEX]);
            AircraftDescription description = new AircraftDescription(aircraftData[DESCRIPTION_INDEX]);
            AircraftTypeDesignator designator = new AircraftTypeDesignator(aircraftData[DESIGNATOR_INDEX]);
            WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of(aircraftData[WTC_INDEX]);

            String model = aircraftData[MODEL_INDEX];
            return new AircraftData(registration, designator, model, description, wakeTurbulenceCategory);
        }
    }
}

