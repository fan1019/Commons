package com.fmh.commons.mongo;


import com.fmh.commons.log.Loggers;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static java.util.Arrays.asList;

public class MongoClientBase {

	private MongoDatabase database;
	private MongoClient mongo;
	private ServerAddress address;
	private MongoClientOptions mongoClientOptions;

	private void init() {

	}

	protected boolean checkTable(String table){
		return !StringUtils.isEmpty(table);
	}

	private void setMongoClientOptions(final CodecRegistry... registries) {
		this.mongoClientOptions = MongoClientOptions.builder()
				.codecRegistry(CodecRegistries.fromRegistries(
						asList(registries)
				)).build();
	}

	public MongoClientBase(String host, int port, String db, final CodecRegistry... registries) {
		address = new ServerAddress(host, port);
		setMongoClientOptions(registries);
		mongo = new MongoClient(address, mongoClientOptions);
		database = mongo.getDatabase(db);
	}

	public MongoClientBase(String host, int port, String db) {
		address = new ServerAddress(host, port);
		setMongoClientOptions(MongoClient.getDefaultCodecRegistry());
		mongo = new MongoClient(address, mongoClientOptions);
		database = mongo.getDatabase(db);
	}

	public MongoClientBase(String host, String db){
		address = new ServerAddress(host,27017);
		setMongoClientOptions(MongoClient.getDefaultCodecRegistry());
		mongo = new MongoClient(address,mongoClientOptions);
		database = mongo.getDatabase(db);
	}

	public MongoClientBase(List<ServerAddress> serverAddressList, String db) {
		setMongoClientOptions(MongoClient.getDefaultCodecRegistry());
		mongo = new MongoClient(serverAddressList, mongoClientOptions);
		database = mongo.getDatabase(db);
	}

	public FindIterable<Document> find(final String table, final Document query, final Document order, final int skip, final int limit){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return database.getCollection(table).find(query).sort(order).skip(skip).limit(limit < 0 ? 0 : limit);
	}

	public FindIterable<Document> find(final String table, final Document query){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return find(table,query,null,0,0);
	}

	public <T> FindIterable<Document> find(final String table, final String queryfield, final T queryVlaue, final FilterType type){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		Bson query = null;
		switch (type){
			case eq:
				query = eq(queryfield,queryVlaue);
				break;
			case ne:
				query = ne(queryfield,queryVlaue);
				break;
			case gt:
				query = gt(queryfield,queryVlaue);
				break;
			case gte:
				query = gte(queryfield,queryVlaue);
				break;
			case lt:
				query = lt(queryfield,queryVlaue);
				break;
			case lte:
				query = lte(queryfield,queryVlaue);
				break;
		}
		return database.getCollection(table).find(query);
	}

	public Document findOne(final String table, Document query){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return find(table,query).first();
	}

	public Document get(String table, Object id){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return database.getCollection(table).find(eq("_id",id)).first();
	}


}
