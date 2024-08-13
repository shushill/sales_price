import org.example.DateTimeUtil;
import org.example.PriceStore;
import org.example.RangePriceStore;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;










void main(){

    RangePriceStore r1 = new RangePriceStore();
    r1.setSalePrice(500);
    String st = "2022-03-10";
    r1.setStartDate(st);
    String en = "2022-04-30";
    r1.setEndDate(en);

    RangePriceStore r2 = new RangePriceStore();
    r2.setSalePrice(600);
    String st2 = "2022-06-10";
    r2.setStartDate(st2);
    String en2 = "2022-07-30";
    r2.setEndDate(en2);

 //input
    PriceStore priceStore = new PriceStore(200);
    priceStore.setInputPrice(r1);
    priceStore.setInputPrice(r2);


    //Update Query
    RangePriceStore inpQuery = new RangePriceStore();
    inpQuery.setSalePrice(900);
   String st_query = "2022-06-15";
   String en_query = "2022-07-28";

    inpQuery.setStartDate(st_query);
    inpQuery.setEndDate(en_query);

    System.out.println("Input Query ");
    System.out.println(inpQuery);


    priceStore.updateInputRangePrice(inpQuery);

    System.out.println("Displaying the Price Store");
    System.out.println(priceStore);














}
