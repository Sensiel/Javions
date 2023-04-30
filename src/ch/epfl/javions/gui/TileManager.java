package ch.epfl.javions.gui;


import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {
    public record TileId(int zoom, int x, int y) {
        public static boolean isValid(int zoom, int x, int y) {
            boolean zoomIsValid = zoom >= 6 && zoom <= 19;
            boolean xIsValid = x >= 0 && x < 1 << zoom;
            boolean yIsValid = y >= 0 && y < 1 << zoom;
            return zoomIsValid && xIsValid && yIsValid;
        }
    }

    private final Path tilesPath;
    private final String serverName;
    private final LinkedHashMap<TileManager.TileId, Image> cashMemory = new LinkedHashMap<>(100, 0.75f, true);

    public TileManager(Path tilesPath, String serverName){
        this.tilesPath = tilesPath;
        this.serverName = serverName;
    }

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

            if (cashMemory.size() == 100)
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
