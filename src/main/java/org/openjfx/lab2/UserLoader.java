package org.openjfx.lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Lab 2, requirement 2.1: reads the input file and builds an ArrayList of User
 * containing ONLY the valid users.
 *
 * The parsing is the same as in lab 1 (UsersApp): each line is "email password"
 * separated by whitespace, and the User constructor performs all the validation
 * (it throws IllegalArgumentException for invalid details). Invalid lines are
 * reported on System.err and skipped.
 */
public class UserLoader {

    /** Resource name of the default input file (src/main/resources/users.txt). */
    public static final String DEFAULT_RESOURCE = "/users.txt";

    /**
     * @param in reader of the input file (closed by the caller)
     * @return list of valid users, in file order
     */
    public static ArrayList<User> load(Reader in) throws IOException {
        ArrayList<User> users = new ArrayList<>();
        BufferedReader reader = new BufferedReader(in);
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            try {
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Please enter a valid Email as username");
                }
                users.add(new User(parts[0], parts[1]));   // validates
            } catch (IllegalArgumentException e) {
                System.err.println(line + " -> " + e.getMessage());
            }
        }
        return users;
    }
}
