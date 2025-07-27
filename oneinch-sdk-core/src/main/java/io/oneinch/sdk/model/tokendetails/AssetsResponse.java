package io.oneinch.sdk.model.tokendetails;
import io.oneinch.sdk.model.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AssetsResponse {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("website")
    private String website;
    
    @JsonProperty("sourceCode")
    private String sourceCode;
    
    @JsonProperty("whitePaper")
    private String whitePaper;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("shortDescription")
    private String shortDescription;
    
    @JsonProperty("research")
    private String research;
    
    @JsonProperty("explorer")
    private String explorer;
    
    @JsonProperty("social_links")
    private SocialLink socialLinks;
}