package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDatabase {
    String fileName;
    private static final int REGISTRATION_INDEX = 1;
    private static final int DESIGNATOR_INDEX = 2;
    private static final int MODEL_INDEX = 3;
    private static final int DESCRIPTION_INDEX = 4;
    private static final int WTC_INDEX = 5;


    /**
     * Constructor
     *
     * @param fileName : the name associated to the Zip File
     */
    public AircraftDatabase(String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }

    /**
     * Find the complementary information about an aircraft through its given IcaoAddress
     *
     * @param address : IcaoAddress of an aircraft
     * @return the aircraftData associated to given IcaoAddress
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        String csvFileName = address.string().substring(4, 6) + ".csv";
        //System.out.println(csvFileName + " " + address.string());
        try (ZipFile zipFile = new ZipFile(fileName);
             InputStream fileInZip = zipFile.getInputStream(zipFile.getEntry(csvFileName));
             Reader fileInZipReader = new InputStreamReader(fileInZip, UTF_8);
             BufferedReader b = new BufferedReader(fileInZipReader)) {

            String l = "";
            while ((l = b.readLine()) != null) {
                if (l.compareTo(address.string()) >= 0) {
                    break;
                }
            }
            if(l == null || !l.startsWith(address.string())){
                return null;
            }

            String[] aircraftData = l.split(",");
            AircraftRegistration registration = new AircraftRegistration(aircraftData[REGISTRATION_INDEX]);//meilleure encapsulation pour ce bloc ?
            AircraftDescription description = new AircraftDescription(aircraftData[DESCRIPTION_INDEX]);
            AircraftTypeDesignator designator = new AircraftTypeDesignator(aircraftData[DESIGNATOR_INDEX]);
            WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of(aircraftData[WTC_INDEX]);
            String model = aircraftData[MODEL_INDEX];
            return new AircraftData(registration, designator, model, description, wakeTurbulenceCategory);
        }
    }
}

