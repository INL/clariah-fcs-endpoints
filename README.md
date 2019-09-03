# CLARIAH Federated content search corpora
CLARIAH Federated content search corpora, developed by the [Dutch Language Institute (INT)](https://github.com/INL), is a service to enable searching in multiple Dutch corpora at the same time. This application implements the [CLARIN FCS specification](https://office.clarin.eu/v/CE-2017-1046-FCS-Specification.pdf). This repository hosts the source code.


## Using CLARIAH FCS corpora
* A web interface for only the Dutch corpora is available here: https://portal.clarin.inl.nl/fcscorporav2
* The CLARIN FCS web interface, consisting of all European corpora, also features access to the Dutch FCS corpora:  https://spraakbanken.gu.se/ws/fcs/2.0/aggregator/#

## Corpora
CLARIAH FCS Corpora currently has initial basic support for corpora based on [Blacklab Server](https://inl.github.io/blacklab) (INT corpora) and [MTAS](https://meertensinstituut.github.io/mtas/) (Nederlab). The following corpora are included:
 * Letters as Loot (Brieven als Buit, 17th and 18th century sailors' letters)
 * [Corpus Gysseling (13th century)](http://gysseling.corpus.taalbanknederlands.inl.nl/gysseling/page/search)
 * [OpenSoNaR](https://portal.clarin.inl.nl/opensonar_frontend/opensonar/search)
 * [Corpus of Contemporary Dutch (Corpus Hedendaags Nederlands, CHN)](http://corpushedendaagsnederlands.inl.nl/)
 * [Nederlab (Meertens Institute)](https://www.nederlab.nl/onderzoeksportaal/?action=verkennen)


## Architecture
CLARIAH FCS Corpora consists of a backend and a webinterface (aggregator). The backend is based on a CLARIN backend implementation, extended with many specificalities for the Dutch corpora. The aggregator is more or less a copy of the CLARIN aggregator.

### Backend
Based on the fhe Korp fcs 2.0 reference endpoint implementation (https://github.com/clarin-eric/fcs-korp-endpoint), which in turn builds on https://svn.clarin.eu/FCSSimpleEndpoint/

Code of dependencies of this project:
* https://svn.clarin.eu/FCSSimpleEndpoint/ (reference endpoint implementation)  
* https://svn.clarin.eu/FCS-QL/ (dependency of this project)
* https://svn.clarin.eu/SRUServer/ (dependency of this project)


Cf:
* https://www.clarin.eu/content/federated-content-search-clarin-fcs
* https://www.clarin.eu/sites/default/files/CE-2015-0629-FCS-2-workplan.pdf
* published FCS 2.0 specification at https://office.clarin.eu/v/CE-2017-1046-FCS-Specification.pdf
* On dataviews cf. https://www.clarin.eu/sites/default/files/CE-2014-0317-CLARIN_FCS_Specification_DataViews_1_0.pdf, met name CMDI data view. 
* Also (alternative endpoint) https://github.com/KorAP/KorapSRU 


The backend communicates with Blacklab Server for the INT corpora ([documentation here](http://inl.github.io/BlackLab/blacklab-server-overview.html)). For Nederlab, the backend communicates not directly with MTAS, but with an intermediate layer, which restricts access to the corpus, but accepts the same MTAS queries. Cf https://github.com/meertensinstituut/mtas and https://meertensinstituut.github.io/mtas/

### Agregator

Is a simple web interface for federated search. It is still alpha software.

https://spraakbanken.gu.se/ws/fcs/2.0/aggregator/# for a running version; source at https://svn.clarin.eu/SRUAggregator/

* Aggregator code: https://svn.clarin.eu/SRUAggregator; see also https://www.clarin.eu/content/clarin-plus-supplemental-material
* See also https://office.clarin.eu/v/CE-2017-1035-CLARINPLUS-D2_9.pdf
 
## Getting started

Call `mvn package` to create a war file, deploy it on Tomcat to start the backend. To start the aggregator, start `conf/start_aggregator.sh` and browse to http://localhost:4019/Aggregator


## Configuration

### Backend configuration

#### web.xml

The servlet `WEB-INF/web.xml` should specify which engine class (extending extends SimpleEndpointSearchEngineBase from the FCSSimpleEndpoint implementation) handles the search requests.

Currently

<pre>
&lt;init-param>
	&lt;param-name>eu.clarin.sru.server.utils.sruServerSearchEngineClass&lt;/param-name>
	&lt;param-value>org.ivdnt.fcs.endpoint.corpusdependent.CorpusDependentEngine&lt;/param-value>
&lt;/init-param>
</pre>

#### The resource description

This lists the corpora the endpoint gives access to: `src/main/webapp/WEB-INF/endpoint-description.xml`

### Aggregator configuration
The aggregator depends on the following resources

#### yaml file 

(for us, `conf/aggregator_devel.yml`)

#### The Clarin eu center list 

(for testing, http://localhost:8080/clariah-fcs-endpoints/registry/clarin_center_list.xml)

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

Official location for INT:  https://centres.clarin.eu/restxml/22 (We need to update this if we want to make resources visible for the aggregator running at Spr�kbanken)

Testing location: http://localhost:8080/clariah-fcs-endpoints/registry/clarin_center_ivdnt.xml

(again, source is in src/main/webapp/registry)

This should contain something like

<pre>
&lt;WebReference>
  &lt;Website>http://localhost:8080/clariah-fcs-endpoints/sru&lt;/Website>
  &lt;Description>CQL&lt;/Description>
&lt;/WebReference>
</pre>
