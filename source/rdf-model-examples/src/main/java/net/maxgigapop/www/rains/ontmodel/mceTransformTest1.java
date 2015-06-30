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
import com.hp.hpl.jena.ontology.OntTools;
import com.hp.hpl.jena.ontology.OntTools.Path;
import static com.hp.hpl.jena.ontology.OntTools.findShortestPath;
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
import com.hp.hpl.jena.util.iterator.Filter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.System.exit;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 */
public final class mceTransformTest1 {
    public static final String nmlSchemaFileName = "src/main/resources/nml-base-ext.owl";
    public static final String mrsSchemaFileName = "src/main/resources/nml-mrs-ext.owl";
    public static final String instanceFileName = "src/main/resources/testNetwork1.ttl";
    public static final String sparsqlQueryFile = "src/main/resources/sparqlConstruct1.txt";
    public static final String reasonerRulesFile = "src/main/resources/reasonerRules1.txt";
    public static String sparqlString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
    		+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
    		+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
    		+ "PREFIX nml: <http://schemas.ogf.org/nml/2013/03/base#>"
    		+ "PREFIX mrs: <http://schemas.ogf.org/mrs/2013/12/topology#>";
    
    static final Filter<Statement> ANY = Filter.any();

    public static void main(String[] args) {
        String sparqlString = null;
        String rulesString = null;
        try {
            sparqlString = readFile(sparsqlQueryFile);
            rulesString = readFile(reasonerRulesFile);
        } catch (IOException ex) {
            Logger.getLogger(mceTransformTest1.class.getName()).log(Level.SEVERE, null, ex);
            exit(1);
        }
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
        Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        InputStream in2 = FileManager.get().open( instanceFileName );
        if (in == null) {
            throw new IllegalArgumentException("Instance file: " + instanceFileName + " not found");
         }
        model.read(in2, null, "TURTLE");
        Query query = QueryFactory.create(sparqlString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Model modelConstructed = qexec.execConstruct();
        modelConstructed.write(System.out, "TURTLE");

        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rulesString));
        reasoner.setDerivationLogging(true);
        InfModel infModel = ModelFactory.createInfModel(reasoner, modelConstructed);   
        //infModel.write(System.out, "TURTLE");
        Resource nodeA = infModel.getResource("urn:ogf:network:domain=dragon.maxgigapop.net:node=CLPK:port=1-2-3:link=*");
        Resource nodeZ = infModel.getResource("urn:ogf:network:domain=dragon.maxgigapop.net:node=MCLN:port=1-3-1:link=*");
        //Path ontPath = findShortestPath(infModel, nodeA, nodeZ, ANY);
        Model deInfModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        deInfModel.add(infModel);
        List<Path> ksp = computeKShortestPaths(deInfModel, nodeA, nodeZ, 10);
    }

    private static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }

    private static List<Path> computeKShortestPaths(Model model, Resource nodeA, Resource nodeZ, int K) {
        final Filter<Statement> ANY = Filter.any();
        final HashMap<Path, Resource> deviationMap = new HashMap<Path, Resource>();
        final HashSet<Statement> maskedLinks = new HashSet<Statement>();
        final HashMap<Path, HashSet<Statement>> pathMaskMap = new HashMap<Path, HashSet<Statement>>();
        List<Path> KSP = new ArrayList<Path>();
        List<Path> candidatePaths = new ArrayList<Path>();
        // find the first shortest path
        Path nextPath = findShortestPath(model, nodeA, nodeZ, ANY);
        if (nextPath == null)
            return null;
        KSP.add(nextPath);
        candidatePaths.add(nextPath);
        int kspCounter = 1;
        while (!candidatePaths.isEmpty() && KSP.size() <= K) {
            Path headPath = getLeastCostPath(candidatePaths);
            candidatePaths.remove(headPath);
            if (kspCounter > 1) 
                KSP.add(headPath);
            if (kspCounter == K) 
                break;
            for (Statement stmtLink: headPath) {
                if (deviationMap.containsKey(headPath)) {
                    if (stmtLink.getSubject().equals(deviationMap.get(headPath)))
                        break;
                }
                // mask (filter out) all statments to and from the localEnd of link
                StmtIterator itStmt = model.listStatements(stmtLink.getSubject(), null, (Resource)null);
                if (itStmt.hasNext()) {
                    Statement linkToMask = itStmt.next();
                    maskedLinks.add(linkToMask);
                    model = model.remove(linkToMask);
                }
                itStmt = model.listStatements(null, null, stmtLink.getSubject());
                if (itStmt.hasNext()) {
                    Statement linkToMask = itStmt.next();
                    maskedLinks.add(linkToMask);
                    model = model.remove(linkToMask);
                }
            }
            for (Statement stmtLink: headPath) {
                // filter out masked links in headPath 
                if (pathMaskMap.containsKey(headPath)) {
                    maskedLinks.addAll(pathMaskMap.get(headPath));
                    for (Statement stmtMaskedLink: pathMaskMap.get(headPath))
                        model = model.remove(stmtMaskedLink);
                }
                // mask current link
                model = model.remove(stmtLink);
                maskedLinks.add(stmtLink);
                Path deviationPath = findShortestPath(model, stmtLink.getSubject(), headPath.getTerminalResource(), ANY);
                nextPath = new Path();
                if (deviationPath != null) {
                    if (!stmtLink.equals(headPath.get(0))) {
                        // add headPath[begin : stmtLink) to nextPath
                        for (int i = 0; i < headPath.size(); i++) {
                            if (headPath.get(i).equals(stmtLink))
                                break;
                            nextPath.add(headPath.get(i));
                        }
                    }
                    // keep record for deviation node of nextPath
                    deviationMap.put(nextPath, stmtLink.getSubject());
                    // append deviationPath to nextPath 
                    nextPath.addAll(deviationPath);
                    // adjust masking for nextPath
                    if (!pathMaskMap.containsKey(nextPath)) {
                        pathMaskMap.put(nextPath, new HashSet<Statement>());
                    }
                    if (pathMaskMap.containsKey(headPath)) {
                        pathMaskMap.get(nextPath).addAll(pathMaskMap.get(headPath));
                    }
                    pathMaskMap.get(nextPath).add(stmtLink);
                    // add another candiate path
                    candidatePaths.add(nextPath);
                }
                //?? add back current link?
                //model.add(stmtLink);
                //maskedLinks.remove(stmtLink);
            }
            kspCounter++;
        }
        model.write(System.out, "TURTLE");
        for (Statement stmtMaskedLink: maskedLinks)
            model.add(stmtMaskedLink);
        model.write(System.out, "TURTLE");
        return KSP;
    }
    
    private static Path getLeastCostPath(List<Path> candidates) {
        long cost = 1000000;
        Path solution = null;
        for (Path path: candidates) {
            if (path.size() < cost) {
                cost = path.size();
                solution = path;
            }
        }
        return solution;
    }
}
