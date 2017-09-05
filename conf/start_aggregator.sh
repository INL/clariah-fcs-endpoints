JAR=`find lib/ -name 'aggregator-*.jar'`
java -cp $JAR -Xmx4096m eu.clarin.sru.fcs.aggregator.app.Aggregator server conf/aggregator_devel.yml
