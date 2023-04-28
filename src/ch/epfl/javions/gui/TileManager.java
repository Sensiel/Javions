package ch.epfl.javions.gui;


import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {
    record TileId(int zoom, Double x, Double y) {
        public static boolean isValid(int zoom, Double x, Double y) {
            boolean zoomIsValid = zoom >= 6 && zoom <= 19;
            boolean xIsValid = x >= 0 && x < Math.scalb(1, zoom); //
            boolean yIsValid = x >= 0 && x < Math.scalb(1, zoom); //
            return zoomIsValid && xIsValid && yIsValid;
        }
    }

    private Path tilePath;
    private String serverName;
    private LinkedHashMap<TileManager.TileId, Image> cashMemory = new LinkedHashMap<>(100, 0.75f, true);

    TileManager(Path tilePath, String serverName) throws IOException {
        this.tilePath = tilePath;
        this.serverName = serverName;
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        if (cashMemory.containsKey(tileId)) {
            return cashMemory.get(tileId);
        } else if (Files.exists(tilePath)) {
            return new Image(Files.newInputStream(tilePath));
        } else {
            String url = "https://tile.openstreetmap.org/" + tileId.zoom() + "/" + tileId.x() + "/" + tileId.y() + ".png";
            URL url1 = new URL(url);
            URLConnection connection = url1.openConnection();
            connection.setRequestProperty("User-Agent", "Javions");
            Image result;
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            result = new Image(new ByteArrayInputStream(bytes));
            if (cashMemory.size() == 100)
                cashMemory.remove(cashMemory.keySet().iterator().next());
            cashMemory.put(tileId, result);
            OutputStream outputStream = new FileOutputStream(tilePath.toFile());
            Files.createDirectories(tilePath);
            outputStream.write(bytes);
            return result;

            //faut rjt try with ressources + utiliser methode resolve pour creer mon path (d'apres assistante)
        }
    }
}
