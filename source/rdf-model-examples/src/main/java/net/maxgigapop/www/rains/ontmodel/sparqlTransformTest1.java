/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.maxgigapop.www.rains.ontmodel;

import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;

/**
 *
 */
public final class sparqlTransformTest1 {
    public static final String nmlSchemaFileName = "src/main/resources/nml-base-ext.owl";
    public static final String mrsSchemaFileName = "src/main/resources/nml-mrs-ext.owl";
    public static String instanceFileName = "src/main/resources/genericInfModel.ttl";
    public static String sparqlString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
    		+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
    		+ "PREFIX nml: <http://schemas.ogf.org/nml/2013/03/base#>"
    		+ "PREFIX mrs: <http://schemas.ogf.org/mrs/2013/12/topology#>";
    
    public static void main(String[] args) {
        sparqlString += "CONSTRUCT {?s nml:hasBidirectionalPort ?o.} " +
        		"WHERE {{?s nml:hasBidirectionalPort ?o. ?s a nml:Topology} UNION {?s nml:hasBidirectionalPort ?o. ?s a nml:Node}}";
        /*
        sparqlString += "SELECT ?s ?o " +
        		"WHERE {" +
        		"{?s nml:hasNode ?o.  ?s a nml:Topology.}" +
        		" UNION " +
        		"{?s nml:hasNode ?o.  ?s a nml:Node.}" +
        		" UNION " +
        		"{?s nml:hasBidirectionalPort ?o.  ?s a nml:Topology.}" +
        		" UNION " +
        		"{?s nml:hasBidirectionalPort ?o.  ?s a nml:Node.}" +
        		" UNION " +
        		"{?s nml:hasBidirectionalPort ?o.  ?s a nml:BidirectionalPort.}" +
        		" UNION " +
        		"{?s nml:hasBidirectionalPort ?o.  ?s a nml:SwitchingService.}" +
        		"}";
         */
    	// load NML schema
    	OntModel schema = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        InputStream in = FileManager.get().open( nmlSchemaFileName );
        if (in == null) {
           throw new IllegalArgumentException("Schema file: " + nmlSchemaFileName + " not found");
        }
        schema.read(in, null);
    	// load MRS schema
    	OntModel schema2 = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        in = FileManager.get().open( mrsSchemaFileName );
        if (in == null) {
           throw new IllegalArgumentException("Schema file: " + mrsSchemaFileName + " not found");
        }
        schema2.read(in, null);
        schema.add(schema2);
        // load instance model
        if (args.length > 0) {
        	instanceFileName = args[0];
        }
        Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        in = FileManager.get().open( instanceFileName );
        if (in == null) {
            throw new IllegalArgumentException("Instance file: " + instanceFileName + " not found");
         }
        model.read(in, null, "TURTLE");
        model.add(schema);
        Query query = QueryFactory.create(sparqlString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        /*
        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(System.out, results, query) ;
        */
        Model modelConstructed = qexec.execConstruct();
        //modelConstructed.setNsPrefix("nml", "http://schemas.ogf.org/nml/2013/03/base#");
        //modelConstructed.setNsPrefix("mrs", "http://schemas.ogf.org/mrs/2013/12/topology#");       
        modelConstructed.write(System.out, "RDF/XML-ABBREV");
    }
}
