package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public class main {
    public static void main(String[] args) throws IOException {
        test a = new test();
        a.a();
    }

}
class test{
    public void a() throws IOException{
        String d = getClass().getResource("/aircraft.zip").getFile();
        try (ZipFile z = new ZipFile(d);
             InputStream s = z.getInputStream(z.getEntry("14.csv"));
             Reader r = new InputStreamReader(s, UTF_8);
             BufferedReader b = new BufferedReader(r)) {
            String l = "";
            while ((l = b.readLine()) != null)
                System.out.println(l);
        }
        }
    }

