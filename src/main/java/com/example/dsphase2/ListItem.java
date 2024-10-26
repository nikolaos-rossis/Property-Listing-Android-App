package com.example.dsphase2;

public class ListItem {
    private String primaryText;
    private String subText;

    public ListItem(String primaryText, String subText) {
        this.primaryText = primaryText;
        this.subText = subText;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public String getSubText() {
        return subText;
    }
}
