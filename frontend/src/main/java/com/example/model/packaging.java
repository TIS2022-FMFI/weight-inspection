public class packaging
{
    private int packaging_id;
    private String packaging_type;
    private String name;
    private Float weight;
    private String picture_path;

    public int getPackaging_id()
    {
        return packaging_id;
    }

    public void setPackaging_id(int packaging_id)
    {
        this.packaging_id = packaging_id;
    }

    public String getPackaging_type()
    {
        return packaging_type;
    }

    public void setPackaging_type(String packaging_type)
    {
        this.packaging_type = packaging_type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Float getWeight()
    {
        return weight;
    }

    public void setWeight(Float weight)
    {
        this.weight = weight;
    }

    public String getPicture_path()
    {
        return picture_path;
    }

    public void setPicture_path(String picture_path)
    {
        this.picture_path = picture_path;
    }
}