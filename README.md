# clariah-fcs-endpoints

Endpoints for fcs 2.0 federated corpus search in CLARIAH.

Based on the fhe Korp fcs 2.0 reference endpoint implementation (https://github.com/clarin-eric/fcs-korp-endpoint).

We test by running the clarin fcs aggregator.

## Quick start

Call 'mvn war:war' to create a war file. 

There are though some configurations to change if you want to use it with your own Korp service.

### Running with the aggregator (rekenserver only)

start conf/start_aggregator.sh and browse to http://localhost:4019/aggregator.sh

## Endpoints 

### Blacklab Server

### Nederlab portal

### Corpusdependent

Chooses one of the above dependent on the corpus

### Configuration

The aggregator depends on the following resources

### yaml file (for us, conf/aggregator_devel.yml)

### The Clarin eu center list (for testing, http://localhost:8080/blacklab-sru-server/registry/clarin_center_list.xml)

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

### The center profile 

Official location:  https://centres.clarin.eu/restxml/22

Testing location: http://localhost:8080/blacklab-sru-server/registry/clarin_center_ivdnt.xml

(again, source is in src/main/webapp/registry)

This should contain something like

<pre>
                    &lt;WebReference>
                        &lt;Website>http://localhost:8080/blacklab-sru-server/sru&lt;/Website>
                        &lt;Description>CQL&lt;/Description>
                    &lt;/WebReference>
</pre>

