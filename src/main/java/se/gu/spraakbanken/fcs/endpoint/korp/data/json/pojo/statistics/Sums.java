package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.statistics;

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
"absolute",
"relative"
})

public class Sums {

    @JsonProperty("absolute")
    private Integer absolute;
    @JsonProperty("relative")
    private Double relative;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return The absolute total frequency
     */
    @JsonProperty("absolute")
    public Integer getAbsolute() {
	return absolute;
    }

    /**
     *
     * @param absolute The absolute total frequency
     */
    @JsonProperty("absolute")
    public void setAbsolute(Integer absolute) {
	this.absolute = absolute;
    }

    /**
     *
     * @return The relative total frequency
     */
    @JsonProperty("relative")
    public Double getRelative() {
	return relative;
    }

    /**
     *
     * @param relative The relative total frequency
     */
    @JsonProperty("relative")
    public void setRelative(Double relative) {
	this.relative = relative;
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
