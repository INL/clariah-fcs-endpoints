package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	"corpus",
	"match",
	"tokens"
})
public class Kwic {

    @JsonProperty("corpus")
    private String corpus;
    @JsonProperty("match")
    private Match match;
    @JsonProperty("tokens")
    private List<Token> tokens = new ArrayList<Token>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The corpus
     */
    @JsonProperty("corpus")
    public String getCorpus() {
	return corpus;
    }

    /**
     *
     * @param corpus
     * The corpus
     */
    @JsonProperty("corpus")
    public void setCorpus(String corpus) {
	this.corpus = corpus;
    }

    /**
     *
     * @return
     * The match
     */
    @JsonProperty("match")
    public Match getMatch() {
	return match;
    }

    /**
     *
     * @param match
     * The match
     */
    @JsonProperty("match")
    public void setMatch(Match match) {
	this.match = match;
    }

    /**
     *
     * @return
     * The tokens
     */
    @JsonProperty("tokens")
    public List<Token> getTokens() {
	return tokens;
    }

    /**
     *
     * @param tokens
     * The tokens
     */
    @JsonProperty("tokens")
    public void setTokens(List<Token> tokens) {
	this.tokens = tokens;
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
