package gcm.lab2;

/** A system user with a username and password. */
public class User {
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /** @return true if both the username and password match this user */
    public boolean matches(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}
