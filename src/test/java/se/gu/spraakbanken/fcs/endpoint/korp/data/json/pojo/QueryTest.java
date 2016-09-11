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

import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query.*;

public class QueryTest {
    private String jsonString = "{\"corpus_hits\":{\"SUC2\":5934},\"corpus_order\":[\"SUC2\"],\"hits\":5934,\"kwic\":[{\"corpus\":\"SUC2\",\"match\":{\"end\":7,\"position\":86,\"start\":4},\"tokens\":[{\"lemma\":\"|f\u00f6r|\",\"msd\":\"PP\",\"word\":\"F\u00f6r\"}]}],\"querydata\":\"eJwzMjAxtTQ1NzExsja1NDaxDg51NrICsQBF1AWC\\\\n\",\"time\":0.7205440998077393}";
    private String jsonString2 = "{\"corpus_hits\":{\"SUC2\":5934},\"corpus_order\":[\"SUC2\"],\"hits\":5934,\"kwic\":[{\"corpus\":\"SUC2\",\"match\":{\"end\":7,\"position\":86,\"start\":4},\"tokens\":[{\"lemma\":\"|f\u00f6r|\",\"msd\":\"PP\",\"word\":\"F\u00f6r\"},{\"lemma\":\"|viss|\",\"msd\":\"JJ.POS.UTR+NEU.PLU.IND+DEF.NOM\",\"word\":\"vissa\"},{\"lemma\":\"|ledande|\",\"msd\":\"PC.PRS.UTR+NEU.SIN+PLU.IND+DEF.NOM\",\"word\":\"ledande\"},{\"lemma\":\"|statlig|\",\"msd\":\"JJ.POS.UTR+NEU.PLU.IND+DEF.NOM\",\"word\":\"statliga\"},{\"lemma\":\"|och|\",\"msd\":\"KN\",\"word\":\"och\"},{\"lemma\":\"|kommunal|\",\"msd\":\"JJ.POS.UTR+NEU.PLU.IND+DEF.NOM\",\"word\":\"kommunala\"},{\"lemma\":\"|tj\u00e4nsteman|\",\"msd\":\"NN.UTR.PLU.IND.NOM\",\"word\":\"tj\u00e4nstem\u00e4n\"},{\"lemma\":\"|f\u00f6religga|\",\"msd\":\"VB.PRS.AKT\",\"word\":\"f\u00f6religger\"},{\"lemma\":\"|det|\",\"msd\":\"PN.NEU.SIN.DEF.SUB+OBJ\",\"word\":\"det\"},{\"lemma\":\"|valbarhetshinder|\",\"msd\":\"NN.NEU.SIN.IND.NOM\",\"word\":\"valbarhetshinder\"},{\"lemma\":\"|.|\",\"msd\":\"MAD\",\"word\":\".\"}]},{\"corpus\":\"SUC2\",\"match\":{\"end\":9,\"position\":149,\"start\":6},\"tokens\":[{\"lemma\":\"|f\u00f6r|\",\"msd\":\"PP\",\"word\":\"F\u00f6r\"},{\"lemma\":\"|att|\",\"msd\":\"IE\",\"word\":\"att\"},{\"lemma\":\"|f\u00f6rb\u00e4ttra|\",\"msd\":\"VB.INF.AKT\",\"word\":\"f\u00f6rb\u00e4ttra\"},{\"lemma\":\"|service|\",\"msd\":\"NN.UTR.SIN.DEF.NOM\",\"word\":\"servicen\"},{\"lemma\":\"|till|\",\"msd\":\"PP\",\"word\":\"till\"},{\"lemma\":\"|medborgare|\",\"msd\":\"NN.UTR.PLU.DEF.NOM\",\"word\":\"medborgarna\"},{\"lemma\":\"|och|\",\"msd\":\"KN\",\"word\":\"och\"},{\"lemma\":\"|\u00f6ka|\",\"msd\":\"VB.INF.AKT\",\"word\":\"\u00f6ka\"},{\"lemma\":\"|effektivitet|\",\"msd\":\"NN.UTR.SIN.DEF.NOM\",\"word\":\"effektiviteten\"},{\"lemma\":\"|inom|\",\"msd\":\"PP\",\"word\":\"inom\"},{\"lemma\":\"|den|\",\"msd\":\"DT.UTR.SIN.DEF\",\"word\":\"den\"},{\"lemma\":\"|offentlig|\",\"msd\":\"JJ.POS.UTR+NEU.SIN.DEF.NOM\",\"word\":\"offentliga\"},{\"lemma\":\"|sektor|\",\"msd\":\"NN.UTR.SIN.DEF.NOM\",\"word\":\"sektorn\"},{\"lemma\":\"|beh\u00f6va|\",\"msd\":\"VB.PRS.SFO\",\"word\":\"beh\u00f6vs\"},{\"lemma\":\"|en|\",\"msd\":\"DT.UTR.SIN.IND\",\"word\":\"en\"},{\"lemma\":\"|stor|\",\"msd\":\"JJ.KOM.UTR+NEU.SIN+PLU.IND+DEF.NOM\",\"word\":\"st\u00f6rre\"},{\"lemma\":\"|flexibilitet|\",\"msd\":\"NN.UTR.SIN.IND.NOM\",\"word\":\"flexibilitet\"},{\"lemma\":\"|i|\",\"msd\":\"PP\",\"word\":\"i\"},{\"lemma\":\"|fr\u00e5ga|\",\"msd\":\"NN.UTR.SIN.IND.NOM\",\"word\":\"fr\u00e5ga\"},{\"lemma\":\"|om|\",\"msd\":\"PP\",\"word\":\"om\"},{\"lemma\":\"|verksamhetsform|\",\"msd\":\"NN.UTR.PLU.IND.NOM\",\"word\":\"verksamhetsformer\"},{\"lemma\":\"|och|\",\"msd\":\"KN\",\"word\":\"och\"},{\"lemma\":\"|arbetss\u00e4tt|\",\"msd\":\"NN.NEU.PLU.IND.NOM\",\"word\":\"arbetss\u00e4tt\"},{\"lemma\":\"|.|\",\"msd\":\"MAD\",\"word\":\".\"}]}],\"querydata\":\"eJwzMjAxtTQ1NzExsja1NDaxDg51NrICsQBF1AWC\\\\n\",\"time\":0.7205440998077393}";

