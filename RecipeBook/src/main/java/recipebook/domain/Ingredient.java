package recipebook.domain;

public class Ingredient {

    private String name;
    private String unit;

    public Ingredient(String name) {
        this.name = name;
        this.unit = "g";
    }

    public Ingredient(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
