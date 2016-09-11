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

import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.statistics.*;

public class CountLemgramsTest {
    private String jsonString = "{\"ge..vb.1\":9516,\"hitta..vb.1\":1944,\"time\":0.017657041549682617}";

    @Test
    public void countLemgramsSerialize() {
        ObjectMapper mapper = new ObjectMapper();

        CountLemgrams cl = new CountLemgrams();
	cl.setLemgram("ge..vb.1", new Integer("9516"));
	cl.setLemgram("hitta..vb.1", new Integer("1944"));
	cl.setTime(new Double("0.017657041549682617"));

        String s = null;
        try {
            s = mapper.writeValueAsString(cl);
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(s);
	assertEquals(new Integer("9516"), cl.getLemgram("ge..vb.1"));
    }
    
    @Test
    public void countLemgramsDeserialize() {
        ObjectMapper mapper = new ObjectMapper();

	CountLemgrams cl2 = null;
	String roundTripString = "";

        try {
            cl2 = mapper.reader(CountLemgrams.class).readValue(jsonString);
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
	//assertEquals(jsonString, roundTripString);
	assertEquals(new Integer("9516"), cl2.getLemgram("ge..vb.1"));
	assertEquals(new Integer("1944"), cl2.getLemgram("hitta..vb.1"));
	assertEquals(cl2.getTime(), new Double("0.017657041549682617"));

    }

    @Test
    public void countLemgramsDeserializeURL() {
        ObjectMapper mapper = new ObjectMapper();

	CountLemgrams cl4 = null;
	String roundTripString = "";
	String wsString ="https://spraakbanken.gu.se/ws/korp?";
	String queryString = "indent=4&command=lemgram_count&lemgram=ge..vb.1&lemgram=hitta..vb.1&corpus=ROMI&corpus=SUC2";

        try {
	    URL korp = new URL(wsString + queryString);

            cl4 = mapper.reader(CountLemgrams.class).readValue(korp.openStream());
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
	assertNotNull(cl4.getTime());

    }

    public static void main(String[] args) {
    }
}
