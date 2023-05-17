package ch.epfl.javions.gui;


import ch.epfl.javions.WebMercator;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Represent an OSM tile manager
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class TileManager {
    /**
     * Represent the identity of an OSM tile
     * @param zoom : zoom level of the tile
     * @param x : the index x of the tile
     * @param y : the index x of the tile
     */
    public record TileId(int zoom, int x, int y) {

        /**
         * Check the validity of the identity of the OSM tile
         * @param zoom : zoom level of the tile
         * @param x : the index x of the tile
         * @param y : the index x of the tile
         * @return true if all the given arguments are valid
         */
        public static boolean isValid(int zoom, int x, int y) {
            boolean zoomIsValid = zoom >= WebMercator.ZOOM_MIN && zoom <= WebMercator.ZOOM_MAX;
            boolean xIsValid = x >= 0 && x < 1 << zoom;
            boolean yIsValid = y >= 0 && y < 1 << zoom;
            return zoomIsValid && xIsValid && yIsValid;
        }
    }
    private static final int CASH_MEMORY_CAPACITY = 100;
    private final Path tilesPath;
    private final String serverName;
    private final LinkedHashMap<TileManager.TileId, Image> cashMemory = new LinkedHashMap<>(CASH_MEMORY_CAPACITY, 0.75f, true);

    /**
     * Public Constructor
     * @param tilesPath : the path to the folder containing the disk cache
     * @param serverName : the name of the tile server
     */
    public TileManager(Path tilesPath, String serverName){
        this.tilesPath = tilesPath;
        this.serverName = serverName;
    }

    /**
     * Search for the image in the cash obtain it from the tile server
     * @param tileId : the identity of the OSM tile
     * @return the image associated to the given tileId
     * @throws IOException if there's an input/output error
     */
    public Image imageForTileAt(TileId tileId) throws IOException {

        if (cashMemory.containsKey(tileId)) {
            return cashMemory.get(tileId);
        }

        Path folderPath = tilesPath.resolve(getTileFolder(tileId));
        Path filePath = folderPath.resolve(getTileFile(tileId));

        if (Files.exists(filePath)) {
            return new Image(Files.newInputStream(filePath));
        }

        String urlString = "https://" + serverName + "/" + getTileFolder(tileId) + "/" + getTileFile(tileId);
        URL url = new URL(urlString);

        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Javions");
        try (InputStream inputStream = connection.getInputStream()){
            byte[] bytes = inputStream.readAllBytes();
            Image result = new Image(new ByteArrayInputStream(bytes));

            if (cashMemory.size() == CASH_MEMORY_CAPACITY)
                cashMemory.remove(cashMemory.keySet().iterator().next());
            cashMemory.put(tileId, result);

            Files.createDirectories(folderPath);
            Files.createFile(filePath);

            try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                outputStream.write(bytes);
            }
            return result;
        }
    }

    private String getTileFile(TileId tileId){
        return tileId.y() + ".png";
    }

    private String getTileFolder(TileId tileId){
        return tileId.zoom() + "/" + tileId.x();
    }
}
