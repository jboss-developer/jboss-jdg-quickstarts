package org.jboss.as.quickstarts.datagrid.pojos;

import java.io.Serializable;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", generateHeaderColumns = false)
public class Person implements Serializable {

   private static final long serialVersionUID = -9054867559874397446L;

   @DataField(pos = 1)
   private int id;
   @DataField(pos = 2)
   private String firstName;
   @DataField(pos = 3)
   private String lastName;
   @DataField(pos = 4)
   private int age;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public int getAge() {
      return age;
   }

   public void setAge(int age) {
      this.age = age;
   }

}
