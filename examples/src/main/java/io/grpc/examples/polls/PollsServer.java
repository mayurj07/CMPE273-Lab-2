//This is poll server
package io.grpc.examples.polls;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.util.logging.Logger;
import java.util.*;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class PollsServer {
  private static final Logger logger = Logger.getLogger(PollsServer.class.getName());

  /* The port on which the server should run */
  private int port = 50051;
  private ServerImpl server;

  private void start() throws Exception {
    server = NettyServerBuilder.forPort(port)
        .addService(PollServiceGrpc.bindService(new PollsImpl()))
        .build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        PollsServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }


  public static void main(String[] args) throws Exception 
  {
    final PollsServer server = new PollsServer();
    server.start();
  }

  private class PollsImpl implements PollServiceGrpc.PollService 
  {

    @Override
    public void createPoll(io.grpc.examples.polls.PollRequest request,
				io.grpc.stub.StreamObserver<io.grpc.examples.polls.PollResponse> responseObserver)
    {
      System.out.println("Created Moderator with id ="+request.getModeratorId());
	    PollResponse response = PollResponse.newBuilder()
			.setId("1ADC2FZ")    //hard coded the ID
			.build();

      responseObserver.onValue(response);        
	    responseObserver.onCompleted();
    }
  }
}
