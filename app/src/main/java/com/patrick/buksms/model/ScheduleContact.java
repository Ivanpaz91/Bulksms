package com.patrick.buksms.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 10/18/15.
 */
@Table(name = "ScheduleContact")
public class ScheduleContact extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Phone")
    public String phone;

    @Column(name = "Message")
    public String message;



    public ScheduleContact() {

    }

    public static ArrayList<ScheduleContact> getAll() {

        // This is how you execute a query

        return new Select()

                .from(ScheduleContact.class)

                .orderBy("Name ASC")


                .execute();}

    public static ScheduleContact getOne() {

        // This is how you execute a query

        return new Select()

                .from(ScheduleContact.class)

                .orderBy("Name ASC")
                .executeSingle();

                }


    public static void deletAll() {
        new Delete().from(ScheduleContact.class).execute(); // all records
    }
    public static void deleteOne(int i){
        new Delete().from(ScheduleContact.class).where("Id = ?", 1).executeSingle();
    }


//    .where("Category = ?", category.getId())
}
