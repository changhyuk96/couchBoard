package couchBoard.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.kv.Upsert;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;

import couchBoard.data.BoardDTO;

@Service
public class CouchbaseBoardService {
	
	@Autowired
	private Collection boardCollection;
	@Autowired
	private Collection userCollection;
	@Autowired
	private Cluster cluster;
	
	// curl -u Administrator:pass123 -H "Content-type:application/json" http://localhost:8080/boards -X POST -d {\"username\":\"test123\",\"title\":\"title123123\",\"content\":\"sodyddlqslek\"}
	public Object writeBoard(JsonObject boardJson) {
		
		if(!boardJson.containsKey("title") && boardJson.containsKey("content") && boardJson.containsKey("username"))
			return "All values sholud not be null.";
		
		String id= "board::"+UUID.randomUUID();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String writeTime = dateFormat.format(date).toString();
		
		boardJson.put("id", id);
		boardJson.put("writeTime", writeTime);
		
		int num = 1;
		try {
			QueryResult result = cluster.query("select max(boardNum) as num from `myApp`._default.board");
			num = result.rowsAsObject().get(0).getInt("num")+1;
		}catch(Exception e) {
		}
		
		boardJson.put("boardNum", num);
		boardJson.put("hits", 0);
		// 원래는 트랜잭션화 해야함. > SDK랑 Couchbase Server 버전오류로 인해 호환이 안됨.
		
		try {
			userCollection.mutateIn(boardJson.getString("username"),
					Collections.singletonList(Upsert.upsert("boards."+Integer.toString(num),id)));
			boardCollection.insert(id, boardJson);
		}catch(Exception e) {
			return e.toString();
		}
		
		
//		Transactions t = Transactions.create(cluster);
//		try {
//			t.run((ctx) ->{
//				
//				ctx.insert(boardCollection, id, boardJson);
//				
//				ctx.commit();
//				
//			});
//
//		}catch (TransactionCommitAmbiguous e) {
//		    System.err.println("Transaction possibly committed");
//
//		    for (LogDefer err : e.result().log().logs()) {
//		        System.err.println(err.toString());
//		    }
//		    return "error";
//		} catch (TransactionFailed e) {
//		    System.err.println("Transaction did not reach commit point");
//
//		    for (LogDefer err : e.result().log().logs()) {
//		        System.err.println(err.toString());
//		    }
//		    return "error";
//		}
		

		return "The writting has been written.";

	}
	public Object getBoardList() {
		QueryResult result = cluster.query("select b.* from `myApp`._default.board b order by boardNum desc");
		ArrayList<BoardDTO> list = (ArrayList<BoardDTO>) result.rowsAs(BoardDTO.class);
		
		return list;
	}
	
	public Object getBoard(String id) {
		GetResult result=null;
		try {
			result = boardCollection.get(id);
		}catch(DocumentNotFoundException e) {
			return "The post does not exists.";
		}
		
		return result.contentAs(BoardDTO.class);
	}
	
	public Object deleteBoard(String id, String boardNum, String username) {
		try {
			
			// 트랜잭션화 해야함. > SDK랑 Couchbase Server 버전오류로 인해 호환이 안됨.
			userCollection.mutateIn(username, Collections.singletonList(MutateInSpec.remove("boards."+boardNum)));
			boardCollection.remove(id);
		}catch(DocumentNotFoundException e ) {
			return "The post does not exists.";
		}catch(Exception e) {
			return e.toString();
		}
		
		return "The post has been deleted.";
	}
	
	public Object updateBoard(String id, JsonObject json) {
		
		try {
			boardCollection.mutateIn(id, Collections.singletonList(MutateInSpec.replace("content", json.get("content"))));
			boardCollection.mutateIn(id, Collections.singletonList(MutateInSpec.replace("title", json.get("title"))));
		}catch(DocumentNotFoundException e) {
			return "The post does not exists.";
		}catch(Exception e) {
			return e.toString();
		}
		
		return "The post has been updated.";
	}
	
	public Object updateHits(String id) {
		try {
//			QueryResult result = cluster.query("update `myApp`._default.board set hits=hits+1 where id = $id"
//													,QueryOptions.queryOptions().parameters(JsonObject.create().put("id", id)));
//			GetResult result = boardCollection.get(id);
//			JsonObject json = result.contentAsObject();
//			json.put("hits", json.getInt("hits")+1);
//			boardCollection.replace(id, json);
			
			boardCollection.mutateIn(id, Collections.singletonList(MutateInSpec.increment("hits", 1)));
		}catch(DocumentNotFoundException e) {
			e.printStackTrace();
			return "The post does not exists.";
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	
}
