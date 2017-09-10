# clariah-fcs-endpoints

Endpoints for fcs 2.0 federated corpus search in CLARIAH.

Currently has initial basic support for blacklab server and the Nederlab portal.

Based on the fhe Korp fcs 2.0 reference endpoint implementation (https://github.com/clarin-eric/fcs-korp-endpoint), which in turn builds on https://svn.clarin.eu/FCSSimpleEndpoint/

We test by running the 'clarin fcs aggregator' (cf below) locally.


## Clarin Federated Content Search

Cf:
* https://www.clarin.eu/content/federated-content-search-clarin-fcs
* https://svn.clarin.eu/FCSSimpleEndpoint/ (reference endpoint implementation)  
* https://svn.clarin.eu/FCS-QL/
* https://www.clarin.eu/sites/default/files/CE-2015-0629-FCS-2-workplan.pdf
* published FCS 2.0 specification at https://office.clarin.eu/v/CE-2017-1046-FCS-Specification.pdf
* On dataviews cf. https://www.clarin.eu/sites/default/files/CE-2014-0317-CLARIN_FCS_Specification_DataViews_1_0.pdf, met name CMDI data view. 
* Zie ook https://office.clarin.eu/v/CE-2017-1035-CLARINPLUS-D2_9.pdf voor aggregator. 
* Aggregator code: https://svn.clarin.eu/SRUAggregator; zie ook https://www.clarin.eu/content/clarin-plus-supplemental-material. 
* Also (alternative endpoint) https://github.com/KorAP/KorapSRU 


### The aggregator

Is a simple portal for federated search. It is still alpha software.

https://spraakbanken.gu.se/ws/fcs/2.0/aggregator/# for a running version; source at https://svn.clarin.eu/SRUAggregator/
 
## Quick start

Call 'mvn package' and 'mvn war:war' to create a war file; deploy it, and start the aggregator to test. 

Configuration is described below.

### Running with the aggregator (rekenserver only)

start conf/start_aggregator.sh and browse to http://localhost:4019/Aggregator

## Endpoint implementations

### Blacklab Server

Documentation at http://inl.github.io/BlackLab/blacklab-server-overview.html.

Is the response format described?
 

### Nederlab portal

Cf https://github.com/meertensinstituut/mtas and https://meertensinstituut.github.io/mtas/

We use the Nederlab test broker, http://www.nederlab.nl/testbroker/search/#examples

Issues:
* JSON response difficult to understand
* JSON query options also not so obvious
* Cannot get kwics for PoS queries

### Corpusdependent

Chooses one of the above dependent on the corpus.

## Configuration

### web.xml

The servlet WEB-INF/web.xml should specify which engine class (extending extends SimpleEndpointSearchEngineBase from the FCSSimpleEndpoint implementation) handles the search requests.

Currently

<pre>
&lt;init-param>
	&lt;param-name>eu.clarin.sru.server.utils.sruServerSearchEngineClass&lt;/param-name>
	&lt;param-value>org.ivdnt.fcs.endpoint.corpusdependent.CorpusDependentEngine&lt;/param-value>
&lt;/init-param>
</pre>

### The resource description

This lists the corpora the endpoint gives access to

Source file is src/main/webapp/WEB-INF/endpoint-description.xml

### Configuration in the source

* Some defaults 
* The Corpus dependent endpoint contains a mapping from corpus names to Engine class. This should move to a file.


### Aggregator configuration
The aggregator depends on the following resources

#### yaml file 

(for us, conf/aggregator_devel.yml)

#### The Clarin eu center list 

(for testing, http://localhost:8080/blacklab-sru-server/registry/clarin_center_list.xml)

The official location for this is https://centres.clarin.eu/restxml/ 
A testing version is at src/main/webapp/registry/clarin_center_list.xml

We are number 22:
<pre>
&lt;CenterProfile>
	&lt;Centername>Instituut voor de Nederlandse Taal&lt;/Centername>
	&lt;Center_id>22&lt;/Center_id>
	&lt;Center_id_link>https://centres.clarin.eu/restxml/22&lt;/Center_id_link>
&lt;/CenterProfile>
</pre>

#### The center profile 

Official location for INT:  https://centres.clarin.eu/restxml/22 (We need to update this if we want to make resources visible for the aggregator running at Språkbanken)

Testing location: http://localhost:8080/blacklab-sru-server/registry/clarin_center_ivdnt.xml

(again, source is in src/main/webapp/registry)

This should contain something like

<pre>
&lt;WebReference>
  &lt;Website>http://localhost:8080/blacklab-sru-server/sru&lt;/Website>
  &lt;Description>CQL&lt;/Description>
&lt;/WebReference>
</pre>


## Mapping

The federated queries are supposed to be in Universal Dependencies.

There is a simple mapping mechanism from UD to

* CGN tagset, with variant dialects for Nederlab and Opensonar (Nederlab has feature names, Opensonar uses regexes to search for features)
* CHN tagset
* Letters as Loot tagset

##

Als alles meezit krijg je er zoiets uit:

![alt text](https://github.com/INL/clariah-fcs-endpoints/raw/master/doc/aggregator-collections.png "Collection dialog")

![alt text](https://github.com/INL/clariah-fcs-endpoints/raw/master/doc/aggregator-results.png "Search results")
