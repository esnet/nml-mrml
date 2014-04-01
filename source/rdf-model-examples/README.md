### Compile and run the demo programs

    mvn clean install
    java -cp lib/*:target/rains-ontmodel-1-SNAPSHOT.jar net.maxgigapop.www.rains.ontmodel.pelletReasonerTest1
    java -cp lib/*:target/rains-ontmodel-1-SNAPSHOT.jar net.maxgigapop.www.rains.ontmodel.modelTransformTest1 

### A brief Terse RDF Triple Language (TURTLE) tutorial
http://haystack.csail.mit.edu/blog/2008/11/06/a-quick-tutorial-on-the-tutrle-rdf-serialization/

### Protege Desktop 4.3 - RDF/OWL edit and view tools
Download: http://protege.stanford.edu/products.php

### Jena eyeball - consistency / integrity check
Ontology schema-to-instance verification tools and consistency / integrity checking tool based on Closed World Assumption (CWA)

Manual: http://jena.sourceforge.net/Eyeball/full.html

Integrity check: 

    ./eyeball.sh -assume owl-base.owl nml-base.owl nml-mrs-ext-v1.owl -check max.rdf

Note: The default Jena OWL inference and validation are based on open-world assumption does not include the above consistency / integrity check.

### Online RDF/XML-TURTLE converter
 http://www.rdfabout.com/demo/validator/

### Visualization tools
#### RDF-Gravity:
http://semweb.salzburgresearch.at/apps/rdf-gravity/index.html

Web start on Ubuntu:

           sudo apt-get install icedtea-netx
           javaws rdf-gravity.jnlp

#### Welkin
http://simile.mit.edu/welkin/
