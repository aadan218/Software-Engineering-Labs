# Software-Engineering-Labs

מעבדה 2– הנדסת תוכנה

מגישים:
עדן עווד 324302678
איתי שור 216187922

# Software Engineering Labs

## Lab 2 - JavaFX Login (branch `lab2-javafx`)

Authors: Eden Awad (324302678), Itay Shor (216187922)

A JavaFX login window for the GCM system, built on top of the Lab 1 code.

### What the program does
1. Reads the input file (`src/main/resources/users.txt`) and builds an
   `ArrayList<User>` containing only the valid users.
2. Opens a login window (username, password, login button).
3. If a matching user exists, the window is replaced with a Welcome screen.
   Otherwise an error message is shown inside the window (no pop-up).
4. Closing a window with X ends the program.

### Project structure
- `User`, `UsersApp` - Lab 1 classes (only a `package` line was added)
- `UserLoader` - reads the input file, keeps valid users only
- `App` - JavaFX entry point
- `controller/LoginController`, `controller/WelcomeController` - screen logic
- `login.fxml`, `welcome.fxml` - screen layouts

### How to run
Requirements: JDK 17+ and Maven.

    mvn javafx:run

To use a different input file:

    mvn javafx:run -Djavafx.args="C:\path\to\input.txt"

### Input file format
One user per line: `email password` (separated by whitespace).
Invalid lines are reported on the console and skipped.

### Lab 1
Lab 1 is on the `main` branch.
