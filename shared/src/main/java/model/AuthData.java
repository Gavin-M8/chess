package model;

import java.util.Objects;

public class AuthData {

    private String authToken;

    public AuthData(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {return authToken;}

    @Override
    public String toString() {
        return "AuthData{" +
                "authToken='" + authToken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthData authData = (AuthData) o;
        return Objects.equals(authToken, authData.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authToken);
    }
}
