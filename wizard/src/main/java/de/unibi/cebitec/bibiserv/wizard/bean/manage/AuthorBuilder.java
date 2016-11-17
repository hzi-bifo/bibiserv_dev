
package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.techfak.bibiserv.cms.Tperson;

/**
 * Class is used to build Tperson from raw data.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class AuthorBuilder {

    private static final String ID_BASE_TYPE = "author";
    
    public static Tperson createAuthor(String firstname, String lastname,
            String organisation, String email, String phone, String adress){

        Tperson newAuthor = new Tperson();
        newAuthor.setFirstname(firstname);
        newAuthor.setLastname(lastname);
        newAuthor.setEmail(email);

        if(!organisation.isEmpty()){
            newAuthor.setOrganisation(organisation);
        }
        if(!adress.isEmpty()){
            newAuthor.setAdress(adress);
        }
        if(!phone.isEmpty()){
            newAuthor.setPhone(phone);
        }

        return newAuthor;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }
    
}
