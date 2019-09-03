# CLARIAH Federated content search corpora
CLARIAH Federated content search corpora, developed by the [Dutch Language Institute (INT)](https://github.com/INL), is a service to enable searching in multiple Dutch corpora at the same time. This application implements the [CLARIN FCS specification](https://office.clarin.eu/v/CE-2017-1046-FCS-Specification.pdf). This repository hosts the source code.


## Using CLARIAH FCS corpora
* A web interface for only the Dutch corpora is available here: https://portal.clarin.inl.nl/fcscorporav2
* The CLARIN FCS web interface, consisting of all European corpora, also features access to the Dutch FCS corpora:  https://spraakbanken.gu.se/ws/fcs/2.0/aggregator/# . For this, it utilizes the backend hosted at the INT: https://portal.clarin.inl.nl/fcscorpora/clariah-fcs-endpoints/sru


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

### Aggregator

Is a simple web interface for federated search. It is still alpha software.

https://spraakbanken.gu.se/ws/fcs/2.0/aggregator/# for a running version; source at https://svn.clarin.eu/SRUAggregator/

* Aggregator code: https://svn.clarin.eu/SRUAggregator; see also https://www.clarin.eu/content/clarin-plus-supplemental-material
* See also https://office.clarin.eu/v/CE-2017-1035-CLARINPLUS-D2_9.pdf
 
## Installation on own computer

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

#### Resource description (adding corpora)

Corpora have to be added to two files, to be included in FCS. `src/main/webapp/WEB-INF/endpoint-description.xml` contains information targeted at users: the corpus name, a description, and a link to the original search engine (web interface, where users can visit it).

The same corpora, with the same names, also have to be added to `src/main/webapp/WEB-INF/endpoint-engines-list.xml`, which contains information on how FCS can access and use the corpus. Here is an example entry:

<pre>
&lt;Engine>
		&lt;engine-name>zeebrieven&lt;/engine-name>
		&lt;engine-type>blacklabserver&lt;/engine-type>
		&lt;engine-url>http://svprmc20.ivdnt.org/blacklab-server/&lt;/engine-url>
		&lt;engine-native-url-template>http://brievenalsbuit.inl.nl/zeebrieven/page/search&lt;/engine-native-url-template>
		&lt;tagset-conversion-table>UD2BaB&lt;/tagset-conversion-table>
	&lt;/Engine>
</pre>

 * `engine-name` is the corpus name, should be the same name as in `endpoint-description.xml`.
 * `engine-type` is the type of corpus protocol, `blacklabserver` or `nederlab`.
 * `engine-url` is the address where the server instance for this corpus is located.
 * `engine-native-url-template` is the template of the location (just a template, arguments are added by FCS) where users can visit the original corpus search engine to search further. This URL is returned in the backend output.
 * `tagset-conversion-table` is the tagset conversion table used. Only tables for which a `.conversion.json` file exists in `src/main/webapp/WEB-INF/` can be supplied.
### Aggregator configuration
The aggregator depends on the following resources

#### yaml file 

(for us, `conf/aggregator_devel.yml`)

#### The Clarin eu center list 

The official location for this is https://centres.clarin.eu/restxml/ .
A testing version is at `src/main/webapp/clarin_center_list.xml`.

We are number 22:
<pre>
&lt;CenterProfile>
	&lt;Centername>Instituut voor de Nederlandse Taal&lt;/Centername>
	&lt;Center_id>22&lt;/Center_id>
	&lt;Center_id_link>https://centres.clarin.eu/restxml/22&lt;/Center_id_link>
&lt;/CenterProfile>
</pre>
From the `Center_id_link` in the center list, the INT center profile is opened.

#### The center profile 

Official location for INT:  https://centres.clarin.eu/restxml/22 .
A testing version is at: `src/main/webapp/WEB-INF/clarin_center_ivdnt.xml`

This should contain something like:

<pre>
&lt;WebReference>
  &lt;Website>https://portal.clarin.inl.nl/fcscorpora/clariah-fcs-endpoints/sru&lt;/Website>
  &lt;Description>CQL&lt;/Description>
&lt;/WebReference>
</pre>

The aggregator will now connect to the backend that is running at the URL supplied inside the `Website` tag in `WebReference`.
