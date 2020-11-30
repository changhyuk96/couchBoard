package couchBoard.data;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;

@Service
public class CouchbaseInfo {
	
	private final String hostname="localhost";
	private final String username="Administrator";
	private final String password="pass123";
	private final String bucketName = "myApp";
	
	@Bean 
	public Cluster cluster() {
		return Cluster.connect(hostname, username, password);
	}
	
	@Bean 
	public Collection userCollection() {
		return cluster().bucket(bucketName).collection("users");
	}
	
	@Bean
	public Collection boardCollection() {
		return cluster().bucket(bucketName).collection("board");
	}
	

}
