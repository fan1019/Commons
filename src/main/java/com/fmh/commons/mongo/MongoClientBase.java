package com.fmh.commons.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.List;

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
}
