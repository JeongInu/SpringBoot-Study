package com.example.ex02.repository;

import com.example.ex02.entity.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired
    MemoRepository memoRepository;

    @Test
    public void testClass() {
        System.out.println("TEST:::"+memoRepository.getClass().getName());
    }

    @Test
    public void testInsertDummies(){

        IntStream.rangeClosed(1,100).forEach(i->{
           Memo memo = new Memo().builder().memoText("Sample..."+i).build();
           memoRepository.save(memo);
        });

    }

    @Test
    public void testSelect(){

        Long mno = 100L;

        Optional<Memo> result = memoRepository.findById(mno);

        System.out.println("======================JPA======================");

        result.ifPresent(System.out::println);

    }

    @Test
    public void testUpdate(){

        Memo memo = Memo.builder().mno(100L).memoText("Update Text").build();

        System.out.println(memoRepository.save(memo));

    }

    @Test
    public void testDelete(){

        Long mno = 100L;

        memoRepository.deleteById(mno);

    }

    @Test
    public void testPageDefault(){

        // 1페이지 10개
        Pageable pageable = PageRequest.of(0, 10);

        Page<Memo> result = memoRepository.findAll(pageable);

        System.out.println(result);

        System.out.println("-------------------------------------------------------");

        System.out.println("총 몇 페이지 : " + result.getTotalPages());

        System.out.println("전체 개수 : " + result.getTotalElements());

        System.out.println("현재 페이지 번호 : " + result.getNumber());

        System.out.println("페이지 당 데이터 수 : " + result.getSize());

        System.out.println("다음 페이지 존재 : " + result.hasNext());

        System.out.println("시작 페이지(0) : " + result.isFirst());

        System.out.println("-------------------------------------------------------");

        for(Memo memo : result.getContent()){
            System.out.println(memo);
        }

    }

    @Test
    public void testSort(){

        Sort sort1 = Sort.by("mno").descending();
        Sort sort2 = Sort.by("memoText").ascending();
        Sort sortAll = sort1.and(sort2);

        Pageable pageable = PageRequest.of(0, 10, sortAll);

        Page<Memo> result = memoRepository.findAll(pageable);

        result.get().forEach(System.out::println);

    }

    @Test
    public void testQueryMethods(){

        List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);

        for(Memo memo : list){
            System.out.println(memo);
        }

    }

    @Test
    public void testQueryMethodWithPageable(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
        Page<Memo> result = memoRepository.findByMnoBetween(10L, 50L, pageable);
        result.get().forEach(System.out::println);

    }

    @Commit
    @Transactional
    @Test
    public void testDeleteQueryMethods(){
        memoRepository.deleteMemoByMnoLessThan(10L);
    }

}
