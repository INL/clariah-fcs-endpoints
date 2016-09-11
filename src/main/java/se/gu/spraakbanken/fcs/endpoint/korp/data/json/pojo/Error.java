package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo;

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
	"ERROR",
	"time"
})

public class Error {
    @JsonProperty("ERROR")
    private se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.ERROR ERROR;
    @JsonProperty("time")
    private Double time;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The ERROR
     */
    @JsonProperty("ERROR")
    public se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.ERROR getERROR() {
	return ERROR;
    }

    /**
     *
     * @param ERROR
     * The ERROR
     */
    @JsonProperty("ERROR")
    public void setERROR(se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.ERROR ERROR) {
	this.ERROR = ERROR;
    }

    /**
     *
     * @return
     * The time
     */
    @JsonProperty("time")
    public Double getTime() {
	return time;
    }

    /**
     *
     * @param time
     * The time
     */
    @JsonProperty("time")
    public void setTime(Double time) {
	this.time = time;
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
