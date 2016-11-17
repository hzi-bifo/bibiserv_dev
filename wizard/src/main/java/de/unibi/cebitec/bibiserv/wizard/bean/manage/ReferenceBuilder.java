package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.util.bibtexparser.BibTexType;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPerson;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.BiBiPersonImplementation;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.BiBiPublicationImplementation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Class used to create new BibTex entries for References.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ReferenceBuilder {

    /**
     * Get the first three letters of the given string.
     * ü=ue, ö=oe, ä=ae, ß=s
     * ch, sch is one letter
     *
     * @param author last name of the author
     * @return first three letters
     */
    private static String getFirstThreeLetters(String author) {
        // clean of whitespaces
        author = author.replaceAll(" ", "");
        // all to lowercase
        author = author.toLowerCase();

        // rewrite special chars
        author = author.replaceAll("ü", "ue");
        author = author.replaceAll("ö", "oe");
        author = author.replaceAll("ä", "ae");
        author = author.replaceAll("ß", "ss");

        // change sch and ch to new unallowed characters
        author = author.replace("sch", "ü");
        author = author.replace("ch", "ö");

        // get first three chars
        String ret;
        if (author.length() >= 3) {
            ret = author.substring(0, 3);
        } else {
            ret = author;
        }

        // change back to ch and sch
        ret = ret.replace("ü", "sch");
        ret = ret.replace("ö", "ch");

        return ret;
    }

    /**
     * Generate Id XXX[:YYY:[...]]:YEAR for Bibtex, where XXX and YYY are the
     * first three
     * letter of the authors last names
     *
     * @param authors list of alle authors
     * @param year year of the
     * @return
     */
    private static String generateId(List<Tupel<String, String>> authors,
            String year) {

        String ret = "";

        for (Tupel<String, String> author : authors) {
            String letters = getFirstThreeLetters(author.getSecond());
            if (!letters.isEmpty()) {
                ret += letters + ":";
            }
        }
        // clean of whitespaces
        year = year.replaceAll(" ", "");
        ret += year;

        return ret;
    }

    /**
     * Create a Reference as BiBiPublication (see OntoAcess)
     * All strings should be cleaned.
     *
     * @param type what Bibtexobject to create
     * @param authors List of Author Tupels (FirstName, SecondName)
     * @param title title of the referenced item
     * @param year year of the referenced item
     * @param journal journal of the referenced item
     * @param school school of the referenced item
     * @param doi doi of the referenced item
     * @param url ourl f the referenced item
     * @param publisher publisher of the referenced item
     * @param note note of the referenced utem
     * @return new reference
     */
    public static BiBiPublication createReference(BibTexType type,
            List<Tupel<String, String>> authors, String title, String year,
            String journal, String school, String doi, String url,
            String publisher, String note) {

        BiBiPublication reference = new BiBiPublicationImplementation();

        // generate the id from names
        String id = generateId(authors, year);
        // set id to reference
        reference.setPubkey(id);

        // set Type
        reference.setType(type);

        //generate BibiPersons
        List<BiBiPerson> persons = new ArrayList<BiBiPerson>();
        for (Tupel<String, String> author : authors) {
            BiBiPerson newPerson = new BiBiPersonImplementation();
            newPerson.setGivenname(author.getFirst());
            newPerson.setFamily_name(author.getSecond());
            persons.add(newPerson);
        }

        // authors are used by all
        reference.setAuthors(persons);
        // year is used by all
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), 1, 1);
        reference.setPublicationdate(cal.getTime());

        // title is needed by all
        reference.setTitle(title);

        // create the rest of the content
        switch (type) {
            case article:
                reference.setJournal(journal);
                if (!note.isEmpty()) {
                    reference.setNote(note);
                }
                break;
            case book:
                reference.setPublisher(publisher);
                if (!note.isEmpty()) {
                    reference.setNote(note);
                }
                break;
            case inproceedings:
                if (!note.isEmpty()) {
                    reference.setNote(note);
                }
                if (!publisher.isEmpty()) {
                    reference.setPublisher(publisher);
                }
                break;
            case manual:
                if (!note.isEmpty()) {
                    reference.setNote(note);
                }
            case mastersthesis:
            case phdthesis:
                reference.setSchool(school);
                if (!note.isEmpty()) {
                    reference.setNote(note);
                }
                break;
            case proceedings:
                if (!note.isEmpty()) {
                    reference.setNote(note);
                }
                if (!publisher.isEmpty()) {
                    reference.setPublisher(publisher);
                }
                break;
            case techreport:
                reference.setInstitution(school);
                if (!note.isEmpty()) {
                    reference.setNote(note);
                }
                break;
        }

        if (!doi.isEmpty()) {
            reference.setDoi(doi);
        }
        if (!url.isEmpty()) {
            try {
                URI newurl = new URI(url);
                reference.setUrl(newurl);
            } catch (URISyntaxException ex) {
                // should not happen, testest in referencebean
            }
        }

        return reference;
    }
}
