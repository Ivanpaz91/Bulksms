package com.patrick.buksms.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by sb on 10/18/15.
 */
@Table(name = "SimpleContact")
public class SimpleContact extends Model {
    @Column(name = "Name")
    public String name;
    @Column(name = "Email")
    public String email;
    @Column(name = "Phone")
    public String phone;

    public SimpleContact() {
    }

    public static List<SimpleContact> getAll() {

        // This is how you execute a query

        return new Select()

                .from(SimpleContact.class)

                .orderBy("Name ASC")

                .execute();

    }
    public static void deletAll() {
        new Delete().from(SimpleContact.class).execute(); // all records
    }

//    .where("Category = ?", category.getId())
}
