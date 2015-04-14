package net.maxgigapop.www.rains.ontmodel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

public class modelSplitterTest1 {
    public static String instanceFileName = "src/main/resources/genericInfModel.ttl";
    public static String sparqlString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
    		+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
    		+ "PREFIX nml: <http://schemas.ogf.org/nml/2013/03/base#>"
    		+ "PREFIX mrs: <http://schemas.ogf.org/mrs/2013/12/topology#>";
    
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
        List<RDFNode> listTopo = getTopologyList(model);
        if (listTopo == null) {
        	throw new RuntimeException("getTopologyList returns null!");
        }
        for (RDFNode res: listTopo) {
        	OntModel modelTopology = getTopology(model, res);
        	System.out.println("### Dump Sub-Topology "+res.toString());
        	model.remove(modelTopology);
        	modelTopology.setNsPrefix("nml", "http://schemas.ogf.org/nml/2013/03/base#");
        	modelTopology.setNsPrefix("mrs", "http://schemas.ogf.org/mrs/2013/12/topology#");
        	modelTopology.write(System.out, "TURTLE");
        }
    	System.out.println("### Remaining Topology");
    	model.setNsPrefix("nml", "http://schemas.ogf.org/nml/2013/03/base#");
    	model.setNsPrefix("mrs", "http://schemas.ogf.org/mrs/2013/12/topology#");
    	model.write(System.out, "TURTLE");
    }

    //alt:  StmtIterator stmts = model.listStatements( null, RDF.type, model.getResource( "http://schemas.ogf.org/nml/2013/03/base#Topology" ));
    private static List<RDFNode> getTopologyList(Model model) {
        sparqlString += "SELECT ?topology " +
        		"WHERE {?topology a nml:Topology}";
        Query query = QueryFactory.create(sparqlString);
        List<RDFNode> listRes = null;
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet r = (ResultSet) qexec.execSelect();
        while(r.hasNext()) {
            QuerySolution querySolution = r.next();
            RDFNode node = querySolution.get("topology");
            if (listRes == null)
            	listRes = new ArrayList<RDFNode>();
            listRes.add(node);            
         }
		return listRes;
    }

    private static OntModel getTopology(Model model, RDFNode node) {
        OntModel subModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        Set<RDFNode> visited = new HashSet<RDFNode>();
        rdfDFS(node, visited, subModel);
        return subModel;
    }
    
    public static void rdfDFS( RDFNode node, Set<RDFNode> visited, OntModel ontModel) {
        if ( visited.contains( node )) {
            return;
        }
        else {
            visited.add( node );
            if ( node.isResource() ) {
                StmtIterator stmts = node.asResource().listProperties();
                while ( stmts.hasNext() ) {
                    Statement stmt = stmts.next();
                    ontModel.add(stmt);
                    //stmt.remove();
                    if (!stmt.getPredicate().toString().contains("#isAlias")) {
                    	rdfDFS( stmt.getObject(), visited, ontModel);
                    }
                }
            }
        }
    }

}
