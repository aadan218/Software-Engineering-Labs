package gcm.lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Reads the input file (one "username,password" per line) and builds the list of valid users.
 * Invalid lines (wrong field count or empty fields) are skipped.
 * Adapt {@link #parse(String)} if the Lab 1 file format differs.
 */
public class UserLoader {

    public static ArrayList<User> load(String resourceName) throws IOException {
        ArrayList<User> users = new ArrayList<>();
        try (InputStream in = UserLoader.class.getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IOException("Input file not found: " + resourceName);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parse(line);
                if (user != null) {
                    users.add(user);
                }
            }
        }
        return users;
    }

    /** @return the user described by the line, or null if the line is not valid */
    static User parse(String line) {
        String[] parts = line.split(",");
        if (parts.length != 2) {
            return null;
        }
        String name = parts[0].trim();
        String pass = parts[1].trim();
        if (name.isEmpty() || pass.isEmpty()) {
            return null;
        }
        return new User(name, pass);
    }
}
