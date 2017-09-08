package org.ivdnt.fcs.endpoint.bls;

import clariah.fcs.*;
import java.util.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.AdvancedDataViewWriter;
import eu.clarin.sru.server.fcs.Constants;
import eu.clarin.sru.server.fcs.XMLStreamWriterHelper;
import se.gu.spraakbanken.fcs.endpoint.korp.cqp.SUCTranslator;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info.CorporaInfo;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query.Query;

public class BlacklabSRUSearchResultSet extends SRUSearchResultSet 
{
	List<Kwic> hits;
	int currentRecord = -1;
	SRURequest request;
	String corpus;
	ResultSet bsrs;
	
	public static final String CLARIN_FCS_RECORD_SCHEMA = "http://clarin.eu/fcs/resource";
	
	
	
	public BlacklabSRUSearchResultSet(SRUServerConfig config, SRURequest request, SRUDiagnosticList diagnostics,
			ResultSet bsrs) {
		
		super(diagnostics);
		this.request = request;
		this.bsrs = bsrs;
		this.hits = bsrs.hits;
		this.corpus = bsrs.query.corpus;
	}

	@Override
	public int getTotalRecordCount() 
	{
		return this.bsrs.totalNumberOfResults; 
	}

	@Override
	public int getRecordCount() 
	{
		return hits.size();
	}

	@Override
	public String getRecordSchemaIdentifier() {
		
		return request.getRecordSchemaIdentifier() != null ? request.getRecordSchemaIdentifier() : CLARIN_FCS_RECORD_SCHEMA;
	}

	@Override
	public boolean nextRecord() throws SRUException {
		
		if (currentRecord + 1 < hits.size()) {
			currentRecord++;
			return true;
		}
		return false;
	}

	/**
	 * Let op: als er een recordIdentifier in de XML komt geeft de aggregator een error,
	 * want hij zit niet in sruResponse.xsd
	 * zie https://lists.oasis-open.org/archives/search-ws-comment/201404/msg00000.html 
	 * @see eu.clarin.sru.server.SRUSearchResultSet#getRecordIdentifier()
	 */
	@Override
	public String getRecordIdentifier() {
		// TODO Auto-generated method stub
		return null;
		// return "rid:" + currentRecord;
	}

	@Override
	public void writeRecord(XMLStreamWriter writer) throws XMLStreamException 
	{
		
		AdvancedDataViewWriter helper =
				new AdvancedDataViewWriter(AdvancedDataViewWriter.Unit.ITEM);



		long start = 1;
		Kwic kwic = hits.get(this.currentRecord);
		if (false) System.err.println("start writing kwic " + currentRecord + " : " + kwic);

		XMLStreamWriterHelper.writeStartResource(writer, corpus + "-" + kwic.hitStart, null);

		XMLStreamWriterHelper.writeStartResourceFragment(writer, null, null);

        // TODO insert a metadata dataview here, CMDI and possibly dublin core as well (?)
		
		writer.writeStartElement("fcs", "DataView", "http://clarin.eu/fcs/resource");
		writer.writeAttribute(null, null, "type", "application/x-clariah-fcs-simple-metadata+xml");
		 kwic.metadata.forEach(
				  (k,v) -> {
				   try { 
					  writer.writeStartElement(null, "keyval", null);
					  writer.writeAttribute(null, null, "key", k);
					  writer.writeAttribute(null, null, "value", v);
					  writer.writeEndElement();
				 } catch (Exception e) {}; }
				 );
		writer.writeEndElement();
		/**
		 * <fcs:DataView type="application/x-cmdi+xml">
 <cmdi:CMD xmlns:cmdi="http://www.clarin.eu/cmd/" CMDVersion="1.1">
 <!-- content omitted -->
 </cmdi:CMD>
</fcs:DataView>
		 */
		
		try
		{
		for (int i = 0; i < kwic.hitStart; i++) 
		{
			long end = start + kwic.getWord(i).length();
			for (int j=0; j < kwic.tokenPropertyNames.size(); j++)
			{
				String pname  = kwic.tokenPropertyNames.get(j);
				if (false) System.err.println(String.format("add to layer: layer: %s, start: %d, end: %d, value: %s", 
						kwic.layerURL(pname),
						start,
						end,
						kwic.get(pname, i)));
				helper.addSpan(kwic.layerURL(pname), start, end, kwic.get(pname, i));
			}
			start = end + 1;
		}


		for (int i = kwic.hitStart; i < kwic.hitEnd; i++) {
			long end = start + kwic.getWord(i).length();
			for (int j=0; j < kwic.tokenPropertyNames.size(); j++)
			{
				String pname  = kwic.tokenPropertyNames.get(j);
				helper.addSpan(kwic.layerURL(pname), start, end, kwic.get(pname, i),1);
			}
			start = end + 1;
		}

		for (int i =  kwic.hitEnd; i < kwic.size(); i++) 
		{
			long end = start + kwic.getWord(i).length();
			for (int j=0; j < kwic.tokenPropertyNames.size(); j++)
			{
				String pname  = kwic.tokenPropertyNames.get(j);
				helper.addSpan(kwic.layerURL(pname), start, end, kwic.get(pname, i));
			}
			start = end + 1;
		}

		helper.writeHitsDataView(writer, kwic.layerURL(kwic.defaultProperty));
		if (request == null || request.isQueryType(Constants.FCS_QUERY_TYPE_FCS)) {
			helper.writeAdvancedDataView(writer);
		}

		XMLStreamWriterHelper.writeEndResourceFragment(writer);
		XMLStreamWriterHelper.writeEndResource(writer);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new XMLStreamException(e.getMessage());
		}
		
		// System.err.println("end writing kwic " + currentRecord + " : " + kwic);
	}

}
