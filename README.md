# CLARIAH Federated content search corpora
CLARIAH Federated content search corpora, developed by the [Dutch Language Institute (INT)](https://github.com/INL), is a service to enable searching in multiple Dutch corpora at the same time. This application implements the [CLARIN FCS specification](https://office.clarin.eu/v/CE-2017-1046-FCS-Specification.pdf). This repository hosts the source code.


## Using CLARIAH FCS corpora
* A web interface for only the Dutch corpora is available here: https://portal.clarin.inl.nl/fcscorporav2
* The CLARIN FCS web interface, consisting of all European corpora, also features access to the Dutch FCS corpora:  https://spraakbanken.gu.se/ws/fcs/2.0/aggregator/# . For this, it utilizes the backend hosted at the INT: https://portal.clarin.inl.nl/fcscorpora/clariah-fcs-endpoints/sru


## Corpora
CLARIAH FCS Corpora currently has initial basic support for corpora based on [Blacklab Server](https://inl.github.io/blacklab) (INT corpora) and [MTAS](https://meertensinstituut.github.io/mtas/) (Nederlab). The following corpora are included:
 * [Letters as Loot (Brieven als Buit, 17th and 18th century sailors' letters)](https://brievenalsbuit.ivdnt.org/)
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
* On dataviews cf. https://www.clarin.eu/sites/default/files/CE-2014-0317-CLARIN_FCS_Specification_DataViews_1_0.pdf, specifically CMDI data view
* Also (alternative endpoint) https://github.com/KorAP/KorapSRU 


The backend communicates with Blacklab Server for the INT corpora ([BlackLab Server documentation here](http://inl.github.io/BlackLab/blacklab-server-overview.html)). For Nederlab, the backend communicates not directly with [https://meertensinstituut.github.io/mtas/](MTAS), but with an intermediate layer, which restricts access to the corpus, but accepts the same MTAS queries. For more about MTAS, see also the [GitHub repository](https://github.com/meertensinstituut/mtas).

### Aggregator

The aggregator is simple web interface for federated search, developed by CLARIN. It is still alpha software. The `lib` directory of this repository contains a version (not necessarily the latest) of the Aggregator.

* Aggregator code: https://svn.clarin.eu/SRUAggregator; see also https://www.clarin.eu/content/clarin-plus-supplemental-material
* See also https://office.clarin.eu/v/CE-2017-1035-CLARINPLUS-D2_9.pdf
* See https://spraakbanken.gu.se/ws/fcs/2.0/aggregator/# for a running version of the aggregator at the Swedish Language Bank.
 
