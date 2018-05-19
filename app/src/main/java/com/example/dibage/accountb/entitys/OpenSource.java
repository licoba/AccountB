package com.example.dibage.accountb.entitys;

public class OpenSource {
    String website;
    String name;
    String describe;

    public OpenSource(String website, String name, String describe) {
        this.website = website;
        this.name = name;
        this.describe = describe;
    }

    public OpenSource() {
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "OpenSource{" +
                "website='" + website + '\'' +
                ", name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
