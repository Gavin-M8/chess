package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.ResponseObject.*;
import server.Server;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private String authToken;

    @BeforeAll
    public static void init() throws ResponseException, URISyntaxException {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        facade.clear();
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    public void registerSuccess() {
        try {
            RegisterResponse response = facade.register("jimbo", "pass", "mail");
            Assertions.assertNotNull(response.authToken());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    @Order(2)
    public void registerFail() {
        try {
            RegisterResponse response = facade.register("jimbo", "pass", "mail");
        } catch (Exception e) {
            Assertions.assertEquals("Error: User already registered", e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void loginSuccess() {
        try {
            LoginResponse response = facade.login("jimbo", "pass");
            Assertions.assertNotNull(response.authToken());
            authToken = response.authToken();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void loginFail() {
        try {
            LoginResponse response = facade.login("jimbo", "beans");
        } catch (Exception e) {
            Assertions.assertEquals("Error: Password incorrect", e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void logoutSuccess() {
        try {
            LoginResponse response = facade.login("jimbo", "pass");
            facade.logout(response.authToken());
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(6)
    public void logoutFail() {
        try {
            facade.logout("garbage");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    @Order(7)
    public void listGamesSuccess() {
        try{
            LoginResponse loginResponse = facade.login("jimbo", "pass");
            ListGamesResponse listGamesResponse = facade.listGames(loginResponse.authToken());
            Assertions.assertNotEquals(null, listGamesResponse.games());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(8)
    public void listGamesFail() {
        try{
            LoginResponse loginResponse = facade.login("jimbo", "pass");
            ListGamesResponse listGamesResponse = facade.listGames("garbage");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Error: Unauthorized", e.getMessage());
        }
    }

    @Test
    @Order(9)
    public void createGameSuccess() {
        try{
            LoginResponse loginResponse = facade.login("jimbo", "pass");
            CreateGameResponse createGameResponse = facade.createGame(loginResponse.authToken(), "silly");
            Assertions.assertNotEquals(null, createGameResponse.gameID());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(10)
    public void createGameFail() {
        try{
            LoginResponse loginResponse = facade.login("jimbo", "pass");
            CreateGameResponse createGameResponse = facade.createGame("garbage", "silly garbage");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Error: Unauthorized", e.getMessage());
        }
    }

    @Test
    @Order(11)
    public void joinGameSuccess() {
        try{
            LoginResponse loginResponse = facade.login("jimbo", "pass");
            CreateGameResponse createGameResponse = facade.createGame(loginResponse.authToken(), "testing");
            JoinGameResponse joinGameResponse = facade.joinGame(loginResponse.authToken(), ChessGame.TeamColor.WHITE, createGameResponse.gameID());
            Assertions.assertEquals(null, joinGameResponse.message());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(12)
    public void joinGameFail() {
        try{
            LoginResponse loginResponse = facade.login("jimbo", "pass");
            CreateGameResponse createGameResponse = facade.createGame(loginResponse.authToken(), "test");
            JoinGameResponse joinGameResponse = facade.joinGame("garbage", ChessGame.TeamColor.WHITE, createGameResponse.gameID());
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Error: Unauthorized", e.getMessage());
        }
    }

    @Test
    @Order(13)
    public void clearSuccess() {
        try {
            LoginResponse loginResponse = facade.login("jimbo", "pass");
            ListGamesResponse listGamesResponse = facade.listGames(loginResponse.authToken());
            Assertions.assertNotEquals(null, listGamesResponse.games());
            facade.clear();
            RegisterResponse registerResponse = facade.register("jimbo", "pass", "mail");
            ListGamesResponse listGamesResponse2 = facade.listGames(registerResponse.authToken());
            Assertions.assertEquals(new ArrayList<>(), listGamesResponse2.games());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    @Order(14)
    public void clearFail() {
        try {
            LoginResponse loginResponse = facade.login("jimbo", "pass");
            CreateGameResponse createGameResponse = facade.createGame(loginResponse.authToken(), "silly test");
            ListGamesResponse listGamesResponse = facade.listGames(loginResponse.authToken());
            Assertions.assertNotEquals(null, listGamesResponse.games());
            facade.clear();
            RegisterResponse registerResponse = facade.register("jimbo", "pass", "mail");
            ListGamesResponse listGamesResponse2 = facade.listGames(registerResponse.authToken());
            ArrayList<String> games = new ArrayList<>();
            games.add("silly test");
            Assertions.assertNotEquals(games, listGamesResponse2.games());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}
