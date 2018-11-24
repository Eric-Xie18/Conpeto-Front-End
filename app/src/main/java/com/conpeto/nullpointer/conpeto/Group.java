package com.conpeto.nullpointer.conpeto;

public class Group {
    private String groupID;
    private String name;
    private String category;
    private String details;
    private String userIDs;
    private String Lat,Long;

    public Group(String groupID, String name, String category, String details, String userIDs, String Lat, String Long)
    {
        this.groupID = groupID;
        this.name = name;
        this.category = category;
        this.details = details;
        this.userIDs = userIDs;
        this.Lat = Lat;
        this.Long = Long;
    }
    public Group()
    {
        String error = "err";
        this.groupID = error;
        this.name = error ;
        this.category = error;
        this.details = error;
        this.userIDs =error;
        this.Lat = error;
        this.Long = error;
    }

    public String getID() {
        return groupID;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDetails() {
        return details;
    }

    public String getUserIDs() {
        return userIDs;
    }

    public String getLat() {
        return Lat;
    }

    public String getLong() {
        return Long;
    }
}
