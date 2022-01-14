import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "src/data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json,"data.json");
        List<Employee> listXML = parseXML("data.xml");
        String json2 = listToJson(listXML);
        writeString(json2,"data2.json");
        String jsonPars = readString("data.json");
        List<Employee> listJson = jsonToList(jsonPars);
        System.out.println(listJson);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String filename) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> emplList = new ArrayList<Employee>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse (new File(filename));
        Node root = doc.getDocumentElement();
        NodeList nodeList = doc.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            emplList.add(getEmployee(nodeList.item(i)));
        }
        return emplList;
    }

    public static String readString(String filename) {
        String s = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            s = br.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return s;
    }

    public static List<Employee> jsonToList(String json) {
        Type typeOfObjectsList = new TypeToken<ArrayList<Employee>>() {}.getType();
        List<Employee> emplList = new Gson().fromJson(json, typeOfObjectsList);
        return emplList;
    }

    private static Employee getEmployee(Node node) {
        Employee empl = new Employee();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            empl.setId(Long.parseLong(getTagValue("id", element)));
            empl.setFirstName(getTagValue("firstName", element));
            empl.setLastName(getTagValue("lastName", element));
            empl.setCountry(getTagValue("country", element));
            empl.setAge(Integer.parseInt(getTagValue("age", element)));
        }
        return empl;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

}
