package repositories.xml;


import domain.Nota;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repositories.AbstracBaseRepository;
import validators.AbstractValidator;
import validators.ValidationException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;

public class NotaXMLFileRepository extends AbstracBaseRepository<String, Nota> {
    private String fileName;

    public NotaXMLFileRepository(AbstractValidator<Nota> abstractValidator, String fileName) {
        super(abstractValidator);
        this.fileName = fileName;
        loadData();
    }

    private void loadData(){
        try{
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(this.fileName);

            Element root = document.getDocumentElement();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node notaElement = children.item(i);
                if(notaElement instanceof Element){
                    Nota nota = createNotaFromElement((Element) notaElement);
                    super.save(nota);
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private Nota createNotaFromElement(Element notaElement) {
        String id = notaElement.getAttribute("id");
        NodeList nodes = notaElement.getChildNodes();

        String data = notaElement.getElementsByTagName("data")
                .item(0)
                .getTextContent();

        String valoare = notaElement.getElementsByTagName("valoare")
                .item(0)
                .getTextContent();

        String profesor = notaElement.getElementsByTagName("profesor")
                .item(0)
                .getTextContent();

        String feedback = notaElement.getElementsByTagName("feedback")
                .item(0)
                .getTextContent();

        Nota n = new Nota(id, Integer.parseInt(valoare), profesor, data, feedback);

        return n;
    }

    public void writeToFile() {
        try {

            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element root = document.createElement("note");
            document.appendChild(root);
            super.findAll().forEach(n -> {
                Element e = createElementFromNota(document, n);
                root.appendChild(e);
            });

            //write Document to file
            Transformer transformer = TransformerFactory
                    .newInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Source source = new DOMSource(document);

            transformer.transform(source,
                    new StreamResult(fileName));

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private Element createElementFromNota(Document document, Nota nota){
        Element element = document.createElement("student");

        element.setAttribute("id",nota.getId().toString());

        Element data = document.createElement("data");
        data.setTextContent(nota.getData());
        element.appendChild(data);

        Element valoare = document.createElement("valoare");
        valoare.setTextContent(((Integer) nota.getValoare()).toString());
        element.appendChild(valoare);

        Element prof = document.createElement("profesor");
        prof.setTextContent(nota.getProfesor());
        element.appendChild(prof);

        Element feedback = document.createElement("feedback");
        feedback.setTextContent(nota.getFeedback());
        element.appendChild(feedback);

        return element;
    }

    @Override
    public Nota save(Nota entity) throws ValidationException {
        Nota found = super.findOne(entity.getId());
        if(found == null){
            Nota ret = super.save(entity);
            this.writeToFile();
            return ret;
        }
        else{
            throw new ValidationException("DUPLICATE VALUES CANNOT EXIST FOR GRADES!!!");
        }
    }

    @Override
    public Nota delete(String s) {
        Nota ret = super.delete(s);
        this.writeToFile();
        return ret;
    }

    @Override
    public Nota update(Nota entity) {
        Nota ret = super.update(entity);
        this.writeToFile();
        return ret;
    }
}
