package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"corpora",
	"time",
	"total_sentences",
	"total_size"
})
public class CorporaInfo {

    @JsonProperty("corpora")
    private Map<String, Corpus> corpora = new HashMap<String, Corpus>();
    @JsonProperty("time")
    private Double time;
    @JsonProperty("total_sentences")
    private Integer totalSentences;
    @JsonProperty("total_size")
    private Integer totalSize;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @param corpusId The corpusId
     * @return Corpus The Corpus with ID corpusId
     */
    public Corpus getCorpus(final String corpusId) {
	return corpora.get(corpusId);
    }

    /**
     *
     * @param corpusId The corpusId
     * @param corpus The Corpus for corpus corpusId
     */
    public void setCorpus(final String corpusId, final Corpus corpus) {
	corpora.put(corpusId, corpus);
    }

    /**
     *
     * @return
     * The corpora
     */
    @JsonProperty("corpora")
    public Map<String, Corpus> getCorpora() {
	return corpora;
    }

    /**
     *
     * @param corpora The corpora
     */
    @JsonProperty("corpora")
    public void setCorpora(final Map<String, Corpus> corpora) {
	this.corpora = corpora;
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
     * The totalSentences
     */
    @JsonProperty("total_sentences")
    public Integer getTotalSentences() {
	return totalSentences;
    }

    /**
     *
     * @param totalSentences The total_sentences
     */
    @JsonProperty("total_sentences")
    public void setTotalSentences(Integer totalSentences) {
	this.totalSentences = totalSentences;
    }

    /**
     *
     * @return The totalSize
     */
    @JsonProperty("total_size")
    public Integer getTotalSize() {
	return totalSize;
    }

    /**
     *
     * @param totalSize The total_size
     */
    @JsonProperty("total_size")
    public void setTotalSize(Integer totalSize) {
	this.totalSize = totalSize;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
	this.additionalProperties.put(name, value);
    }

    /**
     *
     * @param corpora The List&lt;String&gt; corpora values.
     *
     * @return a CorporaInfo instance for all corpora
     */
    public static CorporaInfo getCorporaInfo(final List<String> corpora) {
        ObjectMapper mapper = new ObjectMapper();

	CorporaInfo ci = null;
	final String wsString ="https://spraakbanken.gu.se/ws/korp?";
	final String queryString = "command=info&corpus=";
	//"ROMI,PAROLE";
	final String corporaValues = getCorpusParameterValues(corpora);
        try {
	    URL korp = new URL(wsString + queryString + corporaValues);

            ci = mapper.reader(CorporaInfo.class).readValue(korp.openStream());
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	return ci;
    }

    /**
     *
     * @param values The List&lt;String&gt; values.
     *
     * @return a comma separated String
     */
    public static String getCorpusParameterValues(Collection<String> values) {
	StringBuffer buf = new StringBuffer();
	boolean first = true;
	for (String key : values) {
	    if (first) { 
		first = false;
	    } else { 
		buf.append(",");
	    }
	    buf.append(key);
	}
	return buf.toString();
    }

}
