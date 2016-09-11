package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.statistics;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"corpora",
	"count",
	"time",
	"total"
})
public class CountCQP {

    @JsonProperty("corpora")
    private Map<String, CorpusFreqs> corpora = new HashMap<String, CorpusFreqs>();
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("time")
    private Double time;
    @JsonProperty("total")
    private CorpusFreqs total;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The corpora
     */
    @JsonProperty("corpora")
    public Map getCorpora() {
	return corpora;
    }

    /**
     *
     * @param corpora
     * The corpora
     */
    @JsonProperty("corpora")
    public void setCorpora(final Map corpora) {
	this.corpora = corpora;
    }

    /**
     *
     * @return
     * The count
     */
    @JsonProperty("count")
    public Integer getCount() {
	return count;
    }

    /**
     *
     * @param count The total count
     */
    @JsonProperty("count")
    public void setCount(Integer count) {
	this.count = count;
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
     * @param time The time
     */
    @JsonProperty("time")
    public void setTime(Double time) {
	this.time = time;
    }

    /**
     *
     * @return
     * The total
     */
    @JsonProperty("total")
    public CorpusFreqs getTotal() {
	return total;
    }

    /**
     *
     * @param total The total count of values
     */
    @JsonProperty("total")
    public void setTotal(CorpusFreqs total) {
	this.total = total;
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
