package repositories;

import domain.Tema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import validators.AbstractValidator;
import validators.ValidationException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;

public class TemaXMLFileRepository extends AbstracBaseRepository<String, Tema> {
    private String fileName;

    public TemaXMLFileRepository(AbstractValidator<Tema> abstractValidator, String fileName) {
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
                Node temaElement = children.item(i);
                if(temaElement instanceof Element){
                    Tema tema = createTemaFromElement((Element) temaElement);
                    super.save(tema);
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private Tema createTemaFromElement(Element temaElement) {
        String id = temaElement.getAttribute("id");
        NodeList nodes = temaElement.getChildNodes();

        String nume = temaElement.getElementsByTagName("nume")
                .item(0)
                .getTextContent();

        String descriere = temaElement.getElementsByTagName("descriere")
                .item(0)
                .getTextContent();

        String startweek = temaElement.getElementsByTagName("startweek")
                .item(0)
                .getTextContent();

        String deadlineweek = temaElement.getElementsByTagName("deadlineweek")
                .item(0)
                .getTextContent();

        Tema t = new Tema(id,nume,descriere, startweek,deadlineweek);

        return t;
    }

    public void writeToFile() {
        try {

            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element root = document.createElement("teme");
            document.appendChild(root);
            super.findAll().forEach(t -> {
                Element e = createElementFromTema(document, t);
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

    private Element createElementFromTema(Document document, Tema tema){
        Element element = document.createElement("tema");

        element.setAttribute("id",tema.getId().toString());

        Element name = document.createElement("nume");
        name.setTextContent(tema.getNume());
        element.appendChild(name);

        Element descriere = document.createElement("descriere");
        descriere.setTextContent(tema.getDescriere());
        element.appendChild(descriere);

        Element startWeek = document.createElement("startweek");
        startWeek.setTextContent(tema.getStartWeek());
        element.appendChild(startWeek);

        Element deadlineWeek = document.createElement("deadlineweek");
        deadlineWeek.setTextContent(tema.getDeadlineWeek());
        element.appendChild(deadlineWeek);

        return element;
    }


    @Override
    public Tema save(Tema entity) throws ValidationException {
        Tema ret = super.save(entity);
        this.writeToFile();
        return ret;
    }

    @Override
    public Tema delete(String s) {
        Tema ret = super.delete(s);
        this.writeToFile();
        return ret;
    }

    @Override
    public Tema update(Tema entity) {
        Tema ret = super.update(entity);
        this.writeToFile();
        return ret;
    }
}