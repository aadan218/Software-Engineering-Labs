import java.util.regex.Pattern;

/** A system user with a validated e-mail username and password. */
public class User {
    private static final int MAX_EMAIL_LEN = 50;
    private static final int MIN_PASS_LEN = 8;
    private static final int MAX_PASS_LEN = 12;
    private static final String SYMBOLS = "!@#$%^&*()_+-=";

    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9][A-Za-z0-9.-]*\\.[A-Za-z]{2,}$");

    private final String name;
    private final String password;

    public User(String name, String password) {
        validateName(name);
        validatePassword(password);
        this.name = name;
        this.password = password;
    }

    private static void validateName(String name) {
        if (name == null || !EMAIL.matcher(name).matches()) {
            // too-long addresses get their own message when otherwise well-formed
            if (name != null && name.length() > MAX_EMAIL_LEN) {
                throw new IllegalArgumentException("Username is too long, try something shorter");
            }
            throw new IllegalArgumentException("Please enter a valid Email as username");
        }
        if (name.length() > MAX_EMAIL_LEN) {
            throw new IllegalArgumentException("Username is too long, try something shorter");
        }
    }

    private static void validatePassword(String p) {
        if (p == null || p.length() < MIN_PASS_LEN) {
            throw new IllegalArgumentException("Your password is too short, add more characters");
        }
        if (p.length() > MAX_PASS_LEN) {
            throw new IllegalArgumentException("Your password is too long, try a shorter one");
        }
        boolean letter = false, digit = false, symbol = false;
        for (char c : p.toCharArray()) {
            if (Character.isLetter(c) && c < 128) letter = true;
            else if (c >= '0' && c <= '9') digit = true;
            else if (SYMBOLS.indexOf(c) >= 0) symbol = true;
            else throw new IllegalArgumentException("Please enter a valid password");
        }
        if (!(letter && digit && symbol)) {
            throw new IllegalArgumentException("Please enter a valid password");
        }
    }

    public String getName() { return name; }

    public String getPassword() { return password; }

    @Override
    public String toString() { return name + " " + password; }
}
