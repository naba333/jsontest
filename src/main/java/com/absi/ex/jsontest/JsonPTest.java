package com.absi.ex.jsontest;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.stream.JsonGenerator;
import javax.json.JsonWriterFactory;
import javax.json.JsonReaderFactory;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.util.Collections;
import javax.json.JsonPointer;
import javax.json.JsonStructure;
import java.util.Map;
import java.io.ByteArrayInputStream;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import javax.json.stream.JsonGeneratorFactory;
import java.io.FileOutputStream;
import java.io.IOException;

public class JsonPTest
{
   public static void main(String args[])
   {
      JsonObject authorObject = createJsonObject();  //implementation below - object mode

      //printing on screen
      System.out.println(authorObject.toString());

      //another  method - nice looking
      printJsonOnScreen(authorObject);

      System.out.println("\n\n");


      //reading json JsonObject -----------------------------------------------------------------------------------------------

      String jsonDocument = authorObject.toString(); //creating String from json

      JsonReaderFactory readerFactory = Json.createReaderFactory(Collections.emptyMap());

      try(JsonReader jsonReader = readerFactory.createReader(new ByteArrayInputStream(jsonDocument.getBytes())))
      {
         JsonObject jsonObject = jsonReader.readObject(); //object read

         //printing only part of object
         System.out.println(jsonObject
            .getJsonObject("webpage")
            .getJsonArray("articles")
            .getJsonObject(0)
            .getJsonObject("publication date")
         );
      }

      //and simpler method
      readerFactory = Json.createReaderFactory(Collections.emptyMap());
      try(JsonReader jsonReader = readerFactory.createReader(new ByteArrayInputStream(jsonDocument.getBytes())))
      {
         JsonStructure jsonStructure = jsonReader.read();

         //first method
         System.out.println(jsonStructure.getValue("/webpage/articles/0/publication date"));

         //second method
         JsonPointer jsonPointer = Json.createPointer("/webpage/articles/0/publication date");
         System.out.println(jsonPointer.getValue(jsonStructure));
      }

      //to change something
      JsonPointer jsonPointer = Json.createPointer("/webpage/articles/0/publication date/month");
      JsonObject modifiedAuthor = jsonPointer.replace(authorObject, Json.createValue(3));

      System.out.println("\n\n\nAfter modification into new object:\n");

      printJsonOnScreen(authorObject);
      printJsonOnScreen(modifiedAuthor);


      //stream mode -----------------------------------------------------------------------------------------------

      //write
      //print/write on screen
      writeJsonObjectUsingStream(System.out);

      //to file
      try(FileOutputStream os = new FileOutputStream("test.txt"))
      {
         writeJsonObjectUsingStream(os);
      }
      catch(IOException e) {}

      System.out.println("\n\n");

      //read
      readAsStreamAndPrintOnScreen(createJsonObject());

   }

   private static void readAsStreamAndPrintOnScreen(JsonObject jo)
   {
      JsonParserFactory parserFactory = Json.createParserFactory(Collections.emptyMap());
      JsonParser parser = parserFactory.createParser(createJsonObject());

      while(parser.hasNext())
      {
         JsonParser.Event event = parser.next();
         switch(event)
         {
             case START_OBJECT:
               System.out.println("{");
               break;
            case END_OBJECT:
               System.out.println("}");
               break;
            case START_ARRAY:
               System.out.println("[");
               break;
            case END_ARRAY:
               System.out.println("]");
               break;
            case KEY_NAME:
               System.out.println(String.format("\"%s\": ", parser.getString()));
               break;
            case VALUE_NUMBER:
               System.out.println(parser.getBigDecimal());
               break;
            case VALUE_STRING:
               System.out.println(String.format("\"%s\"", parser.getString()));
               break;
            default:
               System.out.println("true, false or null");
         }
      }
   }


   private static void printJsonOnScreen(JsonObject jo)
   {
      Map<String, ?> config = Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);
      JsonWriterFactory writerFactory = Json.createWriterFactory(config);
      writerFactory.createWriter(System.out).write(jo);
   }

   private static void writeJsonObjectUsingStream(java.io.OutputStream outStream)
   {
      JsonGeneratorFactory generatorFactory = Json.createGeneratorFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));
      JsonGenerator generator = generatorFactory.createGenerator(outStream);
      generator
         .writeStartObject()
            .write("name", "Lord")
            .write("surename", "Vader")
            .writeStartObject("www")
               .write("www address", "http://www.death-star.com")
               .writeStartArray("articles")
                  .writeStartObject()
                     .write("title", "Mein kampf")
                     .writeStartObject("publication date")
                        .write("year", 2666)
                        .write("month", 13)
                        .write("day", 33)
                     .writeEnd()
                  .writeEnd()
               .writeEnd()
            .writeEnd()
         .writeEnd()
         .flush();
   }

   private static JsonObject createJsonObject()
   {
      JsonBuilderFactory builderFactory = Json.createBuilderFactory(Collections.emptyMap());
      JsonObject publicationDateObject = builderFactory.createObjectBuilder()
         .add("year", 2018)
         .add("month", 9)
         .add("day", 13)
         .build();

      JsonObject articleObject = builderFactory.createObjectBuilder()
         .add("title", "Book of Java")
         .add("publication date", publicationDateObject)
         .build();

      JsonArray articlesArray = builderFactory.createArrayBuilder()
         .add(articleObject)
         .build();

      JsonObject webPageObject = builderFactory.createObjectBuilder()
         .add("www address", "http://www.absi.com")
         .add("articles", articlesArray)
         .build();

      JsonObject authorObject = builderFactory.createObjectBuilder()
         .add("name", "Arek")
         .add("surename", "Bancer")
         .add("webpage", webPageObject)
         .build();

      return authorObject;
   }
}
