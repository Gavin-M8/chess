package client;

import chess.ChessGame;
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

        // register user /user POST
        public RegisterResponse register(String username, String password, String email) throws ResponseException {
            String path = "/user";
            var registerRequest = new RegisterRequest(username, password, email);
            var body = makeRequestBody(registerRequest);
            var request = buildRequest("POST", path, body, null);
            var response = sendRequest(request);
            return handleResponse(response, RegisterResponse.class);
        }

        // login user /session POST
        public LoginResponse login(String username, String password) throws ResponseException {
            String path = "/session";
            var loginRequest = new LoginRequest(username, password);
            var body = makeRequestBody(loginRequest);
            var request = buildRequest("POST", path, body, null);
            var response = sendRequest(request);
            return handleResponse(response, LoginResponse.class);
        }

        // logout user /session DELETE
        public LogoutResponse logout(String authToken) throws ResponseException {
            String path = "/session";
            var request = buildRequest("DELETE", path, null, authToken);
            var response = sendRequest(request);
            return handleResponse(response, LogoutResponse.class);
        }

        // list games /game GET
        public ListGamesResponse listGames(String authToken) throws ResponseException {
            String path = "/game";
            var request = buildRequest("GET", path, null, authToken);
            var response = sendRequest(request);
            return handleResponse(response, ListGamesResponse.class);
        }

        // create game /game POST
        public CreateGameResponse createGame(String authToken, String gameName) throws ResponseException {
            String path = "/game";
            var createGameRequest = new CreateGameRequest(gameName);
            var body = makeRequestBody(createGameRequest);
            var request = buildRequest("POST", path, body, authToken);
            var response = sendRequest(request);
            return handleResponse(response, CreateGameResponse.class);
        }

        // join game /game PUT
        public JoinGameResponse joinGame(String authToken, ChessGame.TeamColor playerColor, Integer gameID) throws ResponseException {
            String path = "/game";
            var joinGameRequest = new JoinGameRequest(playerColor, gameID);
            var body = makeRequestBody(joinGameRequest);
            var request = buildRequest("PUT", path, body, authToken);
            var response = sendRequest(request);
            return handleResponse(response, JoinGameResponse.class);
        }

        // clear /db DELETE
        public ClearResponse clear() throws ResponseException {
            String path = "/db";
            var request = buildRequest("DELETE", path, null, null);
            var response = sendRequest(request);
            return handleResponse(response, ClearResponse.class);
        }

    private HttpRequest buildRequest(String method, String path, BodyPublisher body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, body);
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
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
