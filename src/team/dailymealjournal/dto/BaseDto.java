package team.dailymealjournal.dto;

import java.util.List;

public interface BaseDto {
    List<String> getErrorList();
    void setErrorList(List<String> errorList);
}
