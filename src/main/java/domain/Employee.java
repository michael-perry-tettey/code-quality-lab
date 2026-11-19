package domain;

import static validation.InputValidator.requireText;

public record Employee(String id, String name) {

    public Employee {
        requireText(id, "Employee ID");
        requireText(name, "Employee name");
    }
}
