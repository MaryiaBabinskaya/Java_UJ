package uj.wmii.pwj.w7.insurance;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class InsuranceEntry{
    String[] tab;
    InsuranceEntry(String arg){
        tab = arg.split(",");
    }
}

public class FloridaInsurance {

    public static void main(String[] args){

        List<InsuranceEntry> list = new ArrayList<>();

        try(ZipFile file = new ZipFile("FL_insurance.csv.zip")) {
            ZipEntry entry = file.entries().nextElement();
            if(!entry.isDirectory()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(entry)));
                String line;
                while((line = reader.readLine()) != null){
                    list.add(new InsuranceEntry(line));
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        count(list);
        tiv2012(list);
        most_valuable(list);
    }

    private static void count(List<InsuranceEntry> list){
        File file = new File("count.txt");
        int counter = -1;
        List<String> listOfCountries = new ArrayList<>();
        for(InsuranceEntry entry : list){
            if(!listOfCountries.contains(entry.tab[2])){
                listOfCountries.add(entry.tab[2]);
                counter++;
            }
        }
        try(FileWriter writer = new FileWriter(file.getName())) {
            writer.write(Integer.toString(counter));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void tiv2012(List<InsuranceEntry> list) {
        File file = new File("tiv2012.txt");
        BigDecimal bigDecimal = new BigDecimal(0);
        int counter = 0;
        for(InsuranceEntry entry : list){
            if(counter == 0) counter = 1;
            else bigDecimal = bigDecimal.add(new BigDecimal(entry.tab[8]));
        }
        try(FileWriter writer = new FileWriter(file.getName())) {
            writer.write(bigDecimal.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void most_valuable(List<InsuranceEntry> list){
        File file = new File("most_valuable.txt");
        Map<String, BigDecimal> countryValues = new HashMap<>();
        for (int i = 1; i < list.size(); i++) {
            InsuranceEntry entry = list.get(i);
            String country = entry.tab[2];
            BigDecimal tiv2011 = new BigDecimal(entry.tab[7]);
            BigDecimal tiv2012 = new BigDecimal(entry.tab[8]);
            BigDecimal difference = tiv2012.subtract(tiv2011);
            if (countryValues.containsKey(country)) {
                BigDecimal currentValue = countryValues.get(country);
                countryValues.put(country, currentValue.add(difference));
            } else {
                countryValues.put(country, difference);
            }
        }
        List<BigDecimal> sortedValues  = new ArrayList<>(countryValues.values());
        sortedValues.sort(Collections.reverseOrder());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("country,value\n");
            for (int i = 0; i < 10; i++) {
                BigDecimal currentValue = sortedValues.get(i);
                for (var entry : countryValues.entrySet()) {
                    if (entry.getValue().equals(currentValue)) {
                        writer.write(entry.getKey() + "," + currentValue + "\n");
                        break;
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}