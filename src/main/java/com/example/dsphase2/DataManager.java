package com.example.dsphase2;


import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class DataManager {


    private static DataManager instance;

    private Object[] filter = new Object[7];


    private String[] cur= new String[9];
    private ArrayList<String[]> Katalymata = new ArrayList<String[]>();

    private ArrayList<String[]> SentKatalymata = new ArrayList<String[]>();

    private File curimageFile;


    private DataManager() {}

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }



    public  ArrayList<String[]> getKatalymata() {
        return this.Katalymata;
    }

    public void SetKatalymata(ArrayList<String[]> a) {
        this.Katalymata.addAll(a);
    }

    public  ArrayList<String[]> getSentKatalymata() {
        return this.SentKatalymata;
    }

    public void addtoSentKatalymata(String[] a) {
        this.SentKatalymata.add(a);
    }

    public void setFilter(String f1, LocalDate f2, LocalDate f3, String f4, String f5, String f6, String f7){
        this.filter[0]=f1;
        this.filter[1]=f2;
        this.filter[2]=f3;
        this.filter[3]=f4;
        this.filter[4]=f5;
        this.filter[5]=f6;
        this.filter[6]=f7;
    }

    public Object[] getFilter(){
        return this.filter;
    }

    public void setcurrent(String[] kat){
        for (int i =0 ; i<9 ;i++){
            this.cur[i]=kat[i];
        }
    }

    public String[] getcur(){
        return this.cur;
    }

    public void setcurimg(File file){
        this.curimageFile=file;
    }
    public File getcurimg(){
        return this.curimageFile;
    }

}

