package couchBoard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.couchbase.client.java.json.JsonObject;

import couchBoard.service.CouchbaseUserService;

@RestController
public class UserController {
	
	@Autowired
	CouchbaseUserService userService;

	@RequestMapping(value="/", method=RequestMethod.GET)
	public Object hiTest() {
		
		ModelAndView mv = new ModelAndView("index");
		
		return mv;
	}
	
	//curl -u Administrator:pass123 -H "content-type:application/json" http://localhost:8080/users -X POST -d {\"username\":\"ckdgur123\",\"password\":\"ckd123\"}
	@RequestMapping(value="/users", method=RequestMethod.POST)
	public Object signUpUsers(@RequestBody String userJson) {
		
		JsonObject json=null;
		try {
			 json = JsonObject.fromJson(userJson);
		}catch(Exception e) {
			
			return e.toString();
		}
		return userService.signupUser(json);
	}
	
	@RequestMapping(value="/users/{username}/{inputPassword}", method=RequestMethod.GET)
	public Object getUser(@PathVariable("username") String username, @PathVariable("inputPassword") String inputPassword) {
		
		return userService.getUser(username, inputPassword);
	} 
	
	@RequestMapping(value="/users/{username}/{inputPassword}", method=RequestMethod.POST)
	public Object loginUser(@PathVariable("username") String username, @PathVariable("inputPassword") String inputPassword) {
		
		return userService.loginUser(username, inputPassword);
	}
	
	@RequestMapping(value="/users/{username}/{inputPassword}", method=RequestMethod.PUT)
	public Object updateUser(@PathVariable("username") String username, @PathVariable("inputPassword") String inputPassword,
							@RequestBody String userJson) {
		
		JsonObject json=null;
		try {
			 json = JsonObject.fromJson(userJson);
		}catch(Exception e) {
			return e.toString();
		}
		return userService.updateUser(username, inputPassword, json);
	}
	
	@RequestMapping(value="/users/{username}/{inputPassword}", method=RequestMethod.DELETE)
	public Object deleteUser(@PathVariable("username") String username, @PathVariable("inputPassword") String inputPassword) {
		
		return userService.deleteUser(username, inputPassword);
	}
	
}
