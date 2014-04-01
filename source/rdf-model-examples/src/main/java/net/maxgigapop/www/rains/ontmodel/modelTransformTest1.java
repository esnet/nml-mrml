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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 *
 */
public final class modelTransformTest1 {
    public static String instanceFileName = "src/main/resources/maxInfModel1.ttl";
    public static Resource[] selectSubjects = {
    	Nml.Topology,
    	Nml.Node,
    	Nml.Port,
    	Nml.PortGroup,
    	Nml.BidirectionalPort,
    	Nml.Link,
    	Nml.LinkGroup,
    	Nml.BidirectionalLink,
    	Nml.SwitchingService,
    	};
    public static Property[] selectPredicates = {
    	Nml.hasNode,
    	Nml.hasPort,
    	Nml.hasBidirectionalPort,
    	Nml.hasLink,
    	Nml.hasBidirectionalLink,
    	Nml.isAlias,
    	Nml.belongsTo
    	};
    public static void main(String[] args) {
        // load instance model
        if (args.length > 0) {
        	instanceFileName = args[0];
        }
        Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        InputStream in = FileManager.get().open( instanceFileName );
        if (in == null) {
            throw new IllegalArgumentException("Instance file: " + instanceFileName + " not found");
         }
        model.read(in, null, "TURTLE");
        
        OntModel modelReduced = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        StmtIterator sIt = model.listStatements();
        while (sIt.hasNext()) {
        	Statement stmt = sIt.nextStatement();
        	if (isSelectedPredicate(stmt.getPredicate()) && isSelectedSubject(stmt.getSubject())) {
                modelReduced.add(stmt);
        	}
        }
        modelReduced.setNsPrefix("nml", "http://schemas.ogf.org/nml/2013/03/base#");
        modelReduced.setNsPrefix("mrs", "http://schemas.ogf.org/mrs/2013/12/topology#");
        modelReduced.write(System.out, "RDF/XML-ABBREV");
    }

    private static boolean isSelectedSubject(Resource res) {
    	for (Resource r: selectSubjects) {
    		StmtIterator smtIt = res.listProperties(RDF.type);
    		while (smtIt.hasNext()) {
    			Statement stmt = smtIt.nextStatement();
    			if (stmt.getObject().toString().equals(r.getURI())) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

    private static boolean isSelectedPredicate(Property pre) {
    	for (Property p: selectPredicates) {
    		if (p.equals(pre)) {
    			return true;
    		}
    	}
    	return false;
    }
}
