package com.fmh.commons.mongo.codec;


import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodecUtil {

	public static <T> List<T> readList(final BsonReader reader, final DecoderContext decoderContext, final BsonTypeCodecMap bsonTypeCodecMap) {
		reader.readStartArray();
		List<T> list = new ArrayList<>();
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			list.add(readValue(reader, decoderContext, bsonTypeCodecMap));
		}
		reader.readEndArray();
		return list;
	}

	public static <V> V readValue(final BsonReader reader, final DecoderContext decoderContext, final BsonTypeCodecMap bsonTypeCodecMap) {
		BsonType type = reader.getCurrentBsonType();
		if (type == BsonType.NULL) {
			reader.readNull();
			return null;
		} else if (type == BsonType.ARRAY) {
			return (V) readList(reader, decoderContext, bsonTypeCodecMap);
		} else {
			return (V) bsonTypeCodecMap.get(type).decode(reader, decoderContext);
		}
	}

	public static <T> Map<String, T> readMap(final BsonReader reader, final DecoderContext decoderContext, final BsonTypeCodecMap bsonTypeCodecMap) {
		reader.readStartDocument();
		Map<String,T> map = new HashMap<>();
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT){
			String fieldName = reader.readName();
			map.put(fieldName,readValue(reader,decoderContext,bsonTypeCodecMap));
		}
		reader.readEndDocument();
		return map;
	}

	public void writeValue(final BsonWriter writer, final EncoderContext encoderContext, final Object value, final CodecRegistry registry) {
		if (value == null) {
			writer.writeNull();
		} else if (value instanceof Iterable) {
			writeIterable(writer, (Iterable<Object>) value, encoderContext.getChildContext(),registry);
		} else if (value instanceof Map) {
			writeMap(writer, (Map<String, Object>) value, encoderContext.getChildContext(),registry);
		} else {
			Codec codec = registry.get(value.getClass());
			encoderContext.encodeWithChildContext(codec, writer, value);
		}
	}

	public void writeIterable(final BsonWriter writer, final Iterable<Object> list, final EncoderContext encoderContext, final CodecRegistry registry) {
		writer.writeStartArray();
		for (final Object value : list) {
			writeValue(writer, encoderContext, value,registry);
		}
		writer.writeEndArray();
	}

	public void writeMap(final BsonWriter writer, final Map<String, Object> map, final EncoderContext encoderContext, final CodecRegistry registry) {
		writer.writeStartDocument();
		for (final Map.Entry<String, Object> entry : map.entrySet()) {
			writer.writeName(entry.getKey());
			writeValue(writer, encoderContext, entry.getValue(),registry);
		}
		writer.writeEndDocument();
	}
}
