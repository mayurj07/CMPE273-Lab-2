package io.grpc.examples.polls;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.net.URL;
import java.io.InputStream;
import java.io.*;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.lang.*;
/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class PollsClient {
  private static final Logger logger = Logger.getLogger(PollsClient.class.getName());

  private final ChannelImpl channel;
  private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

  public PollsClient(String host, int port) {
    channel =
        NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
            .build();
    blockingStub = PollServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
  }

  public void postPoll(PollRequest poll) {
    try {
      //logger.info("Poll created with details :");
      PollRequest request = poll;
      PollResponse response = blockingStub.createPoll(request);
      System.out.println("Created a new poll with id = " +  response.getId());         
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public static void main(String[] args) throws Exception 
  {
    PollsClient client = new PollsClient("localhost", 50051);
    JSONParser parser = new JSONParser();
      try 
      {            

          String moderatorId = "1";
          String question = "What type of smartphone do you have?";
          String started_at = "2015-03-18T13:00:00.000Z";
          String expired_at = "2015-03-19T13:00:00.000Z";
          String[] choice = new String[] { "Android", "iPhone" };
          List<String> choice1 = Arrays.asList(choice); 
    	    Iterable<String> choiceIterator = choice1;
    	    if(moderatorId == null)
    	    {
      		logger.info("Moderator ID not found..!!");
      		return;		
    	    } 

    	    if(question == null || started_at == null || expired_at == null || choice == null)
    	    {
    		    logger.info("Error in request body. Plz check request payload.");
    		    return;
          } 
    	
    	   PollRequest pollRequest= PollRequest.newBuilder()
    		.setModeratorId(moderatorId) 
        .setQuestion(question)
        .setStartedAt(started_at)
    		.setExpiredAt(expired_at)
    		.addAllChoice(choiceIterator)
    		.build();
           
    	    client.postPoll(pollRequest);
       
	   }catch (Exception e) {   e.printStackTrace();  } finally {  client.shutdown(); }
  }

}
