package client;

import com.google.gson.Gson;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import server.RequestObject.*;
import server.ResponseObject.*;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

        // register user
        public RegisterResponse register(String username, String password, String email) throws ResponseException {
            String path = "/user";
            var registerRequest = new RegisterRequest(username, password, email);
            var body = makeRequestBody(registerRequest);
            var request = buildRequest("POST", path, body);
            var response = sendRequest(request);
            return handleResponse(response, RegisterResponse.class);
        }

        // login user

        // logout user

        // list games

        // create game

        // join game

        // clear

    private HttpRequest buildRequest(String method, String path, BodyPublisher body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, body);
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        }
        else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }
            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "Other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status == 200;
    }
}
