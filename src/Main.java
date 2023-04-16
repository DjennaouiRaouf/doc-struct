
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class Main {

  static ArrayList<String> res;
  static ArrayList<String> xml;


  static void get_Files(Path path, ArrayList<String> L) throws IOException {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
      for (Path entry : stream) {
        if (Files.isDirectory(entry)) {
          get_Files(entry, L);
        }
        if (Files.isRegularFile(entry)) {
          L.add(entry.toString());
        }
      }
    }
  }


  public static boolean check_str(String str) {


    for (int i = 0; i < str.length(); i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }

    return false;

  }


  public static String[] splitPath(String name) {
    Path path = Paths.get(name);
    return StreamSupport.stream(path.spliterator(), false).map(Path::toString)
        .toArray(String[]::new);
  }


  public static boolean check(String input) {
    File file = new File(input);
    if (file.isDirectory()) {
      return true;
    } else {
      return false;
    }

  }


  public static void modifier(Node node, Document doc, String gram_dtd, int j, String d_t_d,
                              String name, Element root, Element first_elt, String sortie)
      throws TransformerException, ParserConfigurationException, FileNotFoundException {

    if (j == 1) {

      String str1 = node.getNodeName() + "_S";
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parseur = factory.newDocumentBuilder();
      DOMImplementation domimp = parseur.getDOMImplementation();
      DocumentType dtd = domimp.createDocumentType(str1, null, "");
      doc = domimp.createDocument(null, str1, dtd);
      root = doc.getDocumentElement();
      first_elt = doc.createElement(name);
      root.appendChild(first_elt);
      j--;
    } else {

      if (node == null) {
        return;
      }
      switch (node.getNodeName()) {
        case "p": {
          if (node.getFirstChild() != null) {
            String str2 = node.getFirstChild().getNodeValue();
            if (check_str(str2) == true) {
              Element next_elt = doc.createElement("texte");
              next_elt.appendChild(doc.createTextNode(str2));
              first_elt.appendChild(next_elt);
            }
          }
        }

        break;

        case "lb":
          if (node.getNextSibling() != null) {
            String str3 = node.getNextSibling().getNodeValue();
            if (check_str(str3) == true) {
              str3 = str3.replaceAll("[\n]+", "");
              Element next_elt = doc.createElement("texte");
              next_elt.appendChild(doc.createTextNode(str3));
              first_elt.appendChild(next_elt);
            }
          }
          break;
        case "hi":
          if (node.getNextSibling() != null) {
            String str3 = node.getNextSibling().getNodeValue();
            if (check_str(str3) == true) {
              str3 = str3.replaceAll("[\n]+", "");
              Element next_elt = doc.createElement("texte");
              next_elt.appendChild(doc.createTextNode(str3));
              first_elt.appendChild(next_elt);
            }
          }
          break;

        case "pb": {
          if (node.hasAttributes()) {
            NamedNodeMap atts = node.getAttributes();
            for (int e = 0; e < atts.getLength(); e++) {

              Attr attr = (Attr) atts.item(e);
              String var = attr.getName();
              String val = attr.getValue();
              if (!val.equals("209")) {
                if (node.getNextSibling() != null) {
                  String str3 = node.getNextSibling().getNodeValue();
                  if (check_str(str3) == true) {
                    str3 = str3.replaceAll("[\n]+", "");
                    Element next_elt = doc.createElement("texte");
                    next_elt.appendChild(doc.createTextNode(str3));
                    first_elt.appendChild(next_elt);
                  }

                }

              }
            }
          }
        }
        break;


      }


    }


    NodeList nodeList = node.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node currentNode = nodeList.item(i);
      if (currentNode.getNodeName() != "#text") {
        modifier(currentNode, doc, gram_dtd, j, d_t_d, name, root, first_elt, sortie);
      }

    }

    doc.setXmlStandalone(true);
    DOMSource ds = new DOMSource(doc);
    StreamResult res = new StreamResult(new File(sortie));
    TransformerFactory transform = TransformerFactory.newInstance();
    Transformer tr = transform.newTransformer();
    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tr.setOutputProperty(OutputKeys.INDENT, "yes");
    tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
        "dom.dtd");

    tr.transform(ds, res);

  }


  public static void text_to_xml_1(String input_file, Document doc, String name)
      throws ParserConfigurationException, TransformerException, IOException {

    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(input_file));
      String line = reader.readLine();
      int id = 0;
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parseur = factory.newDocumentBuilder();
      DOMImplementation domimp = parseur.getDOMImplementation();
      DocumentType dtd = domimp.createDocumentType(name, null, null);
      doc = domimp.createDocument(null, name, dtd);
      Element root = doc.getDocumentElement();
      Element first_elt = null;
      Element nxt_elt = null;
      Node node =
          doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"fiche.xsl\"");
      doc.insertBefore(node, root);

      while (line != null) {


        if (line.endsWith("BE")) {
          id++;
          String str_id = Integer.toString(id);
          first_elt = doc.createElement(name.substring(0, name.length() - 1));
          first_elt.setAttribute("id", str_id);
          String str = line.substring(0, line.length() - 2);

          root.appendChild(first_elt);

          nxt_elt = doc.createElement("BE");
          nxt_elt.appendChild(doc.createTextNode(str));
          first_elt.appendChild(nxt_elt);

        } else {
          String tmp = line;
          if (Stream.of("FR", "AR").anyMatch(s -> tmp.startsWith(s))) {
            line = line.replaceAll(" ", "");
            nxt_elt = doc.createElement("Langue");
            nxt_elt.setAttribute("id", line);
            first_elt.appendChild(nxt_elt);

            Element elt = doc.createElement("DO");
            elt.appendChild(doc.createTextNode("DO : Médecine & Orthophonie	"));
            nxt_elt.appendChild(elt);
            elt = doc.createElement("SD");
            elt.appendChild(doc.createTextNode("SD : ORL; Audiologie; Surdité	"));
            nxt_elt.appendChild(elt);


          } else {
            if (Stream.of("TY", "AU").anyMatch(s -> tmp.endsWith(s))) {
              String str = line.substring(0, line.length() - 2);
              String str2 = line.substring(line.length() - 2, line.length());
              nxt_elt = doc.createElement(str2);
              nxt_elt.appendChild(doc.createTextNode(str2 + " : " + str));
              first_elt.appendChild(nxt_elt);
            } else {
              if (!line.isEmpty()) {

                String regex = "p.\\s*\\d+|\\d+\\s*p.|ص\\s*\\d+|\\«.+\\»|\\“.+\\”";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                  String regex_1 = "(RF|NT|DF|VE|PH)\\s*:";
                  Pattern pattern_1 = Pattern.compile(regex_1);
                  Matcher matcher_1 = pattern_1.matcher(line);
                  ArrayList<String> array = new ArrayList<String>();
                  while (matcher_1.find()) {
                    line = line.replaceAll(matcher_1.group(1) + "\\s*:", "");
                    array.add(matcher_1.group(1) + " : ");

                  }

                  Collections.reverse(array);
                  if (!array.get(0).startsWith("RF") && array.size() == 1) {
                    array.add(0, "RF | ");
                  }
                  if (array.get(0).startsWith("RF") && array.size() >= 1) {
                    array.remove(0);
                    array.add(0, "RF | ");
                  }

                  String str = "";
                  for (String i : array) {
                    str = str + i;
                  }


                  Element nxt_elt_1 = doc.createElement("RF");
                  nxt_elt_1.appendChild(doc.createTextNode(str + line));
                  nxt_elt.appendChild(nxt_elt_1);

                } else {

                  String regex_1 = "(RF|NT|DF|VE|PH)\\s*:";
                  Pattern pattern_1 = Pattern.compile(regex_1);
                  Matcher matcher_1 = pattern_1.matcher(line);
                  if (matcher_1.find()) {
                    line = line.replaceAll(matcher_1.group(1) + "\\s*:\\s*", "");
                    Element nxt_elt_1 = doc.createElement(matcher_1.group(1));
                    nxt_elt_1.appendChild(doc.createTextNode(matcher_1.group(1) + " : " + line));
                    nxt_elt.appendChild(nxt_elt_1);
                  }


                }


              }

            }
          }
        }


        line = reader.readLine();
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }


    doc.setXmlStandalone(true);
    DOMSource ds = new DOMSource(doc);

    TransformerFactory transform = TransformerFactory.newInstance();
    Transformer tr = transform.newTransformer();
    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tr.setOutputProperty(OutputKeys.INDENT, "yes");
    tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "fiches.dtd");
    tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    tr.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");

    Writer out =
        new OutputStreamWriter(new FileOutputStream("fiches1.xml"), StandardCharsets.UTF_8);
    out.write('\ufeff');
    StreamResult res = new StreamResult(out);
    tr.transform(ds, res);

    Path path = Paths.get("fiches1.xml");
    Charset charset = StandardCharsets.UTF_8;
    String content = new String(Files.readAllBytes(path), charset);
    content = content.replaceAll("><", ">\r\n<");
    content = space_to_tab(content);
    Files.write(path, content.getBytes(charset));
    ajuster("fiches1.xml");


  }


  public static void ajuster(String file) throws IOException {
    ArrayList<String> array = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line = reader.readLine();
    while (line != null) {
      array.add(line);
      line = reader.readLine();
    }

    String tmp = array.get(1);
    array.set(1, array.get(2));
    array.set(2, tmp);
    try (FileWriter writer = new FileWriter(file);
         BufferedWriter bw = new BufferedWriter(writer)) {
      for (int i = 0; i < array.size(); i++) {
        bw.write(array.get(i) + "\r\n");

      }


    } catch (IOException e) {
      System.out.print("");
    }

    array.clear();

  }


  public static void text_to_xml_2(String input_file, Document doc, String name)
      throws ParserConfigurationException, TransformerException, IOException {

    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(input_file));
      String line = reader.readLine();
      int id = 0;
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parseur = factory.newDocumentBuilder();
      DOMImplementation domimp = parseur.getDOMImplementation();
      DocumentType dtd = domimp.createDocumentType(name, null, null);
      doc = domimp.createDocument(null, name, dtd);
      Element root = doc.getDocumentElement();
      Element first_elt = null;
      Element nxt_elt = null;
      Node node =
          doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"fiche.xsl\"");
      doc.insertBefore(node, root);
      while (line != null) {


        if (line.endsWith("BE")) {
          id++;
          String str_id = Integer.toString(id);
          first_elt = doc.createElement(name.substring(0, name.length() - 1));
          first_elt.setAttribute("id", str_id);
          String str = line.substring(0, line.length() - 2);

          root.appendChild(first_elt);

          nxt_elt = doc.createElement("BE");
          nxt_elt.appendChild(doc.createTextNode(str));
          first_elt.appendChild(nxt_elt);

        } else {
          String tmp = line;
          if (Stream.of("FR", "AR").anyMatch(s -> tmp.startsWith(s))) {
            line = line.replaceAll(" ", "");
            nxt_elt = doc.createElement("Langue");
            nxt_elt.setAttribute("id", line);
            first_elt.appendChild(nxt_elt);


          } else {
            if (Stream.of("TY", "DO", "SD", "AU").anyMatch(s -> tmp.endsWith(s))) {
              String str = line.substring(0, line.length() - 2);
              String str2 = line.substring(line.length() - 2, line.length());
              nxt_elt = doc.createElement(str2);
              nxt_elt.appendChild(doc.createTextNode(str2 + " : " + str));
              first_elt.appendChild(nxt_elt);
            } else {
              if (!line.isEmpty()) {

                String regex = "p.\\s*\\d+|\\d+\\s*p.|ص\\s*\\d+|\\«.+\\»|\\“.+\\”";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                  String regex_1 = "(RF|NT|DF|VE|PH)\\s*:";
                  Pattern pattern_1 = Pattern.compile(regex_1);
                  Matcher matcher_1 = pattern_1.matcher(line);
                  ArrayList<String> array = new ArrayList<String>();
                  while (matcher_1.find()) {
                    line = line.replaceAll(matcher_1.group(1) + "\\s*:", "");
                    array.add(matcher_1.group(1) + " : ");

                  }

                  Collections.reverse(array);
                  if (!array.get(0).startsWith("RF") && array.size() == 1) {
                    array.add(0, "RF | ");
                  }
                  if (array.get(0).startsWith("RF") && array.size() >= 1) {
                    array.remove(0);
                    array.add(0, "RF | ");
                  }

                  String str = "";
                  for (String i : array) {
                    str = str + i;
                  }


                  Element nxt_elt_1 = doc.createElement("RF");
                  nxt_elt_1.appendChild(doc.createTextNode(str + line));
                  nxt_elt.appendChild(nxt_elt_1);

                } else {

                  String regex_1 = "(RF|NT|DF|VE|PH)\\s*:";
                  Pattern pattern_1 = Pattern.compile(regex_1);
                  Matcher matcher_1 = pattern_1.matcher(line);
                  if (matcher_1.find()) {
                    line = line.replaceAll(matcher_1.group(1) + "\\s*:\\s*", "");
                    Element nxt_elt_1 = doc.createElement(matcher_1.group(1));
                    nxt_elt_1.appendChild(doc.createTextNode(matcher_1.group(1) + " : " + line));
                    nxt_elt.appendChild(nxt_elt_1);
                  }


                }


              }

            }
          }
        }


        line = reader.readLine();
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }


    doc.setXmlStandalone(true);
    DOMSource ds = new DOMSource(doc);
    TransformerFactory transform = TransformerFactory.newInstance();
    Transformer tr = transform.newTransformer();
    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tr.setOutputProperty(OutputKeys.INDENT, "yes");
    tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    tr.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
    Writer out =
        new OutputStreamWriter(new FileOutputStream("fiches2.xml"), StandardCharsets.UTF_8);
    out.write('\ufeff');

    StreamResult res = new StreamResult(out);
    tr.transform(ds, res);

    Path path = Paths.get("fiches2.xml");
    Charset charset = StandardCharsets.UTF_8;
    String content = new String(Files.readAllBytes(path), charset);
    content = content.replaceAll("><", ">\r\n<");
    content = space_to_tab(content);
    content = content + "\r\n";
    Files.write(path, content.getBytes(charset));

  }


  public static String space_to_tab(String str) {
    String tab[] = str.split("\r\n");

    for (int i = 0; i < tab.length; i++) {
      String split[] = tab[i].split("<", 2);
      String tmp = "<" + split[1];
      split[0] = split[0].replaceAll("    ", "\t");
      tab[i] = split[0] + tmp;
    }
    String res = String.join("\r\n", tab);
    return res;

  }


  public static void poeme_to_xml(String input_file, Document doc)
      throws ParserConfigurationException, TransformerException {
    BufferedReader reader;

    try {
      reader = new BufferedReader(new FileReader(input_file));
      String line = reader.readLine();

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parseur = factory.newDocumentBuilder();
      DOMImplementation domimp = parseur.getDOMImplementation();
      DocumentType dtd = domimp.createDocumentType("poema", null, null);
      doc = domimp.createDocument(null, "poema", dtd);
      Element root = doc.getDocumentElement();
      Element verso = null;
      Element estrofa = null;


      Element first_elt = doc.createElement("titulo");
      first_elt.appendChild(doc.createTextNode(line));
      root.appendChild(first_elt);

      line = reader.readLine();
      while (line != null) {
        try {
          while (line.equals("")) {
            line = reader.readLine();
          }
          estrofa = doc.createElement("estrofa");
          root.appendChild(estrofa);
          while (!line.equals("")) {
            verso = doc.createElement("verso");
            verso.appendChild(doc.createTextNode(line));
            estrofa.appendChild(verso);
            line = reader.readLine();

          }
          line = reader.readLine();


        } catch (Exception e) {
          System.out.print("");
        }


      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }


    doc.setXmlStandalone(true);
    DOMSource ds = new DOMSource(doc);
    StreamResult res = new StreamResult(new File("neruda.xml"));
    TransformerFactory transform = TransformerFactory.newInstance();
    Transformer tr = transform.newTransformer();

    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tr.setOutputProperty(OutputKeys.INDENT, "yes");
    tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
        "neruda.dtd");
    tr.transform(ds, res);


  }


  public static void fxml_to_xml(Node node, Document doc, int j, Element root)
      throws ParserConfigurationException, TransformerException, IOException {

    if (j == 1) {

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parseur = factory.newDocumentBuilder();
      DOMImplementation domimp = parseur.getDOMImplementation();
      DocumentType dtd = domimp.createDocumentType("Racine", null, "");
      doc = domimp.createDocument(null, "Racine", dtd);
      root = doc.getDocumentElement();
      root.setAttribute("xmlns:fx", "http://javafx.com/fxml");
      j--;
    }

    if (node == null) {
      return;
    }

    if (node.hasAttributes()) {

      NamedNodeMap atts = node.getAttributes();
      for (int e = 0; e < atts.getLength(); e++) {

        Attr attr = (Attr) atts.item(e);
        String var = attr.getName();
        String val = attr.getValue();

        Element elt = doc.createElement("texte");
        elt.setAttribute(var, "x");
        elt.appendChild(doc.createTextNode(val));
        root.appendChild(elt);
      }
    }


    NodeList nodeList = node.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node currentNode = nodeList.item(i);
      if (currentNode.getNodeName() != "#text") {
        fxml_to_xml(currentNode, doc, j, root);
      }

    }

    doc.setXmlStandalone(true);
    DOMSource ds = new DOMSource(doc);
    StreamResult res = new StreamResult(new File("javafx.xml"));
    TransformerFactory transform = TransformerFactory.newInstance();
    Transformer tr = transform.newTransformer();
    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tr.setOutputProperty(OutputKeys.INDENT, "yes");
    tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    tr.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");

    tr.transform(ds, res);

    Path path = Paths.get("javafx.xml");
    Charset charset = StandardCharsets.UTF_8;
    String content = new String(Files.readAllBytes(path), charset);
    content = content.replaceAll("><", ">\n<");
    if (content.endsWith("\r\n")) {
      content = content.substring(0, content.length() - 2);
    }
    content = space_to_tab(content);

    Files.write(path, content.getBytes(charset));

  }


  static void html_to_xml(Node node, Document docres, Document docsrc) throws Exception {

    Element root = null;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parseur = factory.newDocumentBuilder();
    DOMImplementation domimp = parseur.getDOMImplementation();
    factory.setIgnoringElementContentWhitespace(true);
    DocumentType dtd = domimp.createDocumentType("Concessionnaires", null, "");
    docres = domimp.createDocument(null, "Concessionnaires", dtd);
    root = docres.getDocumentElement();

    boolean b = true;
    NodeList liste = docsrc.getElementsByTagName("p");
    for (int i = 0; i < liste.getLength(); i++) {
      Element elt = (Element) liste.item(i);
      NodeList strong = elt.getChildNodes();
      if (strong.getLength() >= 4 && strong.item(1).getNodeName().compareTo("strong") == 0) {
        Element nom = docres.createElement("Nom");
        Element adresse = docres.createElement("Adresse");
        Element tel = docres.createElement("Num_téléphone");

        nom.appendChild(docres.createTextNode(
            strong.item(1).getFirstChild().getNodeValue().replaceAll("[\n\r]", " ").trim()));

        String adr = strong.item(6).getNodeValue();
        if (b) {
          adr = adr.replace(":", "");
        }

        adresse.appendChild(docres.createTextNode(adr.replaceAll("[\n\r]", " ").trim()));

        String num_tel = strong.item(b ? 10 : 8).getNodeValue();
        if (b) {
          num_tel = num_tel.replace(":", "");
        }
        tel.appendChild(docres.createTextNode(num_tel.replaceAll("[\n\r]", " ").trim()));

        root.appendChild(nom);
        root.appendChild(adresse);
        root.appendChild(tel);

        if (b) {
          b = false;
        }
      }
    }

    docres.setXmlStandalone(true);
    DOMSource ds = new DOMSource(docres);
    Writer out =
        new OutputStreamWriter(new FileOutputStream("renault.xml"), StandardCharsets.UTF_8);
    out.write('\ufeff');
    StreamResult res = new StreamResult(out);
    TransformerFactory transform = TransformerFactory.newInstance();
    Transformer tr = transform.newTransformer();
    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tr.setOutputProperty(OutputKeys.INDENT, "yes");
    tr.transform(ds, res);

    Path path = Paths.get("renault.xml");
    Charset charset = StandardCharsets.UTF_8;
    String content = new String(Files.readAllBytes(path), charset);
    content = content.replaceAll("><", ">\n<");
    Files.write(path, content.getBytes(charset));


  }


  public static void main(String[] args) throws Exception {

    System.setProperty("file.encoding", "UTF-8");
    Field charset = Charset.class.getDeclaredField("defaultCharset");
    charset.setAccessible(true);
    charset.set(null, null);

    if (args.length == 0) {
      System.out.println(
          "Attention vous avez oublié de spécifier le nom du répertoire à traiter !");
      return;
    } else {
      System.out.println("traitement du repertoire ...");
      res = new ArrayList<String>();
      get_Files(Paths.get("examen"), res);

      int ind = 1;
      for (int i = 0; i < res.size(); i++) {
        InputSource is = null;
        if (res.get(i).endsWith("M457.xml")) {
          InputStream inputStream = new FileInputStream(res.get(i));
          Reader reader = new InputStreamReader(inputStream, "windows-1252");
          is = new InputSource(reader);
          is.setEncoding("windows-1252");
          DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
          docBuilderFactory.setValidating(false);
          docBuilderFactory.setFeature(
              "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
          DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
          Document document = docBuilder.parse(is);
          Document doc = null;
          modifier(document.getDocumentElement(), doc, "", 1, "dom.dtd", "M457.xml", null, null,
              "sortie2.xml");

          System.out.println(res.get(i));
        }
        if (res.get(i).endsWith("M674.xml")) {
          InputStream inputStream = new FileInputStream(res.get(i));
          Reader reader = new InputStreamReader(inputStream, "UTF-8");
          is = new InputSource(reader);
          is.setEncoding("UTF-8");
          DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
          docBuilderFactory.setValidating(false);
          docBuilderFactory.setFeature(
              "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
          Document document = docBuilder.parse(is);
          Document doc = null;
          modifier(document.getDocumentElement(), doc, "", 1, "dom.dtd", "M674.xml", null, null,
              "sortie1.xml");
          System.out.println(res.get(i));
        }


        if (res.get(i).endsWith("fiches.txt")) {
          Document doc = null;
          text_to_xml_1(res.get(i), doc, "FICHES");
          text_to_xml_2(res.get(i), doc, "FICHES");
          System.out.println(res.get(i));
        }
        if (res.get(i).endsWith("poeme.txt")) {

          Document doc = null;
          poeme_to_xml(res.get(i), doc);
          System.out.println(res.get(i));

        }
        if (res.get(i).endsWith(".fxml")) {

          DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
          docBuilderFactory.setValidating(false);
          docBuilderFactory.setFeature(
              "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
          Document document = docBuilder.parse(new File(res.get(i)));
          Document doc = null;
          fxml_to_xml(document.getDocumentElement(), doc, 1, null);
          System.out.println(res.get(i));

        }
        if (res.get(i).endsWith(".html")) {

          DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
          docBuilderFactory.setValidating(false);
          docBuilderFactory.setFeature(
              "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
          DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
          Document document = docBuilder.parse(new File(res.get(i)));
          Document doc = null;
          html_to_xml(document.getDocumentElement(), doc, document);
          System.out.println(res.get(i));


        }


      }
    }
  }

}



