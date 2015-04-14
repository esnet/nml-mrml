/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.maxgigapop.www.rains.experiment;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.*;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.util.*;

/**
 *
 * @author xyang
 */
public class AwsTest1 {
    public static void main(String[] args) {
        try {
            // marshall contractXml
            RunInstancesRequest runInstance = new RunInstancesRequest();
            runInstance.withImageId("ami-146e2a7c")
                    .withInstanceType("t2.micro")
                    .withMaxCount(1)
                    .withMinCount(1);
            List<InstanceNetworkInterfaceSpecification> networkInterfaceSpecifications = new ArrayList();
            runInstance.withNetworkInterfaces(networkInterfaceSpecifications);
            runInstance.setGeneralProgressListener(null);
            runInstance.setRequestCredentials(null);
            String xml = new JAXBHelper<RunInstancesRequest>(RunInstancesRequest.class).partialMarshal(runInstance, new QName("http://maxgigapop.net/versastack/aws/api/", "RunInstancesRequest"));
            System.out.println("RunInstancesRequest mashaled:\n" + xml);
        } catch (JAXBException ex) {
            System.err.println(ex.toString());
        }
    }
}
