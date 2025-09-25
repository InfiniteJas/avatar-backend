package kz.nitec.sduchatbotapplication.mapper;

import kz.nitec.sduchatbotapplication.dto.ChatDto;
import kz.nitec.sduchatbotapplication.entity.ChatEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    ChatDto toDto(ChatEntity chatEntity);
}
