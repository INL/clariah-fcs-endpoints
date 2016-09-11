package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"corpus_hits",
	"corpus_order",
	"hits",
	"kwic",
	"querydata",
	"time"
})
public class Query {

    @JsonProperty("corpus_hits")
    private Map<String, Integer> corpusHits = new HashMap<String, Integer>();
    @JsonProperty("corpus_order")
    private List<String> corpusOrder = new ArrayList<String>();
    @JsonProperty("hits")
    private Integer hits;
    @JsonProperty("kwic")
    private List<Kwic> kwic = new ArrayList<Kwic>();
    @JsonProperty("querydata")
    private String querydata;
    @JsonProperty("time")
    private Double time;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @param corpusId The corpusId
     * @return The corpusHits for the corpus corpusId
     */
    @JsonProperty("corpusHits")
    public Integer getCorpusHits(final String corpusId) {
	return corpusHits.get(corpusId);
    }

    /**
     *
     * @param corpusId The corpusId
     * @param corpusHits The corpusHits for corpus corpusId
     */
    @JsonProperty("corpusHits")
    public void setCorpusHits(final String corpusId, final Integer corpusHits) {
	this.corpusHits.put(corpusId, corpusHits);
    }

    /**
     *
     * @return The corpusHits
     */
    @JsonProperty("corpus_hits")
    public Map getCorpusHits() {
	return corpusHits;
    }

    /**
     *
     * @param corpusHits The corpus_hits
     */
    @JsonProperty("corpus_hits")
    public void setCorpusHits(final Map corpusHits) {
	this.corpusHits = corpusHits;
    }

    /**
     *
     * @return The corpusOrder
     */
    @JsonProperty("corpus_order")
    public List<String> getCorpusOrder() {
	return corpusOrder;
    }

    /**
     *
     * @param corpusOrder The corpus_order
     */
    @JsonProperty("corpus_order")
    public void setCorpusOrder(List<String> corpusOrder) {
	this.corpusOrder = corpusOrder;
    }

    /**
     *
     * @return The hits
     */
    @JsonProperty("hits")
    public Integer getHits() {
	if (hits != null) {
	    return hits;
	}
	return new Integer("-1");
    }

    /**
     *
     * @param hits The hits
     */
    @JsonProperty("hits")
    public void setHits(Integer hits) {
	this.hits = hits;
    }

    /**
     *
     * @return The kwic
     */
    @JsonProperty("kwic")
    public List<Kwic> getKwic() {
	return kwic;
    }

    /**
     *
     * @param kwic The kwic
     */
    @JsonProperty("kwic")
    public void setKwic(List<Kwic> kwic) {
	this.kwic = kwic;
    }

    /**
     *
     * @return The querydata
     */
    @JsonProperty("querydata")
    public String getQuerydata() {
	return querydata;
    }

    /**
     *
     * @param querydata The querydata
     */
    @JsonProperty("querydata")
    public void setQuerydata(String querydata) {
	this.querydata = querydata;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
	this.additionalProperties.put(name, value);
    }
}
