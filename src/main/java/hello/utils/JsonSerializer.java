package hello.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializer {
	
	public static <T> String getSerializedObject(T object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new RuntimeException("Error during object serialization: "+e);
		}
	}
	
	public static  HashMap<String,Object> maybeGetUnmarshalledObject(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			 
			return mapper.readValue(json, HashMap.class);
		} catch (IOException e) {
			throw new RuntimeException("Error during json parsing: "+e);
		}
	}
}
