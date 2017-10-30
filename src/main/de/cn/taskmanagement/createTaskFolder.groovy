package de.cn.taskmanagement

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.WordUtils

import javax.swing.*
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

@Grapes(
        [
                @Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.6'),
                @Grab(group = 'org.apache.commons', module = 'commons-text', version = '1.1')
        ]
)
@CompileStatic
class createTaskFolder {
    public static void main(String[] args) {
        def foldername = JOptionPane.showInputDialog(new JFrame(), "Please enter a foldername")
        if (StringUtils.isEmpty(foldername))
            System.exit(1)

        new File(".", processFoldername(foldername)).mkdir()
        System.exit(0)
    }

    private def static getTicketNumber(String input) {
        String returnValue = "";
        Matcher ticketMatcher = Pattern.compile("(?i)task \\d+").matcher(input)
        if (ticketMatcher.find()) {
            returnValue = ticketMatcher.group()
        }

        return returnValue
    }

    private def static getDate(String input) {
        Matcher dateMatcher = Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(input)
        String returnValue = "";
        if (dateMatcher.find()) {
            returnValue = dateMatcher.group()
        }
        return returnValue
    }

    private def static recombineWords(String foldername) {
        Matcher noWordMatcher = Pattern.compile("[^\\p{L}0-9]+").matcher(foldername)
        String retVal = noWordMatcher.replaceAll(" ")
        retVal = retVal.toLowerCase()
        retVal = WordUtils.capitalizeFully(retVal)
        return StringUtils.deleteWhitespace(retVal)
    }

    private def static joinWords(String today, String rfcNumber, String foldername) {
        List<String> words = new ArrayList<>()
        words.add(today)
        if (StringUtils.isNotEmpty(rfcNumber))
            words.add(rfcNumber)
        words.add(foldername)
        return StringUtils.join(words, '-')
    }

    private def static clearGermanCharacters(String input) {
        String retVal = input

        retVal = StringUtils.replaceEach(retVal,
                ["Ä", "ä",
                 "Ö", "ö",
                 "Ü", "ü",
                 "ß"] as String[],
                ["Ae", "ae",
                 "Oe", "oe",
                 "Ue", "ue",
                 "ss"] as String[])
        /*retVal = retVal.replaceAll("Ä", "Ae");
        retVal = retVal.replaceAll("ä", "ae");
        retVal = retVal.replaceAll("Ö", "Oe");
        retVal = retVal.replaceAll("ö", "oe");
        retVal = retVal.replaceAll("Ü", "Ue");
        retVal = retVal.replaceAll("ü", "ue");
        retVal = retVal.replaceAll("ß", "ss");*/
        return retVal
    }

    private def static testSingle(String input) {
        println input + " => " + processFoldername(input)
    }

    private def static String processFoldername(String input) {
        String foldername = clearGermanCharacters(input)
        String ticketNumber = getTicketNumber(foldername);
        foldername = StringUtils.remove(foldername, ticketNumber)
        String date = getDate(foldername)
        if (StringUtils.isNotEmpty(date)) {
            foldername = StringUtils.remove(foldername, date)
        } else {
            date = new SimpleDateFormat('yyyy-MM-dd').format(new Date())
        }
        foldername = recombineWords(foldername)
        foldername = joinWords(date, ticketNumber, foldername)
        foldername = foldername.replaceAll("\\s+", "")
        foldername = StringUtils.abbreviate(foldername, 255)
        return foldername
    }
}
