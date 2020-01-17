package repositories.xml;

import domain.Student;
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

public class StudentXMLFileRepository extends AbstracBaseRepository<String, Student> {
    private String fileName;

    public StudentXMLFileRepository(AbstractValidator<Student> abstractValidator, String fileName) {
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
                Node studentElement = children.item(i);
                if(studentElement instanceof Element){
                    Student student = createStudentFromElement((Element) studentElement);
                    super.save(student);
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private Student createStudentFromElement(Element studentElement) {
        String id = studentElement.getAttribute("id");
        NodeList nodes = studentElement.getChildNodes();

        String nume = studentElement.getElementsByTagName("nume")
                .item(0)
                .getTextContent();

        String prenume = studentElement.getElementsByTagName("prenume")
                .item(0)
                .getTextContent();

        String grupa = studentElement.getElementsByTagName("grupa")
                .item(0)
                .getTextContent();

        String email = studentElement.getElementsByTagName("email")
                .item(0)
                .getTextContent();

        String cadru = studentElement.getElementsByTagName("cadru")
                .item(0)
                .getTextContent();

        Student s = new Student(id,nume,prenume, Integer.parseInt(grupa),email,cadru);
        return s;
    }

    public void writeToFile() {
        try {

            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element root = document.createElement("students");
            document.appendChild(root);
            super.findAll().forEach(s -> {
                Element e = createElementFromStudent(document, s);
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

    private Element createElementFromStudent(Document document, Student student){
        Element element = document.createElement("student");

        element.setAttribute("id",student.getId().toString());

        Element name = document.createElement("nume");
        name.setTextContent(student.getNume());
        element.appendChild(name);

        Element surname = document.createElement("prenume");
        surname.setTextContent(student.getPrenume());
        element.appendChild(surname);

        Element grupa = document.createElement("grupa");
        grupa.setTextContent(((Integer) student.getGrupa()).toString());//grupa.setTextContent(Integer.toString(student.getGrupa())); - echivalent
        element.appendChild(grupa);

        Element email = document.createElement("email");
        email.setTextContent(student.getEmail());
        element.appendChild(email);

        Element cadru = document.createElement("cadru");
        cadru.setTextContent(student.getCadruDidacticIndrumatorLab());
        element.appendChild(cadru);

        return element;
    }


    @Override
    public Student save(Student entity) throws ValidationException {
        Student ret = super.save(entity);
        this.writeToFile();
        return ret;
    }

    @Override
    public Student delete(String s) {
        Student ret = super.delete(s);
        this.writeToFile();
        return ret;
    }

    @Override
    public Student update(Student entity) {
        Student ret = super.update(entity);
        this.writeToFile();
        return ret;
    }

}
