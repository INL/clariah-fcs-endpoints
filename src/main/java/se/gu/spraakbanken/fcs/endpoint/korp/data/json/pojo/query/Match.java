package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query;

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
	"end",
	"position",
	"start"
})

public class Match {

    @JsonProperty("end")
    private Integer end;
    @JsonProperty("position")
    private Integer position;
    @JsonProperty("start")
    private Integer start;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The end
     */
    @JsonProperty("end")
    public Integer getEnd() {
	return end;
    }

    /**
     *
     * @param end
     * The end
     */
    @JsonProperty("end")
    public void setEnd(Integer end) {
	this.end = end;
    }

    /**
     *
     * @return
     * The position
     */
    @JsonProperty("position")
    public Integer getPosition() {
	return position;
    }

    /**
     *
     * @param position
     * The position
     */
    @JsonProperty("position")
    public void setPosition(Integer position) {
	this.position = position;
    }

    /**
     *
     * @return
     * The start
     */
    @JsonProperty("start")
    public Integer getStart() {
	return start;
    }

    /**
     *
     * @param start
     * The start
     */
    @JsonProperty("start")
    public void setStart(Integer start) {
	this.start = start;
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
