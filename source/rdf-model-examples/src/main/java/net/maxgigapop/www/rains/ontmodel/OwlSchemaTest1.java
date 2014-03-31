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
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.reasoner.ValidityReport;

/**
 *
 */
public final class OwlSchemaTest1 {
    public static final String nmlSchemaFileName = "src/main/resources/nml-base-ext.owl";
    public static final String mrsSchemaFileName = "src/main/resources/nml-mrs-ext.owl";
    public static String instanceFileName = "src/main/resources/max3.ttl";
    public static final String UNION_GRAPH = "urn:x-arq:UnionGraph";

    public static void main(String[] args) {
    	// load MRS schema
    	OntModel schema = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        InputStream in = FileManager.get().open( mrsSchemaFileName );
        if (in == null) {
           throw new IllegalArgumentException("Schema file: " + mrsSchemaFileName + " not found");
        }
        schema.read(in, null);
    	// load NML schema
    	OntModel schema2 = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        in = FileManager.get().open( nmlSchemaFileName );
        if (in == null) {
           throw new IllegalArgumentException("Schema file: " + nmlSchemaFileName + " not found");
        }
        schema2.read(in, null);
        schema.add(schema2);
        // bind reasoner
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(schema);
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
        // create instance inference model using the schema model
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);
        ValidityReport valid = infModel.validate();
        if (!valid.isClean() || !valid.isValid()) {
        	Iterator<Report> itr = valid.getReports();
        	while (itr.hasNext()) {
        		System.out.println("model invalid:" + itr.next().getDescription());
        	}
        }
        // output
        infModel.setNsPrefix("nml", "http://schemas.ogf.org/nml/2013/03/base#");
        infModel.setNsPrefix("mrs", "http://schemas.ogf.org/mrs/2013/12/topology#");
        System.out.println(">>>InfModel");
        //infModel.write(System.out, "RDF/XML-ABBREV");
        infModel.write(System.out, "TURTLE");
        System.out.println("<<<InfModel");
    }
}
