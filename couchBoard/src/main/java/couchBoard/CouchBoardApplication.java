package couchBoard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;

@SpringBootApplication
public class CouchBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouchBoardApplication.class, args);
	}
}
