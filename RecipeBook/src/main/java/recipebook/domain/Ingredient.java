package recipebook.domain;

public class Ingredient implements Comparable<Ingredient> {

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

    @Override
    public String toString() {
        return this.name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int compareTo(Ingredient other) {
        return this.name.compareToIgnoreCase(other.getName());
    }

}
