package org.palladiosimulator.addon.slingshot.debuggereventsystems.tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlPrinter<T> {

	private final File file;
	private final FileWriter writer;
	private final Node<T> tree;
	private final Consumer<DocElementNode<T>> dataMarshaller;
	
	public XmlPrinter(final File file, final Node<T> tree, final Consumer<DocElementNode<T>> dataMarshaller) throws IOException {
		this.file = file;
		this.writer = new FileWriter(file);
		this.tree = tree;
		this.dataMarshaller = dataMarshaller;
	}
	
	public XmlPrinter(final File file, final Node<T> tree) throws IOException {
		this(file, tree, null);
	}
	
	public void writeXml() throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final DOMImplementation impl = builder.getDOMImplementation();
		
		final Document doc = impl.createDocument(null, null, null);
		final Element root = doc.createElement("events");
		
		tree.forEach(tree.breadthFirstIterator(), node -> createElement(doc, root, node));
		
		doc.appendChild(root);
		
		final DOMSource domSource = new DOMSource(doc);
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		final StreamResult sr = new StreamResult(writer);
		transformer.transform(domSource, sr);
	}
	
	private Element createElement(final Document document, final Element rootElem, final Node<T> node) {
		final Element el = document.createElement("event");
		el.setAttribute("id", node.getId());
		
		if (dataMarshaller != null) {
			dataMarshaller.accept(new DocElementNode<>(document, el, node));
		}
			
		for (final Node<T> child : node.getChildren()) {
			final Element nextEl = document.createElement("next");
			nextEl.setAttribute("event", child.getId());
			el.appendChild(nextEl);
		}
		
		rootElem.appendChild(el);
		return el;
	}
	
	public static record DocElementNode<T>(Document doc, Element rootElement, Node<T> node) {}
}
