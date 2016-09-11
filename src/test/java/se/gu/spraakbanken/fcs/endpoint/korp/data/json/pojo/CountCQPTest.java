package se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.statistics.*;

public class CountCQPTest {
    private String jsonString = "{\"corpora\":{\"ROMI\":{\"absolute\":{\"givande\":9},\"relative\":{\"givande\":1.367943312429133},\"sums\":{\"absolute\":9,\"relative\":1.367943312429133}}},\"count\":1,\"time\":0.8480129241943359,\"total\":{\"absolute\":{\"givande\":9},\"relative\":{\"givande\":1.367943312429133},\"sums\":{\"absolute\":9,\"relative\":1.367943312429133}}}";

    @Test
    public void countCQPSerialize() {
        ObjectMapper mapper = new ObjectMapper();

        CountCQP cl = new CountCQP();
	CorpusFreqs cf = new CorpusFreqs();
	cf.addAbsolute("givande", new Integer("9"));
	cf.addRelative("givande", new Double("1.367943312429133"));

	Sums s1 = new Sums();
	s1.setAbsolute(new Integer("9"));
	s1.setRelative(new Double("1.367943312429133"));
	cf.setSums(s1);

	Map cMap = new HashMap<String, CorpusFreqs>();
	cMap.put("ROMI", cf);

	cl.setCorpora(cMap);
	cl.setCount(new Integer("1"));
	cl.setTime(new Double("0.8480129241943359"));
	// In this case with a single attribute cf can be reused as is
	cl.setTotal(cf);

        String s = null;
        try {
            s = mapper.writeValueAsString(cl);
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(s);
	assertEquals(jsonString, s);
    }
    
    @Test
    public void countCQPDeserialize() {
        ObjectMapper mapper = new ObjectMapper();

	CountCQP cl2 = null;
	String roundTripString = "";

        try {
            cl2 = mapper.reader(CountCQP.class).readValue(jsonString);
	    roundTripString = mapper.writeValueAsString(cl2);
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
	assertEquals(jsonString, roundTripString);
	assertEquals(cl2.getTime(), new Double("0.8480129241943359"));

    }

    @Test
    public void countCQPDeserializeURL() {
        ObjectMapper mapper = new ObjectMapper();

	CountCQP cl4 = null;
	String roundTripString = "";
	String wsString ="https://spraakbanken.gu.se/ws/korp?";
	String queryString = "indent=4&command=count&cqp=%5Bword=%22givande%22%5D&corpus=ROMI&groupby=word&ignore_case=word";
//{"corpora":{},"time":3.287792205810547E-4,"ERROR":{"type":"KeyError","value":"'Key is required: groupby'"}}

        try {
	    URL korp = new URL(wsString + queryString);

            cl4 = mapper.reader(CountCQP.class).readValue(korp.openStream());
	    roundTripString = mapper.writeValueAsString(cl4);
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
	assertEquals("ROMI", cl4.getCorpora().keySet().toArray()[0]);
	assertNotNull(cl4.getTime());

    }

    public static void main(String[] args) {
    }
}
