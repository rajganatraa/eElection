package com.example.eelection.model;

public class Candidate  {

    String name,party,position,image,id,elect_name;
    int count=0;

//    public Candidate()
//    {
//
//    }

    public Candidate(String name,String party,String position,String image,String id,String elect_name)
    {
        this.name=name;
        this.party=party;
        this.position=position;
        this.image=image;
        this.id=id;
        this.elect_name=elect_name;
    }

    public String getElect_name() {
        return elect_name;
    }

    public void setElect_name(String elect_name) {
        this.elect_name = elect_name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
