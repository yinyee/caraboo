package com.github.yinyee.caraboo;

import javax.ws.rs.PathParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class Question {
	
	public int qid;
	public int answer;
	public long lastAnswered;
	
	public Question(int qid, int answer) {
		this.qid = qid;
		this.answer = answer;
		this.lastAnswered = System.currentTimeMillis();
	}
	
	public Question(int qid, int answer, long timestamp) {
		this.qid = qid;
		this.answer = answer;
		this.lastAnswered = timestamp;
	}
	
	/**
	 * Returns the answer that corresponds to the specified user id and timestamp
	 * @param userid
	 * @param timestamp
	 * @return
	 */
	@GET
	@Path("/users/{userid}/questions/{timestamp}")
	@Produces(MediaType.APPLICATION_JSON)
    public Question getItem(@PathParam("userid") String userid, @PathParam("timestamp") String timestamp) {
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		key.put("uid", new AttributeValue(userid));
		key.put("timestamp", new AttributeValue().withN(timestamp));
		
		Map<String, AttributeValue> value = Application.getDbClient().getItem(new GetItemRequest().withTableName("caraboo-questions").withKey(key)).getItem();
		this.lastAnswered = Long.parseLong(timestamp);
		this.answer = Integer.parseInt(value.get("answer").getN());
		this.qid = Integer.parseInt(value.get("qid").getN());
		
        return this;
    }
	
	/**
	 * Returns all the answers available for the specified user id
	 * @param userid
	 * @return
	 */
	@GET
	@Path("/users/{userid}/questions")
	@Produces(MediaType.APPLICATION_JSON)
    public List<Question> getItem(@PathParam("userid") String userid) {
		
//		ArrayList<Question> questions = new ArrayList<Question>();
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		key.put("uid", new AttributeValue(userid));
		
		// Create the Mapper and send a request
		DynamoDBMapper mapper = new DynamoDBMapper(Application.getDbClient());
		
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(userid));
		
        DynamoDBQueryExpression<Question> queryExpression = new DynamoDBQueryExpression<Question>()
                .withKeyConditionExpression("uid = :val1")
                .withExpressionAttributeValues(eav);
        
        List<Question> questions = mapper.query(Question.class, queryExpression);
        
		// Loop over all results
//		Map<String, AttributeValue> value;
//		int qid, answer;
//		long timestamp;
//		
//		for (Question q : latestQuestions) {
//			
//		}
//		
//		
//		
//		
//		qid = Integer.parseInt(value.get("qid").getN());
//		answer = Integer.parseInt(value.get("answer").getN());
//		timestamp = Integer.parseInt(value.get("timestamp").getN());
//		
//		q = new Question(qid, answer, timestamp); 
//		questions.add(q);
		
        return questions;
    }
	
	@POST
	@Path("/users/{userid}/questions")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putItem(@PathParam("userid") String userid, Question question) {
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		
		key.put("uid", new AttributeValue(userid));
		key.put("timestamp", new AttributeValue().withN(String.valueOf(question.lastAnswered)));
		key.put("qid", new AttributeValue().withN(String.valueOf(question.qid)));
		key.put("answer", new AttributeValue().withN(String.valueOf(question.answer)));
		
		Application.getDbClient().putItem(new PutItemRequest().withTableName("caraboo-questions").withItem(key));
		
		return Response.noContent().build();
	}
	
}