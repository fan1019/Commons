package com.fmh.commons.mongo;

import com.fmh.commons.mongo.codec.CodecUtil;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.bson.assertions.Assertions.notNull;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class MongoClientBaseTest {
	private static MongoClientBase client = new MongoClientBase("127.0.0.1", 27017, "test");

	public static class User {
		private int id;
		private String userName;
		private int age;
		private List<String> used_name;

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public void setUsed_name(List<String> used_name) {
			this.used_name = used_name;
		}

		public int getAge() {
			return age;
		}

		public List<String> getUsed_name() {
			return used_name;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserName() {
			return userName;
		}
	}

	public static class UserCodec implements Codec<User> {
		private static final CodecRegistry DEFAULT_REGISTRY = fromProviders(asList(new ValueCodecProvider(),
				new BsonValueCodecProvider(),
				new DocumentCodecProvider()));
		private final BsonTypeCodecMap bsonTypeCodecMap = new BsonTypeCodecMap(notNull("bsonTypeClassMap", new BsonTypeClassMap()), DEFAULT_REGISTRY);

		@Override
		public User decode(BsonReader reader, DecoderContext decoderContext) {
			User user = new User();
			reader.readStartDocument();
			user.setAge(reader.readInt32("_id"));
			user.setUserName(reader.readString("userName"));
			user.setAge(reader.readInt32("age"));
			user.setUsed_name(CodecUtil.readList(reader, decoderContext, bsonTypeCodecMap));
			reader.readEndDocument();
			return user;
		}

		@Override
		public void encode(BsonWriter writer, User value, EncoderContext encoderContext) {
			writer.writeStartDocument();
			writer.writeInt32("_id", value.getId());
			writer.writeString("userName", value.getUserName());
			writer.writeInt32("age", value.getAge());
			writer.writeName("used_name");
			CodecUtil.writeIterable(writer, value.getUsed_name(), encoderContext, DEFAULT_REGISTRY);
			writer.writeEndDocument();
		}

		@Override
		public Class<User> getEncoderClass() {
			return User.class;
		}
	}

	public static class UserCodecRegistry implements CodecRegistry {

		@Override
		public <T> Codec<T> get(Class<T> clazz) {
			if (clazz == User.class) {
				return (Codec<T>) new UserCodec();
			}
			return null;
		}
	}


	@Test
	public void test1() {
		Document doc = new Document();
		doc.put("_id", 10);
		doc.put("name", "minghui");
		client.insert("test", doc);
	}

	@Test
	public void test2() {
		List<Document> list = new ArrayList<>();
		for (int i = 2; i <= 5; i++) {
			Document doc = new Document();
			doc.put("_id", i);
			doc.put("name", "minghui");
			list.add(doc);
		}
		client.insert("test", list);
	}

	@Test
	public void test3() {
		System.out.println(client.count("test"));
	}

	@Test
	public void test4() {
		Document query = new Document();
		query.put("name", "minghui");
		System.out.println(client.count("test", query));
	}

	@Test
	public void test5() {
		System.out.println(client.count("test", new Document("name", "minghui"), 2, 2));
	}

	@Test
	public void test6() {
		for (MongoCursor<Document> it = client.find("test", new Document("name", "minghui")); it.hasNext(); ) {
			Document doc = it.next();
			System.out.println(doc);
		}
	}

	@Test
	public void test7() {
		for (MongoCursor<Document> it = client.find("test", new Document("name", "minghui"), new Document("_id", -1), 1, 3); it.hasNext(); ) {
			Document doc = it.next();
			System.out.println(doc);
		}
	}

	@Test
	public void test8() {
		System.out.println(client.findOne("test", new Document("name", "minghui")));
	}

	@Test
	public void test9() {
		for (MongoCursor<Document> it = client.find("test", "name", "minghui", FilterType.eq); it.hasNext(); ) {
			Document doc = it.next();
			System.out.println(doc);
		}
	}

	@Test
	public void test10() {
		System.out.println(client.get("test", 3));
	}

	@Test
	public void test11() {
		for (Object o : client.ids("test", new Document("name", "minghui"))) {
			System.out.println(o);
		}
	}

	@Test
	public void test12() {
		for (Object o : client.ids("test", new Document("name", "minghui"), new Document("_id", -1), 2, 2)) {
			System.out.println(o);
		}
	}

	@Test
	public void test13() {
		System.out.println(client.delete("test", 2));
	}

	@Test
	public void test14() {
		Document doc = new Document("name", "minghui2");
		System.out.println(client.updateOne("test", new Document("_id", 3), doc));
	}

	@Test
	public void test15() {
		Document doc = new Document("sex", 3);
		System.out.println(client.updateMany("test", new Document("name", "minghui"), doc));
	}

	@Test
	public void test16() {
		Document doc = new Document();
		doc.put("name", "minghui6");
		System.out.println(client.replaceOne("test", new Document("_id", 3), doc));
	}

	@Test
	public void test17() {
		Document doc = new Document();
		doc.put("name", "minghui6");
		System.out.println(client.replaceOne("test", new Document("_id", 6), doc, true));
	}

	@Test
	public void test18() {
		System.out.println(client.createIndex("test", "name"));
	}

	@Test
	public void test19() {
		client.dropIndex("test", "name");
	}

	@Test
	public void test20() {
		client.createIndexes("test", Arrays.asList("name", "sex"));
	}

	@Test
	public void test21() {
		client.renameCollection("test2", "test");
	}

	@Test
	public void test22() {
		client.renameCollection("test1", "test2", true);
	}

	@Test
	public void test23() {
		List<Document> pipeline = new ArrayList<>();
		Document match = new Document("$match", new Document("name", "minghui"));
		Document group = new Document("$group", new Document("_id", "$name").append("total", new Document("$sum", "$count")));
		Document sort = new Document("$sort", new Document("total", -1));
		pipeline.add(match);
		pipeline.add(group);
		pipeline.add(sort);
		for (Document doc : client.aggregate("test", pipeline)) {
			System.out.println(doc);
		}
	}

	@Test
	public void test24() {
		for (Document document : client.listCollections()) {
			System.out.println(document);
		}
	}

	@Test
	public void test25() {
		System.out.println(client.getDatabase());
	}

	@Test
	public void test26() {
		System.out.println(client.getHost());
	}

	@Test
	public void test27() {
		System.out.println(client.getPort());
	}

	@Test
	public void test28() {
		MongoClientBase mongo = new MongoClientBase("127.0.0.1", 27017, "test", MongoClient.getDefaultCodecRegistry(),new UserCodecRegistry());
		User user = new User();
		user.setId(1);
		user.setAge(20);
		user.setUserName("fanminghui");
		user.setUsed_name(Arrays.asList("fanminghui1","fanminghui2"));
		mongo.insert("user",user,User.class);
	}

	@Test
	public void test29(){
		MongoClientBase mongo = new MongoClientBase("127.0.0.1", 27017, "test", MongoClient.getDefaultCodecRegistry(),new UserCodecRegistry());
		User user = mongo.findOne("user",new Document("_id",1),User.class);
		System.out.println(user.getUsed_name());
	}

	@Test
	public void test30(){
		MongoClientBase mongo = new MongoClientBase("127.0.0.1", 27017, "test", MongoClient.getDefaultCodecRegistry(),new UserCodecRegistry());
		User user = mongo.get("user",1,User.class);
		System.out.println(user.getUsed_name());
	}

	@Test
	public void test31(){
		MongoClientBase mongo = new MongoClientBase("127.0.0.1", 27017, "test", MongoClient.getDefaultCodecRegistry(),new UserCodecRegistry());
		System.out.println(mongo.findOne("user",new Document("_id",1),new Document("userName",1)));
	}
}

