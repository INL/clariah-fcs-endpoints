package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	"cqp-version",
	"protected_corpora",
	"time"
})

public class ServiceInfo {
    
    @JsonProperty("corpora")
    private List<String> corpora = new ArrayList<String>();
    @JsonProperty("cqp-version")
    private String cqpVersion;
    @JsonProperty("protected_corpora")
    private List<String> protectedCorpora = new ArrayList<String>();
    @JsonProperty("time")
    private Double time;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private static final List<String> MODERN_CORPORA = Collections.unmodifiableList(Arrays.asList("FSBBLOGGVUXNA","MAGMAKOLUMNER","INFORMATIONSTIDNINGAR","LAGTEXTER","MYNDIGHET","PROPOSITIONER","BARNLITTERATUR","FSBESSAISTIK","FSBSAKPROSA","FSBSKONLIT1960-1999","FSBSKONLIT2000TAL","UNGDOMSLITTERATUR","FNB1999","FNB2000","HBL1991","HBL1998","HBL1999","HBL20122013","HBL2014","JAKOBSTADSTIDNING1999","JAKOBSTADSTIDNING2000","PARGASKUNGORELSER2011","PARGASKUNGORELSER2012","SYDOSTERBOTTEN2010","SYDOSTERBOTTEN2011","SYDOSTERBOTTEN2012","SYDOSTERBOTTEN2013","SYDOSTERBOTTEN2014","VASABLADET1991","VASABLADET2012","VASABLADET2013","VASABLADET2014","ABOUNDERRATTELSER2012","ABOUNDERRATTELSER2013","OSTERBOTTENSTIDNING2011","OSTERBOTTENSTIDNING2012","OSTERBOTTENSTIDNING2013","OSTERBOTTENSTIDNING2014","BORGABLADET","VASTRANYLAND","AT2012","OSTRANYLAND","ASTRA1960-1979","ASTRANOVA","BULLEN","FANBARAREN","FINSKTIDSKRIFT","FORUMFEOT","HANKEITEN","HANKEN","JFT","KALLAN","MEDDELANDEN","NYAARGUS","STUDENTBLADET","SVENSKBYGDEN","ROMI","ROMII","ROM99","STORSUC","BLOGGMIX1998","BLOGGMIX1999","BLOGGMIX2000","BLOGGMIX2001","BLOGGMIX2002","BLOGGMIX2003","BLOGGMIX2004","BLOGGMIX2005","BLOGGMIX2006","BLOGGMIX2007","BLOGGMIX2008","BLOGGMIX2009","BLOGGMIX2010","BLOGGMIX2011","BLOGGMIX2012","BLOGGMIX2013","BLOGGMIX2014","BLOGGMIX2015","BLOGGMIXODAT","TWITTER","GP1994","GP2001","GP2002","GP2003","GP2004","GP2005","GP2006","GP2007","GP2008","GP2009","GP2010","GP2011","GP2012","GP2013","GP2D","PRESS65","PRESS76","PRESS95","PRESS96","PRESS97","PRESS98","WEBBNYHETER2001","WEBBNYHETER2002","WEBBNYHETER2003","WEBBNYHETER2004","WEBBNYHETER2005","WEBBNYHETER2006","WEBBNYHETER2007","WEBBNYHETER2008","WEBBNYHETER2009","WEBBNYHETER2010","WEBBNYHETER2011","WEBBNYHETER2012","WEBBNYHETER2013","ATTASIDOR","DN1987","ORDAT","FOF","SFS","SNP7879","SUC3","WIKIPEDIA-SV","TALBANKEN"));

    /**
     *
     * @return
     * The corpora
     */
    @JsonProperty("corpora")
    public List<String> getCorpora() {
	return corpora;
    }

    /**
     *
     * @param corpora
     * The corpora
     */
    @JsonProperty("corpora")
    public void setCorpora(List<String> corpora) {
	this.corpora = corpora;
    }

    /**
     *
     * @return
     * The cqpVersion
     */
    @JsonProperty("cqp-version")
    public String getCqpVersion() {
	return cqpVersion;
    }

    /**
     *
     * @param cqpVersion
     * The cqp-version
     */
    @JsonProperty("cqp-version")
    public void setCqpVersion(String cqpVersion) {
	this.cqpVersion = cqpVersion;
    }

    /**
     *
     * @return
     * The protectedCorpora
     */
    @JsonProperty("protected_corpora")
    public List<String> getProtectedCorpora() {
	return protectedCorpora;
    }

    /**
     *
     * @param protectedCorpora
     * The protected_corpora
     */
    @JsonProperty("protected_corpora")
    public void setProtectedCorpora(List<String> protectedCorpora) {
	this.protectedCorpora = protectedCorpora;
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
     * @param time
     * The time
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

    public static List<String> getOpenCorpora() {
        ObjectMapper mapper = new ObjectMapper();

	ServiceInfo si = null;
	final String wsString = "https://spraakbanken.gu.se/ws/korp?";
	final String queryString = "command=info";

        try {
	    URL korp = new URL(wsString + queryString);

            si = mapper.reader(ServiceInfo.class).readValue(korp.openStream());
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

	List<String> openCorpora = new ArrayList<String>();
	boolean isPC = false;
	for (String corpus : si.getCorpora()) {
	    for (String pCorpus : si.getProtectedCorpora()) {
		if (corpus.equals(pCorpus)) {
		    isPC = true;
		}
	    }
	    if (!isPC) {
		openCorpora.add(corpus);
	    }
	    isPC = false;
	}
	return openCorpora;
    }

    public static List<String> getModernCorpora() {
	List<String> modernCorpora = new ArrayList<String>();
	List<String> openCorpora = ServiceInfo.getOpenCorpora();
	for (String corpus : openCorpora) {
		if (MODERN_CORPORA.contains(corpus)) {
		    modernCorpora.add(corpus);
		}
	}
	return modernCorpora;
    }

}
