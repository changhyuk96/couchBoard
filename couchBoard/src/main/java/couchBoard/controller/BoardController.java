package couchBoard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.json.JsonObject;

import couchBoard.service.CouchbaseBoardService;

@RestController
public class BoardController {
	
	@Autowired
	CouchbaseBoardService boardService;
	
	@RequestMapping(value="/boards", method=RequestMethod.GET)
	public Object getBoardList() {
		
		return boardService.getBoardList();
	}
	
	@RequestMapping(value="/boards", method=RequestMethod.POST)
	public Object writeBoard(@RequestBody String boardJson) {
		JsonObject json = null;
		try {
			json = JsonObject.fromJson(boardJson);
		}
		catch(Exception e) {
			return e.toString();
		}
		
		return boardService.writeBoard(json);
	}
	
	@RequestMapping(value="/boards/{id}", method=RequestMethod.POST)
	public Object getBoard(@PathVariable String id) {
		
		return boardService.getBoard(id);
	}
	
	@RequestMapping(value="/boards/{id}", method=RequestMethod.PUT)
	public Object updateBoard(@PathVariable String id, @RequestBody String boardJson) {
		JsonObject json = null;
		try {
			json = JsonObject.fromJson(boardJson);
		}
		catch(Exception e) {
			return e.toString();
		}
		
		return boardService.updateBoard(id, json);
	}
	
	@RequestMapping(value="/boards/{id}/{boardNum}", method=RequestMethod.PUT)
	public void updateHits(@PathVariable String id,@PathVariable String boardNum) {
		
		if(!(boolean)boardService.updateHits(id))
			System.out.println("Hits Error.");
		
		
		return;
	}
	
	@RequestMapping(value="/boards/{id}/{boardNum}", method=RequestMethod.DELETE)
	public Object deleteBoard(@PathVariable String id,@PathVariable String boardNum,
								@RequestBody String username) {
		
		String user = null;
		try {
			user = JsonObject.fromJson(username).getString("username");
		}
		catch(Exception e) {
			return e.toString();
		}
		System.out.println(user);
		return boardService.deleteBoard(id,boardNum,user);
	}
}