    @Test
    public void querySerialize() {
        ObjectMapper mapper = new ObjectMapper();

        Query q = new Query();
	q.setCorpusHits("SUC2", new Integer("5934"));

	List co = new ArrayList<String>();
	co.add("SUC2");
	q.setCorpusOrder(co);
	q.setHits(new Integer("5934"));
	List kList = new ArrayList<Kwic>();
	Kwic kwic = new Kwic();
	kwic.setCorpus("SUC2");
	Match match = new Match();
	match.setEnd(new Integer("7"));
	match.setPosition(new Integer("86"));
	match.setStart(new Integer("4"));
	kwic.setMatch(match);
	List tList = new ArrayList<Token>();
	Token tok = new Token();
	tok.setLemma("|f\u00f6r|");
	tok.setMsd("PP");
	tok.setWord("F\u00f6r");
	tList.add(tok);
	kwic.setTokens(tList);
	kList.add(kwic);
	q.setKwic(kList);

	q.setTime(new Double("0.7205440998077393"));
	q.setQuerydata("eJwzMjAxtTQ1NzExsja1NDaxDg51NrICsQBF1AWC\\n");

        String s = null;
        try {
            s = mapper.writeValueAsString(q);
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(s);
	assertEquals(jsonString, s);
    }
    
    @Test
    public void queryDeserialize() {
        ObjectMapper mapper = new ObjectMapper();

	Query q2 = null;
	String roundTripString = "";

        try {
            q2 = mapper.reader(Query.class).readValue(jsonString);
	    roundTripString = mapper.writeValueAsString(q2);
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
	assertEquals(q2.getTime(), new Double("0.7205440998077393"));

    }

    @Test
    public void queryDeserializeFile() {
        ObjectMapper mapper = new ObjectMapper();

	Query q3 = null;
	String roundTripString = "";
	String queryFilename = "src/main/resources/se/gu/spraakbanken/fcs/endpoint/korp/data/json/korp-kwic-1.json";

        try {
            q3 = mapper.reader(Query.class).readValue(new File(queryFilename));
	    roundTripString = mapper.writeValueAsString(q3);
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
	assertEquals(q3.getTime(), new Double("0.7205440998077393"));

    }

    @Test
    public void queryDeserializeURL() {
        ObjectMapper mapper = new ObjectMapper();

	Query q4 = null;
	String roundTripString = "";
	String wsString ="https://spraakbanken.gu.se/ws/korp?";
	String queryString = "indent=4&command=query&corpus=SUC2&start=0&end=9&defaultcontext=1+sentence&cqp=%22och%22+%5B%5D+%5Bpos=%22NN%22%5D&show=msd,lemma";
//lang=sv&stats_reduce=word&cqp=[]&word_pic&search_tab=2&corpus=suc2&search=cqp|[word%3D"och"] [] [pos%3D"NN"]
//command=query
//&defaultcontext=1+sentence
//&show=sentence%2Cpos%2Cmsd%2Clemma%2Clex%2Csaldo%2Cdephead%2Cdeprel%2Cref%2Cprefix%2Csuffix
//&show_struct=text_id
//&cache=true
//&start=0&end=24
//&corpus=SUC2
//&cqp=%5Bword+%3D+%22och%22%5D+%5B%5D+%5Bpos+%3D+%22NN%22%5D
//&queryData=eJzTNbQ0tDAwNjU1MbM2sA4OdTayMgAALC4EZQ%3D%3D%0A
//&context=
//&incremental=true
//&defaultwithin=sentence
//&within=
        try {
	    URL korp = new URL(wsString + queryString);

            q4 = mapper.reader(Query.class).readValue(korp.openStream());
	    roundTripString = mapper.writeValueAsString(q4);
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
	assertNotNull(q4.getTime());

    }

    @Test
    public void missingQueryParameterDeserializeURL() {
        ObjectMapper mapper = new ObjectMapper();

	Query q4 = null;
	String roundTripString = "";
	String wsString ="https://spraakbanken.gu.se/ws/korp?";
	String queryString = "indent=4&command=query&corpus=SUC2&start=0&end=9&defaultcontext=1+sentence&cq=%22och%22+%5B%5D+%5Bpos=%22NN%22%5D&show=msd,lemma";
        try {
	    URL korp = new URL(wsString + queryString);

            q4 = mapper.reader(Query.class).readValue(korp.openStream());
	    roundTripString = mapper.writeValueAsString(q4);
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
	assertNotNull(q4.getHits());
	assertEquals(new Integer("-1"), q4.getHits());
	assertNotNull(q4.getAdditionalProperties().get("ERROR"));
	assertNotNull(q4.getTime());

    }

    public static void main(String[] args) {
    }
}
