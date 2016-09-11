package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.wordpicture;

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
"dep",
"depextra",
"deppos",
"freq",
"head",
"headpos",
"mi",
"rel",
"source"
})

public class Relation {
    @JsonProperty("dep")
    private String dep;
    @JsonProperty("depextra")
    private String depextra;
    @JsonProperty("deppos")
    private String deppos;
    @JsonProperty("freq")
    private Integer freq;
    @JsonProperty("head")
    private String head;
    @JsonProperty("headpos")
    private String headpos;
    @JsonProperty("mi")
    private Double mi;
    @JsonProperty("rel")
    private String rel;
    @JsonProperty("source")
    private List<String> source = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The dep
     */
    @JsonProperty("dep")
    public String getDep() {
	return dep;
    }

    /**
     *
     * @param dep
     * The dep
     */
    @JsonProperty("dep")
    public void setDep(String dep) {
	this.dep = dep;
    }

    /**
     *
     * @return
     * The depextra
     */
    @JsonProperty("depextra")
    public String getDepextra() {
	return depextra;
    }

    /**
     *
     * @param depextra
     * The depextra
     */
    @JsonProperty("depextra")
    public void setDepextra(String depextra) {
	this.depextra = depextra;
    }

    /**
     *
     * @return
     * The deppos
     */
    @JsonProperty("deppos")
    public String getDeppos() {
	return deppos;
    }

    /**
     *
     * @param deppos The deppos
     */
    @JsonProperty("deppos")
    public void setDeppos(String deppos) {
	this.deppos = deppos;
    }

    /**
     *
     * @return
     * The freq
     */
    @JsonProperty("freq")
    public Integer getFreq() {
	return freq;
    }

    /**
     *
     * @param freq The frequency
     */
    @JsonProperty("freq")
    public void setFreq(Integer freq) {
	this.freq = freq;
    }

    /**
     *
     * @return
     * The head
     */
    @JsonProperty("head")
    public String getHead() {
	return head;
    }

    /**
     *
     * @param head
     * The head
     */
    @JsonProperty("head")
    public void setHead(String head) {
	this.head = head;
    }

    /**
     *
     * @return
     * The headpos
     */
    @JsonProperty("headpos")
    public String getHeadpos() {
	return headpos;
    }

    /**
     *
     * @param headpos
     * The headpos
     */
    @JsonProperty("headpos")
    public void setHeadpos(String headpos) {
	this.headpos = headpos;
    }

    /**
     *
     * @return
     * The mi
     */
    @JsonProperty("mi")
    public Double getMi() {
	return mi;
    }

    /**
     *
     * @param mi
     * The mi
     */
    @JsonProperty("mi")
    public void setMi(Double mi) {
	this.mi = mi;
    }

    /**
     *
     * @return
     * The rel
     */
    @JsonProperty("rel")
    public String getRel() {
	return rel;
    }

    /**
     *
     * @param rel
     * The rel
     */
    @JsonProperty("rel")
    public void setRel(String rel) {
	this.rel = rel;
    }

    /**
     *
     * @return
     * The source corpora positions
     */
    @JsonProperty("source")
    public List<String> getSource() {
	return source;
    }

    /**
     *
     * @param source
     * The source
     */
    @JsonProperty("source")
    public void setSource(List<String> source) {
	this.source = source;
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
