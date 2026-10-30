package gcm.lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Reads the Lab 1 input file ("name password" per line) and builds the list of valid users.
 * Invalid lines are reported on stderr and skipped, as in Lab 1.
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
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.trim().split("\\s+");
                try {
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Please enter a valid Email as username");
                    }
                    users.add(new User(parts[0], parts[1]));
                } catch (IllegalArgumentException e) {
                    System.err.println(line.trim() + " -> " + e.getMessage());
                }
            }
        }
        return users;
    }
}
