package com.fmh.commons.mongo;


import com.fmh.commons.log.Loggers;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.Tag;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import javax.swing.*;
import java.util.Iterator;
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

	protected boolean checkTable(String table) {
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

	public MongoClientBase(String host, String db) {
		address = new ServerAddress(host, 27017);
		setMongoClientOptions(MongoClient.getDefaultCodecRegistry());
		mongo = new MongoClient(address, mongoClientOptions);
		database = mongo.getDatabase(db);
	}

	public MongoClientBase(List<ServerAddress> serverAddressList, String db) {
		setMongoClientOptions(MongoClient.getDefaultCodecRegistry());
		mongo = new MongoClient(serverAddressList, mongoClientOptions);
		database = mongo.getDatabase(db);
	}

	public MongoCursor<Document> find(final String table, final Document query, final Document order, final int skip, final int limit) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return database.getCollection(table).find(query).sort(order).skip(skip).limit(limit < 0 ? 0 : limit).iterator();
	}

	public MongoCursor<Document> find(final String table, final Document query) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return find(table, query, null, 0, 0);
	}

	public <T> MongoCursor<Document> find(final String table, final String queryfield, final T queryVlaue, final FilterType type) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		Bson query = null;
		switch (type) {
			case eq:
				query = eq(queryfield, queryVlaue);
				break;
			case ne:
				query = ne(queryfield, queryVlaue);
				break;
			case gt:
				query = gt(queryfield, queryVlaue);
				break;
			case gte:
				query = gte(queryfield, queryVlaue);
				break;
			case lt:
				query = lt(queryfield, queryVlaue);
				break;
			case lte:
				query = lte(queryfield, queryVlaue);
				break;
		}
		return database.getCollection(table).find(query).iterator();
	}

	public Iterable<Object> ids(final String table, final Document query, final Document order, final int skip, final int limit) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		MongoCursor<Document> cursor = find(table, query, order, skip, limit);
		return cursor == null ? null : () -> new Iterator<Object>() {
			@Override
			public boolean hasNext() {
				if (!cursor.hasNext()) {
					cursor.close();
					return false;
				} else {
					return true;
				}
			}

			@Override
			public Object next() {
				Document doc = cursor.next();
				return doc == null ? null : doc.get("_id");
			}
		};
	}

	public Iterable<Object> ids(final String table, final Document query){
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return ids(table,query,null,0,0);
	}

	public Document findOne(final String table, final Document query) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return database.getCollection(table).find(query).first();
	}

	public Document get(final String table, final Object id) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return database.getCollection(table).find(eq("_id", id)).first();
	}

	public Long count(final String table) {
		return count(table, null);
	}

	public Long count(final String table, final Document query) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return 0L;
		}
		return database.getCollection(table).count(query);
	}

	public Long count(final String table, final Document query, final int skip, final int limit) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return 0L;
		}
		CountOptions options = new CountOptions();
		options.skip(skip);
		options.limit(limit < 0 ? 0 : limit);
		return database.getCollection(table).count(query, options);
	}

	public Document delete(final String table, final Object id) {
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return database.getCollection(table).findOneAndDelete(eq("_id", id));
	}

	public boolean deleteOne(final String table, final Document query){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return false;
		}
		Long count = database.getCollection(table).deleteOne(query).getDeletedCount();
		return count == 1L;
	}

	public Long deleteMany(final String table, final Document query){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return 0L;
		}
		return database.getCollection(table).deleteMany(query).getDeletedCount();
	}

	public void insert(final String table, final Document document){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return;
		}
		database.getCollection(table).insertOne(document);
	}

	public void insert(final String table, final List<? extends Document> documents){
		insert(table,documents,false);
	}

	public void insert(final String table, final List<? extends Document> documents, final boolean order){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return;
		}
		InsertManyOptions options = new InsertManyOptions();
		options.ordered(order);
		database.getCollection(table).insertMany(documents,options);
	}

	public Document updateOne(final String table, final Document query, final Document update){
		if (!checkTable(table)) {
			Loggers.STDOUT.error("table error!");
			return null;
		}
		Document up = new Document("$set",update);
		return database.getCollection(table).findOneAndUpdate(query,up);
	}

	public UpdateResult updateMany(final String table, final Document query, final Document update){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		Document up = new Document("$set",update);
		return database.getCollection(table).updateMany(query,up);
	}

	public Document replaceOne(final String table, final Document query, final Document replace){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return database.getCollection(table).findOneAndReplace(query,replace);
	}

	public UpdateResult replaceOne(final String table, final Document query, final Document replace, Boolean upsert){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		UpdateOptions options = new UpdateOptions();
		options.upsert(upsert);
		return database.getCollection(table).replaceOne(query,replace,options);
	}

	public void dropTable(final String table){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return ;
		}
		database.getCollection(table).drop();
	}

	public void createIndexes(final String table, final List<String> keys){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return ;
		}
		for (String key : keys){

		}
	}

	public String createIndex(final String table, final String key){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return null;
		}
		return database.getCollection(table).createIndex(new Document(key,1));
	}

	public void dropIndex(final String table, final String key){
		if (!checkTable(table)){
			Loggers.STDOUT.error("table error!");
			return;
		}
		database.getCollection(table).dropIndex(new Document(key,1));
	}

}
