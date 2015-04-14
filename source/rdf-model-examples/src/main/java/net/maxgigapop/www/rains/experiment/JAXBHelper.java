/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.maxgigapop.www.rains.experiment;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import java.io.StringWriter;
import java.io.StringReader;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;


public class JAXBHelper<T> {
 
    private JAXBContext jaxbContext;
    private Class<T> classToBind;
 
    public JAXBHelper(Class<T> classToBind) {
        this.classToBind = classToBind;
        try {
            jaxbContext = JAXBContext.newInstance(classToBind);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
 
    @SuppressWarnings("unchecked")
    public T unmarshal(String xmlContent) throws JAXBException {
        return (T) jaxbContext.createUnmarshaller().unmarshal(
                new StringReader(xmlContent));
    }
 
    public String marshal(T jaxbObject) throws JAXBException {
        StringWriter sw = new StringWriter();
 
        jaxbContext.createMarshaller().marshal(jaxbObject, sw);
 
        return sw.toString();
    }
 
    public String partialMarshal(T jaxbObject, QName rootElementName) throws JAXBException {
        Marshaller m = jaxbContext.createMarshaller(); 
        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
 
        StringWriter sw = new StringWriter();
        m.marshal(new JAXBElement<T>(
                rootElementName, classToBind, jaxbObject), sw);
 
        return sw.toString();
    }
 
    public T partialUnmarshal(String xmlContent) throws JAXBException {
        Node node = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(
                    new InputSource(new StringReader(xmlContent)));
            node = doc.getDocumentElement();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
 
        JAXBElement<T> jaxbElement = (JAXBElement<T>) 
            jaxbContext.createUnmarshaller().unmarshal(node, classToBind);
 
        return jaxbElement.getValue();
    }
}
