package io.oneinch.sdk.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlexibleTagsDeserializer extends JsonDeserializer<List<Object>> {
    
    @Override
    public List<Object> deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {
        
        JsonNode node = p.getCodec().readTree(p);
        List<Object> tags = new ArrayList<>();
        
        if (node.isArray()) {
            ObjectMapper mapper = new ObjectMapper();
            
            for (JsonNode tagNode : node) {
                if (tagNode.isTextual()) {
                    // Handle List<String> format
                    tags.add(tagNode.asText());
                } else if (tagNode.isObject()) {
                    // Handle List<TagDto> format
                    try {
                        TagDto tagDto = mapper.treeToValue(tagNode, TagDto.class);
                        tags.add(tagDto);
                    } catch (Exception e) {
                        // If TagDto deserialization fails, fall back to string
                        tags.add(tagNode.toString());
                    }
                }
            }
        }
        
        return tags;
    }
}