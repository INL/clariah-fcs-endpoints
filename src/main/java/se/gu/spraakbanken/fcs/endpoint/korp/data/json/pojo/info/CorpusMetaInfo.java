package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info;

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
	"Charset",
	"FirstDate",
	"LastDate",
	"Saldo",
	"Sentences",
	"Size",
	"Updated"
})

public class CorpusMetaInfo {

    @JsonProperty("Charset")
    private String Charset;
    @JsonProperty("FirstDate")
    private String FirstDate;
    @JsonProperty("LastDate")
    private String LastDate;
    @JsonProperty("Saldo")
    private String Saldo;
    @JsonProperty("Sentences")
    private String Sentences;
    @JsonProperty("Size")
    private String Size;
    @JsonProperty("Updated")
    private String Updated;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The Charset
     */
    @JsonProperty("Charset")
    public String getCharset() {
	return Charset;
    }

    /**
     *
     * @param Charset
     * The Charset
     */
    @JsonProperty("Charset")
    public void setCharset(String Charset) {
	this.Charset = Charset;
    }

    /**
     *
     * @return
     * The FirstDate
     */
    @JsonProperty("FirstDate")
    public String getFirstDate() {
	return FirstDate;
    }

    /**
     *
     * @param FirstDate
     * The FirstDate
     */
    @JsonProperty("FirstDate")
    public void setFirstDate(String FirstDate) {
	this.FirstDate = FirstDate;
    }

    /**
     *
     * @return
     * The LastDate
     */
    @JsonProperty("LastDate")
    public String getLastDate() {
	return LastDate;
    }

    /**
     *
     * @param LastDate
     * The LastDate
     */
    @JsonProperty("LastDate")
    public void setLastDate(String LastDate) {
	this.LastDate = LastDate;
    }

    /**
     *
     * @return
     * The Saldo
     */
    @JsonProperty("Saldo")
    public String getSaldo() {
	return Saldo;
    }

    /**
     *
     * @param Saldo
     * The Saldo
     */
    @JsonProperty("Saldo")
    public void setSaldo(String Saldo) {
	this.Saldo = Saldo;
    }

    /**
     *
     * @return
     * The Sentences
     */
    @JsonProperty("Sentences")
    public String getSentences() {
	return Sentences;
    }

    /**
     *
     * @param Sentences
     * The Sentences
     */
    @JsonProperty("Sentences")
    public void setSentences(String Sentences) {
	this.Sentences = Sentences;
    }

    /**
     *
     * @return
     * The Size
     */
    @JsonProperty("Size")
    public String getSize() {
	return Size;
    }

    /**
     *
     * @param Size
     * The Size
     */
    @JsonProperty("Size")
    public void setSize(String Size) {
	this.Size = Size;
    }

    /**
     *
     * @return
     * The Updated
     */
    @JsonProperty("Updated")
    public String getUpdated() {
	return Updated;
    }

    /**
     *
     * @param Updated
     * The Updated
     */
    @JsonProperty("Updated")
    public void setUpdated(String Updated) {
	this.Updated = Updated;
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
