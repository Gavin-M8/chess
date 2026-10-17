package dataaccess;

import java.util.Objects;

public class AuthData {

    private final String authtoken;

    AuthData (String authtoken) {
        this.authtoken = authtoken;
    }

    public String getAuthtoken() {return authtoken;}

    @Override
    public String toString() {
        return "AuthData{" +
                "authtoken='" + authtoken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthData authData = (AuthData) o;
        return Objects.equals(authtoken, authData.authtoken);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authtoken);
    }
}
