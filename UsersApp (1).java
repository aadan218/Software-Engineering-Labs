import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class UsersApp {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        Scanner reader = new Scanner(System.in);

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            if (line.trim().isEmpty()) continue;
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

        Collections.sort(users, (u1, u2) -> u1.getName().compareTo(u2.getName()));
        for (User u : users) {
            System.out.print(u + "\n");
        }
    }
}
