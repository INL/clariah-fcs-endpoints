package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info.ServiceInfo;

public class ServiceInfoTest {
    private String jsonString = "{\"corpora\":[\"ROMI\"],\"cqp-version\":\"CQP version 3.4.9\",\"protected_corpora\":[],\"time\":0.0093}";
    private String jsonString2 = "{\"corpora\":[\"FRAGELADAN\",\"LSI\",\"WIKIPEDIA-SV\"],\"cqp-version\":\"CQP version 3.4.9\",\"protected_corpora\":[\"LSI\",\"FRAGELADAN\"],\"time\":0.10500788688659668}";

    @Test
    public void serviceInfoSerialize() {
        ObjectMapper mapper = new ObjectMapper();

        ServiceInfo si = new ServiceInfo();
	List corpora = new ArrayList();
	corpora.add("ROMI");
        si.setCorpora(corpora);
	si.setCqpVersion("CQP version 3.4.9");
	si.setTime(new Double("0.0093"));

        String s = null;
        try {
            s = mapper.writeValueAsString(si);
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(s);
	assertEquals(jsonString, s);
    }
    
    @Test
    public void serviceInfoDeserialize() {
        ObjectMapper mapper = new ObjectMapper();

        ServiceInfo si2 = null;
	String roundTripString = "";

        try {
            si2 = mapper.reader(ServiceInfo.class).readValue(jsonString2);
	    roundTripString = mapper.writeValueAsString(si2);
        }
        catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(roundTripString);
	assertEquals(jsonString2, roundTripString);
	assertEquals(si2.getCqpVersion(), "CQP version 3.4.9");
	assertEquals(si2.getTime(), new Double("0.10500788688659668"));
    }

    @Test
    public void corporaInfoDeserializeURL() {
        ObjectMapper mapper = new ObjectMapper();

	ServiceInfo si4 = null;
	String roundTripString = "";
	String wsString ="https://spraakbanken.gu.se/ws/korp?";
	String queryString = "indent=4&command=info";

        try {
	    URL korp = new URL(wsString + queryString);

            si4 = mapper.reader(ServiceInfo.class).readValue(korp.openStream());
	    roundTripString = mapper.writeValueAsString(si4);
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

        System.out.println(roundTripString);
	//assertEquals(jsonString, roundTripString);
	assertNotNull(si4.getTime());
	assertNotNull(si4.getCqpVersion());

    }

    public static void main(String[] args) {
    }
}
