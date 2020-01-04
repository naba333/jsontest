package com.absi.ex.jsontest;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.LocalDate;

//it is also possible to change the way object is serialized (property names, elements order etc.)
//special annotations (in serialized class) are used to do that



public class JsonBTest
{
   private static final Newspaper newspaper = new Newspaper("Imperial news", 100_000, LocalDate.of(2666, 1, 1));

   private static final Jsonb jsonb = JsonbBuilder.create();

   public static void main(String args [])
   {
      objectWriting();
      objectReading();
   }


   private static void objectWriting()
   {
      String newspaperRepresentation = jsonb.toJson(newspaper);
      System.out.println("\n" + newspaperRepresentation);
   }

   private static void objectReading()
   {
      String newspaperRepresentation = jsonb.toJson(newspaper);

      Newspaper otherNewspaper = jsonb.fromJson(newspaperRepresentation, Newspaper.class);
      System.out.println("\n" + otherNewspaper.getIssueDate());
   }
}
