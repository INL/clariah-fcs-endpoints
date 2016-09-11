package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
"attrs",
"info"
})

public class Corpus {

    @JsonProperty("attrs")
    private CorpusAttrs attrs;
    @JsonProperty("info")
    private CorpusMetaInfo info;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The attrs
     */
    @JsonProperty("attrs")
    public CorpusAttrs getAttrs() {
	return attrs;
    }

    /**
     *
     * @param attrs
     * The attrs
     */
    @JsonProperty("attrs")
    public void setAttrs(CorpusAttrs attrs) {
	this.attrs = attrs;
    }

    /**
     *
     * @return
     * The info
     */
    @JsonProperty("info")
    public CorpusMetaInfo getMetaInfo() {
	return info;
    }

    /**
     *
     * @param info
     * The info
     */
    @JsonProperty("info")
    public void setMetaInfo(CorpusMetaInfo info) {
	this.info = info;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
	this.additionalProperties.put(name, value);
    }
}
