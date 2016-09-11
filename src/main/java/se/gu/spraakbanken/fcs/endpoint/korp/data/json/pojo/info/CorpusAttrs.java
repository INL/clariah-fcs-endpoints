package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info;

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
	"a",
	"p",
	"s"
})
public class CorpusAttrs {

    @JsonProperty("a")
    private List<Object> a = new ArrayList<Object>();
    @JsonProperty("p")
    private List<String> p = new ArrayList<String>();
    @JsonProperty("s")
    private List<String> s = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The a
     */
    @JsonProperty("a")
    public List<Object> getA() {
	return a;
    }

    /**
     *
     * @param a
     * The a
     */
    @JsonProperty("a")
    public void setA(List<Object> a) {
	this.a = a;
    }

    /**
     *
     * @return
     * The p
     */
    @JsonProperty("p")
    public List<String> getP() {
	return p;
    }

    /**
     *
     * @param p
     * The p
     */
    @JsonProperty("p")
    public void setP(List<String> p) {
	this.p = p;
    }

    /**
     *
     * @return
     * The s
     */
    @JsonProperty("s")
    public List<String> getS() {
	return s;
    }

    /**
     *
     * @param s
     * The s
     */
    @JsonProperty("s")
    public void setS(List<String> s) {
	this.s = s;
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
