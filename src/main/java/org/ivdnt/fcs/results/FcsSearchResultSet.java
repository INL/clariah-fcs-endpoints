package org.ivdnt.fcs.results;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.AdvancedDataViewWriter;
import eu.clarin.sru.server.fcs.Constants;
import eu.clarin.sru.server.fcs.XMLStreamWriterHelper;

/**
 * The FcsSearchResultSet class is the main result object for the FCS endpoints.
 * 
 * The other result class (ResultSet) is packed into 
 * the current class, along with the original SRU request and some more things.
 * 
 * @author jesse
 *
 */
public class FcsSearchResultSet extends SRUSearchResultSet 
{
	// the resultset from the standard example is our ResultSet
	// but Jesse wanted to add some extra information to it (even it
	// the aggregator does nothing with it). That's why the ResultSet
	// is packed into a bigger result object FcsSearchResultSet
	// which the extra information Jesse wanted to be able to send
	
	
	SRURequest request;
	String corpus;
	ResultSet resultSet;
	
	// list of text results, t.i. keywords in context (Kwic)
	List<Kwic> hits;
	
	// instantiate current record number 
	int currentRecordNr = -1;

	public static final String CLARIN_FCS_RECORD_SCHEMA = "http://clarin.eu/fcs/resource";


	
	// ---------------------------------------------------------------------------------
	
	/**
	 * FcsSRUSearchResultSet constructor
	 * 
	 * This is instantiated as soon as our ResultSet is ready to be sent
	 * to the Aggregator or some other webservice querying our FCS endpoints
	 * 
	 * We not only return the ResultSet, but also the
	 * SRUServerConfig, the SRURequest, and the SRUDiagnosticList
	 * 
	 * @param config, this contains the base URL, host, port, etc.
	 * @param request, this contains the SRU query, the sorting key, the query type, etc.
	 * @param diagnostics
	 * @param resultSet
	 */
	public FcsSearchResultSet(SRUServerConfig config, SRURequest request, 
			SRUDiagnosticList diagnostics,
			ResultSet resultSet) {

		super(diagnostics);
		this.request =		request;
		this.resultSet = 	resultSet;
		this.hits = 		resultSet.getHits();
		this.corpus = 		resultSet.getQuery().getCorpus();
	}

	// ---------------------------------------------------------------------------------
	// getters
	
	
	@Override
	public int getTotalRecordCount() 
	{
		return this.resultSet.getTotalNumberOfResults(); 
	}

	@Override
	public int getRecordCount() 
	{
		return this.hits.size();
	}

	@Override
	public String getRecordSchemaIdentifier() {

		return this.request.getRecordSchemaIdentifier() != null ? 
				
				this.request.getRecordSchemaIdentifier() : CLARIN_FCS_RECORD_SCHEMA;
	}

	@Override
	public boolean nextRecord() throws SRUException {

		if (currentRecordNr + 1 < hits.size()) {
			currentRecordNr++;
			return true;
		}
		return false;
	}

	/**
	 * Let op: als er een recordIdentifier in de XML komt, geeft de aggregator een error,
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
	
	
	
	// ---------------------------------------------------------------------------------
	// setter / writer
	

	/**
	 * Write the results (keywords in context) 
	 * as a value of the 'kwic' key in the JSON response string
	 */
	@Override
	public void writeRecord(XMLStreamWriter writer) throws XMLStreamException 
	{

		AdvancedDataViewWriter helper =
				new AdvancedDataViewWriter(AdvancedDataViewWriter.Unit.ITEM);



		long start = 1;
		Kwic kwic = this.hits.get( this.currentRecordNr );
		
		
		
		// for debugging  -----------------

		if (false) 
			System.err.println("start writing kwic " + this.currentRecordNr + " : " + kwic);

		// --------------------------------
		
		
		
		XMLStreamWriterHelper.writeStartResource(writer, corpus + "-" + kwic.getHitStart(), null);
		XMLStreamWriterHelper.writeStartResourceFragment(writer, null, null);

		// TODO insert a metadata dataview here, CMDI and possibly dublin core as well (?)

		writer.writeStartElement("fcs", "DataView", "http://clarin.eu/fcs/resource");
		writer.writeAttribute(null, null, "type", "application/x-clariah-fcs-simple-metadata+xml");
		kwic.getMetadata().forEach(
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
		 * <cmdi:CMD xmlns:cmdi="http://www.clarin.eu/cmd/" CMDVersion="1.1">
		 * <!-- content omitted -->
		 * </cmdi:CMD>
		 * </fcs:DataView>
		 */

		try
		{
			
			// we need to write the results (keywords in context) 
			// in 3 stages:
			//
			// [1] left context   [2] the hit   [3] right context
			
			
			
			// [1] left context ----------------------------------
			
			for (int i = 0; i < kwic.getHitStart(); i++) 
			{
				long end = start + kwic.getWord(i).length();
				for (int j=0; j < kwic.getTokenPropertyNames().size(); j++)
				{
					String pname  = kwic.getTokenPropertyNames().get(j);
					
					// show info when debugging
					if (false) 
					{
						System.err.println(String.format("add to layer: layer: %s, start: %d, end: %d, value: %s", 
								kwic.getLayerURL(pname),
								start,
								end,
								kwic.get(pname, i)));
					}
						
					
					helper.addSpan(kwic.getLayerURL(pname), start, end, kwic.get(pname, i));
				}
				start = end + 1;
			}

			
			// [2] the hit ----------------------------------

			for (int i = kwic.getHitStart(); i < kwic.getHitEnd(); i++) {
				long end = start + kwic.getWord(i).length();
				for (int j=0; j < kwic.getTokenPropertyNames().size(); j++)
				{
					String pname  = kwic.getTokenPropertyNames().get(j);
					helper.addSpan(kwic.getLayerURL(pname), start, end, kwic.get(pname, i), 1);
				}
				start = end + 1;
			}
			
			
			// [3] right context ----------------------------------

			for (int i =  kwic.getHitEnd(); i < kwic.size(); i++) 
			{
				long end = start + kwic.getWord(i).length();
				for (int j=0; j < kwic.getTokenPropertyNames().size(); j++)
				{
					String pname  = kwic.getTokenPropertyNames().get(j);
					helper.addSpan(kwic.getLayerURL(pname), start, end, kwic.get(pname, i));
				}
				start = end + 1;
			}
			
			
			// write the results now

			helper.writeHitsDataView(writer, kwic.getLayerURL(kwic.getDefaultProperty()));
			if (request == null || request.isQueryType(Constants.FCS_QUERY_TYPE_FCS)) {
				helper.writeAdvancedDataView(writer);
			}

			XMLStreamWriterHelper.writeEndResourceFragment(writer);
			XMLStreamWriterHelper.writeEndResource(writer);
			
		} 
		
		catch (Exception e) {
			e.printStackTrace();
			throw new XMLStreamException(e.getMessage());
		}
		
	}
	
	
	// ---------------------------------------------------------------------------------

}