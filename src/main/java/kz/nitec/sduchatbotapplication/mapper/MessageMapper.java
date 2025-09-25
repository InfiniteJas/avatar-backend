package kz.nitec.sduchatbotapplication.mapper;

import kz.nitec.sduchatbotapplication.dto.MessageDto;
import kz.nitec.sduchatbotapplication.entity.MessageEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageDto toDto(MessageEntity message);
}
