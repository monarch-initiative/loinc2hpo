package org.monarchinitiative.loinc2hpo.loinc;


import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@Ignore
public class LoincPanelTest {
    private static final String loincCoreTable = "/Users/zhangx/Downloads/LOINC_2/LoincTableCore.csv";
    public static final String loincPanels = "/Users/zhangx/Downloads/LOINC_2/Accessory/PanelsAndForms/LOINC_263_PanelsAndForms_Panels.csv";
    private static ImmutableMap<LoincId, LoincEntry> loincEntryMap;
    private static Map<LoincId, LoincPanel> panelMap;

    @BeforeClass
    public static void setUp() throws Exception {
        loincEntryMap = LoincEntry.getLoincEntryList(loincCoreTable);
        panelMap = LoincPanel.getPanels(loincPanels, loincEntryMap);

    }

    @Test
    public void getPanelLoincId() throws Exception {
        LoincId testPanel = new LoincId("24322-0");
        assertNotNull(panelMap.get(testPanel));
        assertEquals("24322-0", panelMap.get(testPanel).getPanelLoincEntry().getLOINC_Number().toString());
    }

    @Test
    public void addChild() throws Exception {
        LoincId testPanel = new LoincId("24322-0");
        LoincPanel panel = panelMap.get(testPanel);
        assertEquals(13, panel.getChidren().size());
        LoincPanelComponent newChild = new LoincPanelComponent(new LoincId("8040-8"), PanelComponentConditionality.O, loincEntryMap);
        panel.addChild(newChild);
        assertEquals(14, panel.getChidren().size());
    }

    @Test
    public void isMappableToHpo() throws Exception {
        LoincId testPanel = new LoincId("24322-0");
        LoincPanel panel = panelMap.get(testPanel);
        assertFalse(panel.isInterpretableInHPO());
        panel.setInterpretableInHPO(true);
        assertTrue(panel.isInterpretableInHPO());
    }

    @Test
    public void getChild() throws Exception {

        LoincId testPanel2 = new LoincId("13361-1");
        LoincPanel panel2 = panelMap.get(testPanel2);
        assertEquals(6, panel2.getChidren().size());

        LoincId testPanel = new LoincId("35094-2");
        LoincPanel panel = panelMap.get(testPanel);
        assertEquals(9, panel.getChidren().size());

    }

    @Test
    public void getChildrenRequiredForMapping() throws Exception {
        LoincId testPanel = new LoincId("35094-2");
        LoincPanel panel = panelMap.get(testPanel);
        //panel.getChidren().stream().forEach(c -> System.out.println(c.getLoincEntry().getLOINC_Number()));
        assertEquals(9, panel.getChidren().size());
        assertEquals(0, panel.getChildrenRequiredForMapping().size());
        panel.setChildMappingConditionality(new LoincId("8480-6"), PanelComponentConditionality.R);
        panel.setChildMappingConditionality(new LoincId("8462-4"), PanelComponentConditionality.R);
        assertEquals(2, panel.getChildrenRequiredForMapping().size());
        //panel.getChildrenRequiredForMapping().stream().map(c -> c.getLoincEntry().getLOINC_Number()).forEach(System.out::println);
    }

    @Test
    public void getPanels() throws Exception {
        //total no. of panels: 3093
        System.out.println("no. of panels: " + panelMap.size());
    }

    @Test
    public void checkIfPanelsAreTopLoincs() throws Exception{
        Set<LoincId> top2000 = new LinkedHashSet<>();

        String top2000Path = "/Users/zhangx/Downloads/LOINC_2/Accessory/Top2000/LOINC_1.6_Top2000CommonLabResultsUS.csv";
        BufferedReader reader = new BufferedReader(new FileReader(top2000Path));
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            top2000.add(new LoincId(line.split(",")[0]));
        }

//        top2000.stream().filter(t -> panelMap.containsKey(t)).forEach(System.out::println);

        //Inerestingly, only the following panels appear in the Top2000 LOINC list.
//        49581-2
//        73967-2
//        75547-0
//        77019-8
//        77018-0
//        70164-9
//        why blood pressure are not in the top?

    }


    private final String panelsAndFormsFile = "/Users/zhangx/Downloads/LOINC_2/Accessory/PanelsAndForms/LOINC_263_PanelsAndForms.xlsx";
    private final String testExcel = "/Users/zhangx/Downloads/LOINC_2/Accessory/PanelsAndForms/LOINC_263_PanelsAndForms_Panels.csv";

    @Test
    @Ignore
    public void testParser() throws Exception {

        //FileInputStream excelFile = new FileInputStream(testExcel);
        //System.out.println("1111");
//        OPCPackage fs = OPCPackage.open(new File(testExcel));
//        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(fs);
//        XSSFReader reader = new XSSFReader(fs);
//        StylesTable styles = reader.getStylesTable();
//        XSSFReader.SheetIterator itr = (XSSFReader.SheetIterator) reader.getSheetsData();
//        InputStream panelSheetStream = itr.next();
//
//        InputSource sheetSource = new InputSource(panelSheetStream);
//        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//        SAXParser parser = saxParserFactory.newSAXParser();
//
//        panelSheetStream.close();
        //excelFile.close();
//
        BufferedReader reader = new BufferedReader(new FileReader(testExcel));
        String header = reader.readLine();
        int numElements = header.split("\\*\\*\\*\\*").length;
        System.out.println("no. of elements per line: " + numElements);
        String line;
        String[] elements;
        int count = 0;
        int count_malformed = 0;
        while ((line = reader.readLine()) != null) {
            while (line.split("\\*\\*\\*\\*").length < 31) {
                line = line.trim() + reader.readLine().trim();
            }
            count++;
            elements = line.split("\\*\\*\\*\\*");
            if (elements.length != numElements) {
                count_malformed++;
                System.out.println("line index: " + count);
                System.out.println(line);
                System.out.println("line elements: " + elements.length);
                //System.out.println("Malformed line: " + elements.length + "----" + line);
            }
        }
        System.out.println("no. malformed lines: " + count_malformed);
        reader.close();

    }

}