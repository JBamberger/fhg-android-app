package xyz.jbapps.vplan.util.network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
public class VPlanRequestTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test() throws Exception {
        Document doc = Jsoup.parse(DOC);
        Elements table = doc.getElementsByClass("list").select("tr");

        for (Element line : table) {
            //System.out.println(line.toString());
            System.out.println("###div###");
            Elements cells = line.children();
            for (Element cell : cells) {
                try {
                    System.out.println(cell.html());
                    for (Element element : cell.getElementsByTag("span")) {
                        System.out.println(element.html());
                        System.out.println("###");

                    }
                    System.out.println("### cell end ###");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }


    private static final String DOC = "\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Untis 2018 Vertretungsplan</title>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n" +
            "<meta http-equiv=\"expires\" content=\"0\">\n" +
            "<style type=\"text/css\">\n" +
            "\n" +
            "body { margin-top: 0px; margin-left: 20px; margin-right: 20px;\n" +
            "background: #fff; color: #272727; font: 80% Arial, Helvetica, sans-serif; }\n" +
            "\n" +
            "h1 { color: #ee7f00; font-size: 150%; font-weight: normal;}\n" +
            "h1 strong { font-size: 200%; font-weight: normal; }\n" +
            "h2 { font-size: 125%;}\n" +
            "\n" +
            "h1, h2 { margin: 0; padding: 0;}\n" +
            "\n" +
            "\n" +
            "th { background: #000; color: #fff; }\n" +
            "table.mon_list th, td { padding: 8px 4px;}\n" +
            "\n" +
            "\n" +
            ".mon_title \n" +
            "{ \n" +
            "\tfont-weight: bold; \n" +
            "\tfont-size: 120%; \n" +
            "\tclear: both; \n" +
            "\tmargin: 0; \n" +
            "}\n" +
            "\n" +
            ".inline_header\n" +
            "{\n" +
            "\tfont-weight: bold; \n" +
            "}\n" +
            "\n" +
            "table.info\n" +
            "{\n" +
            "\tcolor: #000000; \n" +
            "\tfont-size: 100%;\n" +
            "\tborder: 1px;\n" +
            "\tborder-style:solid;\n" +
            "\tborder-collapse:collapse;\n" +
            "\tpadding: 8px 4px;\n" +
            "}\n" +
            "\n" +
            "table.mon_list\n" +
            "{\n" +
            "\tcolor: #000000; \n" +
            "\twidth: 100%; \n" +
            "\tfont-size: 100%;\n" +
            "\tborder: 1px;\n" +
            "\tborder-style:solid;\n" +
            "\tborder-collapse:collapse;\n" +
            "}\n" +
            "\n" +
            "table.mon_head\n" +
            "{\n" +
            "\tcolor: #000000; \n" +
            "\twidth: 100%; \n" +
            "\tfont-size: 100%;\n" +
            "}\n" +
            "\n" +
            "td.info,\n" +
            "th.list,\n" +
            "td.list,\n" +
            "tr.list\n" +
            "{\n" +
            "\tborder: 1px;\n" +
            "\tborder-style: solid;\n" +
            "\tborder-color: black;\n" +
            "\tmargin: 0px;\n" +
            "\tborder-collapse:collapse;\n" +
            "\tpadding: 3px;\n" +
            "}\n" +
            "\n" +
            "tr.odd { background: #fad3a6; }\n" +
            "tr.even { background: #fdecd9; }\n" +
            "\n" +
            "</style>\n" +
            "<meta name=\"generator\" content=\"Untis 2018\">\n" +
            "<meta name=\"company\" content=\"Gruber &amp; Petters Software, A-2000 Stockerau, Austria, www.untis.at\">\n" +
            "<meta http-equiv=\"refresh\" content=\"300; URL=subst_001.htm\">\n" +
            "</head>\n" +
            "\n" +
            "Stand: 18.09.2017 13:59<p>\n" +
            "<body bgcolor=\"#F0F0F0\">\n" +
            "<CENTER>\n" +
            "<font size=\"3\" face=\"Arial\">\n" +
            "<div class=\"mon_title\">18.9.2017 Montag</div>\n" +
            "<table class=\"info\" >\n" +
            "<tr class=\"info\"><th class=\"info\" align=\"center\" colspan=\"2\">Nachrichten zum Tag</th></tr>\n" +
            "<tr class='info'><td class='info' colspan=\"2\">Das <b>Unterstufenorchester </b>trifft sich heute bereits um 14 Uhr zur Vorbesprechung.</td></tr>\n" +
            "</table>\n" +
            "<p>\n" +
            "<table class=\"mon_list\" >\n" +
            "<tr class='list'><th class=\"list\" align=\"center\">Klasse(n)</th><th class=\"list\" align=\"center\">Stunde</th><th class=\"list\" align=\"center\">Fach</th><th class=\"list\" align=\"center\">Raum</th><th class=\"list\" align=\"center\">Art</th><th class=\"list\" align=\"center\">Vertretungs-Text</th></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">K2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">rk_3</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\"><s>118</s>?105</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">K2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">5 - 6</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>M_3</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">K2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">5 - 6</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">E_1</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\"><s>120</s>?105</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\">K2</td><td class=\"list\" align=\"center\">7</td><td class=\"list\" align=\"center\">eth_1</td><td class=\"list\" align=\"center\">216</td><td class=\"list\" align=\"center\">Unterricht geändert</td><td class=\"list\" align=\"center\">Dauerraumänderung</td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">K2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">11 - 12</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>Sp_1</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">K1</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">3 - 4</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Gmk_1</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">104</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">K1</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">5</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">ev_1</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\"><s>213</s>?102</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">K1</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">10 - 11</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">Sem_1</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\"><s>047</s>?105</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">10a</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">1 - 2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>Che</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\"><span style=\"color: #010101\">10a</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">3</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\"><s>M</s>?G</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">221</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">Verlegung</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">G statt Mi 6/7. STd.</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\"><span style=\"color: #010101\">10a</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">4</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\"><s>M</s>?G</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">221</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">Verlegung</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">G statt Mi 6/7. STd.</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">10a</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">5 - 6</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">NwT</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\"><s>NwT</s>?026</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\">10a, 10c, 10b</td><td class=\"list\" align=\"center\">9 - 10</td><td class=\"list\" align=\"center\">Sp w</td><td class=\"list\" align=\"center\"><s>TH4</s>?TH2</td><td class=\"list\" align=\"center\">Betreuung</td><td class=\"list\" align=\"center\">&nbsp;</td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">10a, 10c, 10b</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">9 - 10</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>Sp m</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Sp m, nur Grof entfällt</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">10c</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">1 - 2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">GmK</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\"><s>224</s>?102</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">10c</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">5</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">NwT</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\"><s>038</s>?Aula</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">Vertretung</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">selbst. arbeiten</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\"><span style=\"color: #010101\">10c</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">6</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\"><s>NwT</s>?M</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\"><s>038</s>?226</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">Verlegung</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">M statt 7. Std.</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">10c</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">7</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>M</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">9a</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">5 - 6</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">E</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\"><s>211</s>?047</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">9a</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">7</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>Che</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">verlegt auf Do 6. Std.</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">9c</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">1 - 2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>NwT</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>NwT</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">9c</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">5</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">Ek</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\"><s>214</s>?104</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #00FF00\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\"><span style=\"color: #010101\">8a</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">5</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\"><s>M</s>?F 1/2</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">202</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">Verlegung</span></td><td class=\"list\" align=\"center\"><span style=\"color: #010101\">F 1/2 statt 7. Std.</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">8a</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">6</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>M</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">8a</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">7</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>FöM</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">8a</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">7</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>F 1/2</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">8b</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">1 - 2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>Phy</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">8c</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">7</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">GmK</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\"><s>205</s>?047</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raum-Vtr.</span></td><td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" ><span style=\"color: #010101\">Raumänderung</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">7c</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">1 - 2</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>Inf7</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">6a</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">7</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>Bio</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">verlegt auf Di 4. Std.</span></td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\">6d</td><td class=\"list\" align=\"center\">7</td><td class=\"list\" align=\"center\">KlSt</td><td class=\"list\" align=\"center\">126</td><td class=\"list\" align=\"center\">Unterricht geändert</td><td class=\"list\" align=\"center\">Raum 126</td></tr>\n" +
            "<tr class='list even'><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">5d</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">5</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">M</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">067</span></td><td class=\"list\" align=\"center\"><span style=\"color: #2B5E32\">Vertretung</span></td><td class=\"list\" align=\"center\">&nbsp;</td></tr>\n" +
            "<tr class='list odd'><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">5d</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">6</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\"><s>M</s></span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">---</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">Entfall</span></td><td class=\"list\" align=\"center\" style=\"background-color: #C0C0C0\" ><span style=\"color: #010101\">&nbsp;</span></td></tr>\n" +
            "</table>\n" +
            "<p>\n" +
            "<font size=\"3\" face=\"Arial\">\n" +
            "18.9.2017   \n" +
            "</font></font>\n" +
            "\n" +
            "</CENTER>\n" +
            "<p><center><font face=\"Arial\" size=\"2\"><a href=\"http://www.untis.at\" target=\"_blank\" >Untis Stundenplan Software</a></font></center>\n" +
            "</body>\n" +
            "</html>\n" +
            "\n";

}