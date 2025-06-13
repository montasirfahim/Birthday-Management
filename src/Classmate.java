import javafx.beans.property.*;
import java.time.LocalDate;

public class Classmate {
    private final IntegerProperty id;
    private final StringProperty name;
    private final ObjectProperty<LocalDate> birthdate;

    public Classmate(int id, String name, LocalDate birthdate) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.birthdate = new SimpleObjectProperty<>(birthdate);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public LocalDate getBirthdate() { return birthdate.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public ObjectProperty<LocalDate> birthdateProperty() { return birthdate; }
}
