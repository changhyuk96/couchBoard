package couchBoard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;

@Service
public class CouchbaseUserService {
	
	@Autowired
	Collection userCollection;
	@Autowired
	PasswordEncoder passEncoder;
	
	public Object signupUser(JsonObject userJson){
		
		String result;
		try {
			// username, password : 필수 입력조건
			
			if(!userJson.containsKey("username") && userJson.containsKey("password")) 
				return "Please put your username, password in your json.";
			
			userJson.put("password", passEncoder.encode(userJson.getString("password")));
			userJson.put("boards", JsonObject.create());
			userCollection.insert(userJson.getString("username"), userJson.removeKey("username"));
			result = "Signup has been completed.";
		}
		catch(DocumentExistsException e) {
			return "username already exists.";
		}
		catch(ClassCastException e) { 
			return "The password must not exist just numbers.";
		}
		catch(Exception e) {
			return e.toString();
		}
		
		return result.toString();
	}
	
	public Object getUser(String username, String inputPassword) {
		
		Object passCheck = passwordCheck(username, inputPassword);
		
		if(passCheck.toString().contains("true")) {
			GetResult result = userCollection.get(username);
			return result.contentAsObject().toString();
		}
		return passCheck;
	}
	
	public Object loginUser(String username, String inputPassword) {
		
		Object passCheck = passwordCheck(username, inputPassword);
		
		if(passCheck.toString().contains("true")) {
			return "welcome "+username;
			// user will get session
		}
		
		return passCheck;
	}
	
	public Object updateUser(String username, String inputPassword, JsonObject userJson) {
		
		Object passCheck = passwordCheck(username, inputPassword);
		
		if(passCheck.toString().contains("true")) {
			if(!userJson.containsKey("password"))
				userJson.put("password", passEncoder.encode(inputPassword));
			userCollection.replace(username, userJson);
			return "Update has been completed.";
		}
		
		return passCheck;
	}

	public Object deleteUser(String username, String inputPassword) {
		
		Object passCheck = passwordCheck(username, inputPassword);
		
		if(passCheck.toString().contains("true")) {
			userCollection.remove(username);
			return "your account has been deleted.";
		}
		
		return passCheck;
		
	}
	
	
	public Object passwordCheck(String username, String inputPassword) {
		
		String existingPassword;

		try {
			existingPassword = userCollection.get(username).contentAsObject().getString("password");
		}catch(DocumentNotFoundException e) {
			return "This user does not exists.";
		}
		
		if(!passEncoder.matches(inputPassword, existingPassword)) {
			return "Password does not match."; 
		}
		return true;
	}
}
