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
	"lemma",
	"msd",
	"word"
})

public class Token {

    @JsonProperty("lemma")
    private String lemma;
    @JsonProperty("msd")
    private String msd;
    @JsonProperty("word")
    private String word;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The lemma
     */
    @JsonProperty("lemma")
    public String getLemma() {
	return lemma;
    }

    /**
     *
     * @param lemma
     * The lemma
     */
    @JsonProperty("lemma")
    public void setLemma(String lemma) {
	this.lemma = lemma;
    }

    /**
     *
     * @return
     * The msd
     */
    @JsonProperty("msd")
    public String getMsd() {
	return msd;
    }

    /**
     *
     * @param msd
     * The msd
     */
    @JsonProperty("msd")
    public void setMsd(String msd) {
	this.msd = msd;
    }

    /**
     *
     * @return
     * The word
     */
    @JsonProperty("word")
    public String getWord() {
	return word;
    }

    /**
     *
     * @param word
     * The word
     */
    @JsonProperty("word")
    public void setWord(String word) {
	this.word = word;
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
