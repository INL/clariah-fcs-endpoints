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

import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info.*;

public class CorporaInfoTest {
    private String jsonString = "{\"corpora\":{\"PAROLE\":{\"attrs\":{\"a\":[],\"p\":[\"word\",\"pos\",\"msd\",\"lemma\",\"lex\",\"saldo\",\"prefix\",\"suffix\",\"ref\",\"dephead\",\"deprel\"],\"s\":[\"sentence\",\"sentence_id\",\"text\",\"text_id\",\"text_date\",\"text_title\",\"text_publisher\",\"text_datefrom\",\"text_dateto\",\"text_timefrom\",\"text_timeto\"]},\"info\":{\"Charset\":\"utf8\",\"FirstDate\":\"1976-01-01 00:00:00\",\"LastDate\":\"1997-06-16 23:59:59\",\"Saldo\":\"73089\",\"Sentences\":\"1646688\",\"Size\":\"24331936\",\"Updated\":\"2016-03-15\"}},\"ROMI\":{\"attrs\":{\"a\":[],\"p\":[\"word\",\"pos\",\"msd\",\"lemma\",\"lex\",\"saldo\",\"prefix\",\"suffix\",\"ref\",\"dephead\",\"deprel\"],\"s\":[\"sentence\",\"sentence_id\",\"text\",\"text_title\",\"text_datefrom\",\"text_dateto\",\"text_timefrom\",\"text_timeto\",\"paragraph\",\"paragraph_n\",\"text_author\",\"text_year\"]},\"info\":{\"Charset\":\"utf8\",\"FirstDate\":\"1976-01-01 00:00:00\",\"LastDate\":\"1977-12-31 23:59:59\",\"Saldo\":\"73089\",\"Sentences\":\"499030\",\"Size\":\"6579220\",\"Updated\":\"2015-12-18\"}}},\"time\":4.41E-4,\"total_sentences\":2145718,\"total_size\":30911156}";

    @Test
    public void corporaInfoSerialize() {
        ObjectMapper mapper = new ObjectMapper();

        CorporaInfo ci = new CorporaInfo();
	Corpus corpus = new Corpus();
	CorpusAttrs corpusAttrs = new CorpusAttrs();
	CorpusMetaInfo corpusMeta = new CorpusMetaInfo();

	List aList = new ArrayList<Object>();
	List pList = new ArrayList<String>();
	List sList = new ArrayList<String>();

	pList.add("word"); 
 	pList.add("pos"); 
 	pList.add("msd"); 
 	pList.add("lemma"); 
 	pList.add("lex"); 
 	pList.add("saldo"); 
 	pList.add("prefix"); 
 	pList.add("suffix"); 
 	pList.add("ref"); 
 	pList.add("dephead"); 
 	pList.add("deprel");

	sList.add("sentence");
	sList.add("sentence_id");
	sList.add("text");
	sList.add("text_id");
	sList.add("text_date");
	sList.add("text_title");
	sList.add("text_publisher");
	sList.add("text_datefrom");
	sList.add("text_dateto");
	sList.add("text_timefrom");
	sList.add("text_timeto");

	corpusAttrs.setA(aList);
	corpusAttrs.setP(pList);
	corpusAttrs.setS(sList);

	corpusMeta.setCharset("utf8"); 
	corpusMeta.setFirstDate("1976-01-01 00:00:00");
	corpusMeta.setLastDate("1997-06-16 23:59:59");
	corpusMeta.setSaldo("73089"); 
	corpusMeta.setSentences("1646688"); 
	corpusMeta.setSize("24331936"); 
	corpusMeta.setUpdated("2016-03-15");

	corpus.setAttrs(corpusAttrs);
	corpus.setMetaInfo(corpusMeta);
	ci.setCorpus("PAROLE", corpus);

	aList = new ArrayList<Object>();
	pList = new ArrayList<String>();
	sList = new ArrayList<String>();

	pList.add("word");
	pList.add("pos");
	pList.add("msd");
	pList.add("lemma");
	pList.add("lex");
	pList.add("saldo");
	pList.add("prefix");
	pList.add("suffix");
	pList.add("ref");
	pList.add("dephead");
	pList.add("deprel");

	sList.add("sentence");
	sList.add("sentence_id"); 
	sList.add("text"); 
	sList.add("text_title");
	sList.add("text_datefrom");
	sList.add("text_dateto");
	sList.add("text_timefrom"); 
	sList.add("text_timeto");
	sList.add("paragraph"); 
	sList.add("paragraph_n"); 
	sList.add("text_author"); 
	sList.add("text_year"); 

	corpusAttrs = new CorpusAttrs();
	corpusAttrs.setA(aList);
	corpusAttrs.setP(pList);
	corpusAttrs.setS(sList);

	corpusMeta = new CorpusMetaInfo();
	corpusMeta.setCharset("utf8");
        corpusMeta.setFirstDate("1976-01-01 00:00:00"); 
	corpusMeta.setLastDate("1977-12-31 23:59:59"); 
	corpusMeta.setSaldo("73089"); 
	corpusMeta.setSentences("499030");
	corpusMeta.setSize("6579220");
	corpusMeta.setUpdated("2015-12-18");

	corpus = new Corpus();
	corpus.setAttrs(corpusAttrs);
	corpus.setMetaInfo(corpusMeta);
	ci.setCorpus("ROMI", corpus);

	ci.setTime(new Double("0.000441"));
	ci.setTotalSentences(new Integer("2145718"));
	ci.setTotalSize(new Integer("30911156"));

        String s = null;
        try {
            s = mapper.writeValueAsString(ci);
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(s);
	assertEquals(jsonString, s);
    }
    
    @Test
    public void corporaInfoDeserialize() {
        ObjectMapper mapper = new ObjectMapper();

	CorporaInfo ci2 = null;
	String roundTripString = "";

        try {
            ci2 = mapper.reader(CorporaInfo.class).readValue(jsonString);
	    roundTripString = mapper.writeValueAsString(ci2);
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
	assertEquals(ci2.getTime(), new Double("0.000441"));

    }

    @Test
    public void corporaInfoDeserializeURL() {
        ObjectMapper mapper = new ObjectMapper();

	CorporaInfo ci4 = null;
	String roundTripString = "";
	String wsString ="https://spraakbanken.gu.se/ws/korp?";
	String queryString = "indent=4&command=info&corpus=ROMI,PAROLE";

        try {
	    URL korp = new URL(wsString + queryString);

            ci4 = mapper.reader(CorporaInfo.class).readValue(korp.openStream());
	    roundTripString = mapper.writeValueAsString(ci4);
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
	assertNotNull(ci4.getTime());
    }

    public static void main(String[] args) {
    }
}
