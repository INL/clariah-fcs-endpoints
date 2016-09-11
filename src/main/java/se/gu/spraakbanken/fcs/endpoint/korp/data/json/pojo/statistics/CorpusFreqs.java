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
"relative",
"sums"
})

public class CorpusFreqs {

    @JsonProperty("absolute")
    private Map<String, Integer> absolute = new HashMap<String, Integer>();
    @JsonProperty("relative")
    private Map<String, Double> relative = new HashMap<String, Double>();
    @JsonProperty("sums")
    private Sums sums;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return The attributes and their absolute frequencies for this corpus
     */
    @JsonProperty("absolute")
    public Map getAbsolute() {
	return absolute;
    }

    /**
     *
     * @param absolute The absolute frequencies for this corpus
     */
    @JsonProperty("absolute")
    public void setAbsolute(Map absolute) {
	this.absolute = absolute;
    }

    /**
     *
     * @param attr The attribute
     * @return The absolute frequency for the attribute
     */
    public Integer getAbsolute(final String attr) {
	return absolute.get(attr);
    }

    /**
     *
     * @param attr The attribute
     * @param freq The absolute frequecy of the attribute
     */
    public void addAbsolute(final String attr, final Integer freq) {
	absolute.put(attr, freq);
    }

    /**
     *
     * @return The relative
     */
    @JsonProperty("relative")
    public Map getRelative() {
	return relative;
    }

    /**
     *
     * @param relative The attributes and their relative frequencies 
     */
    @JsonProperty("relative")
    public void setRelative(Map relative) {
	this.relative = relative;
    }

    /**
     *
     * @param attr The attribute
     * @return The relative frequency of the attribute
     */
    public Double getRelative(final String attr) {
	return relative.get(attr);
    }

    /**
     *
     * @param attr The attribute
     * @param freq The absolute frequecy of the attribute
     */
    public void addRelative(final String attr, final Double freq) {
	relative.put(attr, freq);
    }

    /**
     *
     * @return The sums
     */
    @JsonProperty("sums")
    public Sums getSums() {
	return sums;
    }

    /**
     *
     * @param sums The sums instance
     * The sums
     */
    @JsonProperty("sums")
    public void setSums(Sums sums) {
	this.sums = sums;
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
