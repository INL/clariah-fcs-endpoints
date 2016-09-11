package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.statistics;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
 	"lemgrams",
 	"time"
})
public class CountLemgrams {
    //@JsonUnwrapped
    @JsonIgnore
    private Map<String, Integer> lemgrams = new HashMap<String, Integer>();
    @JsonProperty("time")
    private Double time;

    /**
     *
     * @return The lemgrams
     */
    @JsonAnyGetter
    public Map<String, Integer> getLemgrams() {
	return lemgrams;
    }

    /**
     *
     * @param lemgramId The lemgram ID
     * @return The lemgram count for lemgramId
     */

    public Integer getLemgram(final String lemgramId) {
	return lemgrams.get(lemgramId);
    }

    /**
     *
     * @param lemgramId The lemgramId
     * @param lemgramCount The lemgramCount for lemgramId
     */
    @JsonAnySetter
    public void setLemgram(final String lemgramId, final Integer lemgramCount) {
	lemgrams.put(lemgramId, lemgramCount);
    }

    /**
     *
     * @return The time
     */
    @JsonProperty("time")
    public Double getTime() {
	return time;
    }

    /**
     *
     * @param time The time
     */
    @JsonProperty("time")
    public void setTime(Double time) {
	this.time = time;
    }
}
