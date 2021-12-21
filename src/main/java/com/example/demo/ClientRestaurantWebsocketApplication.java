package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ClientEndpoint
public class ClientRestaurantWebsocketApplication {

  private static CountDownLatch latch;
  private Logger logger = Logger.getLogger(this.getClass().getName());

  @OnOpen
  public void onOpen(Session session) {
    System.out.println("Connected to server. Session id: " + session.getId() + "\n");
  }

  @OnMessage
  public void onMessage(String receivedText, Session session) {
    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
    try {
      System.out.println("Restaurant: " + receivedText);
      String readFromConsole = bufferRead.readLine();
      session.getBasicRemote().sendText(readFromConsole);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @OnClose
  public void onClose(Session session) {
    logger.info(String.format("Session %s closed.", session.getId()));
//    latch.countDown();
  }

  public static void main(String[] args) {
    latch = new CountDownLatch(1);

    ClientManager client = ClientManager.createClient();
    try {
      client.connectToServer(ClientRestaurantWebsocketApplication.class, new URI("ws://localhost:8025/websockets/restaurant"));
      latch.await();

    } catch (DeploymentException | URISyntaxException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
