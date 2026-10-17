package dataaccess;

public class AuthData {

    private final String authtoken;

    AuthData (String authtoken) {
        this.authtoken = authtoken;
    }

    public String getAuthtoken() {return authtoken;}
}
