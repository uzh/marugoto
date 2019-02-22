package ch.uzh.marugoto.core.helpers;

import org.springframework.beans.BeanUtils;

import ch.uzh.marugoto.core.data.entity.dto.RequestDto;
import ch.uzh.marugoto.core.exception.DtoToEntityException;

public class DtoHelper {

    public static void map(RequestDto dtoObject, Object mapToObject) throws DtoToEntityException {
        try {
            BeanUtils.copyProperties(dtoObject, mapToObject);
        } catch (Exception e) {
            throw new DtoToEntityException(e.getMessage());
        }
    }
}
