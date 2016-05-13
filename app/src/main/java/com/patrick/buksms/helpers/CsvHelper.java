package com.patrick.buksms.helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import com.opencsv.CSVReader;
import com.patrick.buksms.model.Contact;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import ru.bartwell.exfilepicker.ExFilePickerParcelObject;

/**
 * Created by ivan on 10/16/15.
 */
public class CsvHelper {
    public static Contact tokenize(String[] csvLine , int first,int last,int phone,int email) {
        Contact contact = new Contact();

        String phoneString = "";
        if (csvLine.length > 0) {

                if(first != -1) {
                    contact.firstName = csvLine[first].trim();
                }
                if(last != -1){
                    contact.lastName = csvLine[last].trim();
                }else{
                    contact.lastName = "";
                }

                if(phone != -1) {
                    contact.phoneString = csvLine[phone].trim();
                    phoneString =   contact.phoneString;
                }
                if(email != -1) {
                    contact.email = csvLine[email].trim();

                }else{
                    contact.email = "";
                }
//                } else if(i == 6){
//                    contact.state = csvLine[i].trim();
//                }

            phoneString = phoneString.replaceAll(Pattern.quote(")"),"");
            phoneString = phoneString.replaceAll(Pattern.quote("("), "");
            phoneString = phoneString.replaceAll("-", "");
            phoneString = phoneString.replaceAll(" ", "");
            contact.phoneNumber = phoneString;
        }

        return contact;
    }

    public static ArrayList<Contact> getContact(Context context, ExFilePickerParcelObject obj){
        ArrayList<Contact> profiles = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            File file = new File(obj.path + obj.names.get(0));
            FileInputStream fileInputStream = new FileInputStream(file);

            InputStreamReader csvStreamReader = new InputStreamReader(fileInputStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);

            String[] next;
            int firstNamePosition = -1;
            int lastNamePosition = -1;;
            int phonePosition = -1;;
            int emailPosition = -1;;

            //check which column is name,phone
            String[] firstLine = csvReader.readNext();
            for(int i = 0 ; i < firstLine.length;i++){

                if(firstLine[i].toLowerCase().contains("first") || firstLine[i].toLowerCase().contains("name") ){
                    if(firstNamePosition == -1){
                        firstNamePosition = i;
                    }else {
                        lastNamePosition = i;
                    }

                }
                if(firstLine[i].toLowerCase().contains("last")){
                    lastNamePosition = i;

                }
                if(firstLine[i].toLowerCase().contains("phone")){
                    phonePosition = i;
                }
                if(firstLine[i].toLowerCase().contains("email") ){
                    emailPosition = i;
                }
            }

            if(phonePosition != -1) {
                while (true) {
                    next = csvReader.readNext();

                    if (next != null) {
                        profiles.add(tokenize(next, firstNamePosition, lastNamePosition, phonePosition , emailPosition));
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profiles;
    }
}
