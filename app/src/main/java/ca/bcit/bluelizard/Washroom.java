package ca.bcit.bluelizard;

public class Washroom {
    private String type;

    private WashroomGeometry geometry;

    private WashroomProperties properties;

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setGeometry(WashroomGeometry geometry){
        this.geometry = geometry;
    }
    public WashroomGeometry getGeometry(){
        return this.geometry;
    }
    public void setProperties(WashroomProperties properties){
        this.properties = properties;
    }
    public WashroomProperties getProperties(){
        return this.properties;
    }
}
