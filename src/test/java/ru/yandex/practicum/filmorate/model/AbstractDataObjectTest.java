package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractDataObjectTest<T extends DataObject> implements DataObjectTest {

    protected String jsonDir = "src/test/resources/validation/json";

    @Override
    @Test
    public abstract void validateWrong();

    @Override
    @Test
    public abstract void validateCorrect();

    protected void failedValidation(Class<T> clazz, String dir) {
        List<Path> fileList = getPathList(dir);
        StringBuilder stringBuilder = new StringBuilder();
        for (Path path : fileList) {
            stringBuilder.append(failValidationFromJson(path.toFile(), clazz));
        }
        if (!stringBuilder.toString().equals("")) {
            Assertions.fail(stringBuilder.toString());
        }
    }

    protected void correctValidation(Class<T> clazz, String dir) {
        List<Path> fileList = getPathList(dir);
        StringBuilder stringBuilder = new StringBuilder();
        for (Path path : fileList) {
            stringBuilder.append(correctValidationFromJson(path.toFile(), clazz));
        }
        if (!stringBuilder.toString().equals("")) {
            Assertions.fail(stringBuilder.toString());
        }
    }


    private String failValidationFromJson(File json, Class<T> clazz) {
        T object;

        try {
            object = deserialize(json, clazz);
        } catch (IOException ioException) {
            Assertions.fail(ioException.getMessage());
            return "";
        }

        if (valid(object)) {
            return "Incorrect " + json.getName() + " is passed.\n" + object.toString() + "\n";
        }
        return "";
    }

    private String correctValidationFromJson(File json, Class<T> clazz) {
        T object;

        try {
            object = deserialize(json, clazz);
        } catch (IOException ioException) {
            Assertions.fail(ioException.getMessage() + " from file " + json.getName());
            return "";
        }

        if (!valid(object)) {
            return "Correct " + json.getName() + " is not validated.\n" + object.toString() + "\n";
        }
        return "";
    }

    private boolean valid(T object) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        for (ConstraintViolation<T> violation : violations) {
            return false;
        }
        return true;
    }

    private List<Path> getPathList(String dir) {
        List<Path> fileList;
        try {
            fileList = Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        Assertions.assertFalse(fileList.isEmpty(), "Directory " + dir + " not contains files");
        return fileList;
    }

    private T deserialize(File json, Class<T> clazz) throws IOException {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(json, clazz);
    }
}
