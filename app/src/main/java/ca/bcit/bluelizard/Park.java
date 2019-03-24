package ca.bcit.bluelizard;

public class Park
{
    private String parkType;

    private Geometry geometry;

    private Properties properties;

    public void setParkType(String parkType){
        this.parkType = parkType;
    }
    public String getType(){
        return this.parkType;
    }
    public void setGeometry(Geometry geometry){
        this.geometry = geometry;
    }
    public Geometry getGeometry(){
        return this.geometry;
    }
    public void setProperties(Properties properties){
        this.properties = properties;
    }
    public Properties getProperties(){
        return this.properties;
    }
}