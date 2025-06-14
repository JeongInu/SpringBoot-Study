package org.zerock.guestbook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.guestbook.dto.GuestbookDTO;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.dto.PageResultDTO;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;
import org.zerock.guestbook.repository.GuestbookRepository;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService{

  private final GuestbookRepository repository;   // 반드시 final

  @Override
  public Long register(GuestbookDTO dto) {

    log.info("DTO--------------------");
    log.info(dto);

    Guestbook entity = dtoToEntity(dto);
    log.info(entity);

    repository.save(entity);

    return entity.getGno();
  }

  @Override
  public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
    Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());

    BooleanBuilder booleanBuilder = getSearch(requestDTO);

    Page<Guestbook> result = repository.findAll(booleanBuilder, pageable);

    Function<Guestbook, GuestbookDTO> fn = this::entityToDto;

    return new PageResultDTO<>(result, fn);
  }

  @Override
  public GuestbookDTO read(Long gno) {
    Optional<Guestbook> result = repository.findById(gno);

    return result.map(this::entityToDto).orElse(null);
  }

  @Override
  public void remove(Long gno) {
    repository.deleteById(gno);
  }

  @Override
  public void modify(GuestbookDTO dto) {
    Optional<Guestbook> result = repository.findById(dto.getGno());

    if(result.isPresent()){
      Guestbook entity = result.get();

      entity.changeTitle(dto.getTitle());
      entity.changeContent(dto.getContent());

      repository.save(entity);
    }
  }

  private BooleanBuilder getSearch(PageRequestDTO requestDTO){  // Querydsl 처리
    String type = requestDTO.getType();
    BooleanBuilder booleanBuilder = new BooleanBuilder();
    QGuestbook qGuestbook = QGuestbook.guestbook;
    String keyword = requestDTO.getKeyword();
    BooleanExpression expression = qGuestbook.gno.gt(0L);

    booleanBuilder.and(expression);

    if(type == null || type.trim().isEmpty()){
      return booleanBuilder;
    }

    BooleanBuilder conditionBuilder = new BooleanBuilder();
    if(type.contains("t")){
      conditionBuilder.or(qGuestbook.title.contains(keyword));
    }
    if(type.contains("c")){
      conditionBuilder.or(qGuestbook.content.contains(keyword));
    }
    if(type.contains("w")){
      conditionBuilder.or(qGuestbook.writer.contains(keyword));
    }

    booleanBuilder.and(conditionBuilder);

    return booleanBuilder;

  }

}
