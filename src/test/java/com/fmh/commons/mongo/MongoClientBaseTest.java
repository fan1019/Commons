package com.fmh.commons.mongo;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MongoClientBaseTest {
	private static MongoClientBase client = new MongoClientBase("127.0.0.1",27017,"test");

	@Test
	public void test1(){
		Document doc = new Document();
		doc.put("_id",1);
		doc.put("name","minghui");
		client.insert("test",doc);
	}
    @Test
	public void test2(){
		List<Document> list = new ArrayList<>();
		for (int i=2; i<=5; i++){
			Document doc = new Document();
			doc.put("_id",i);
			doc.put("name","minghui");
			list.add(doc);
		}
		client.insert("test",list);
	}

	@Test
	public void test3(){
		System.out.println(client.count("test"));
	}

	@Test
	public void test4(){
		Document query = new Document();
		query.put("name","minghui");
		System.out.println(client.count("test",query));
	}

	@Test
	public void test5(){
		System.out.println(client.count("test",new Document("name","minghui"),2,2));
	}

	@Test
	public void test6(){
		for (MongoCursor<Document> it = client.find("test", new Document("name", "minghui")); it.hasNext(); ) {
			Document doc = it.next();
			System.out.println(doc);
		}
	}

	@Test
	public void test7(){
		for (MongoCursor<Document> it = client.find("test", new Document("name", "minghui"),new Document("_id",-1),1,3); it.hasNext(); ) {
			Document doc = it.next();
			System.out.println(doc);
		}
	}

	@Test
	public void test8(){
		System.out.println(client.findOne("test",new Document("name","minghui")));
	}

	@Test
	public void test9(){
		for (MongoCursor<Document> it = client.find("test","name","minghui",FilterType.eq); it.hasNext(); ) {
			Document doc = it.next();
			System.out.println(doc);
		}
	}

	@Test
	public void test10(){
		System.out.println(client.get("test",2));
	}

	@Test
	public void test11(){
		Iterator<Object> ids = client.ids("test",new Document("name","minghui")).iterator();
		while (ids.hasNext()){
			System.out.println(ids.next());
		}
	}

	@Test
	public void test12(){
		Iterator<Object> ids = client.ids("test",new Document("name","minghui"),new Document("_id",-1),2,2).iterator();
		while (ids.hasNext()){
			System.out.println(ids.next());
		}
	}
}

