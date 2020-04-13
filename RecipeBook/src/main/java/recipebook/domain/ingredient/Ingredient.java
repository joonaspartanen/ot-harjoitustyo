package recipebook.domain.ingredient;

import java.util.Objects;

public class Ingredient implements Comparable<Ingredient> {

    private int id;
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

    public Ingredient(int id, String name, String unit) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Ingredient other) {
        return this.name.compareToIgnoreCase(other.getName());
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof Ingredient)) {
            return false;
        }

        Ingredient other = (Ingredient) object;

        return this.id == other.getId() && this.name.equals(other.getName()) && this.unit.equals(other.getUnit());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.unit);
        return hash;
    }
    
    

}
