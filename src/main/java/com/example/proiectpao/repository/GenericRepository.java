package com.example.proiectpao.repository;

import com.mongodb.lang.Nullable;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/*
 * O interfata generica pentru a putea face mai multe operatii cu mongodb
 */
@NoRepositoryBean
public interface GenericRepository<T, ID>
        extends ListCrudRepository<T, ID>,
                ListPagingAndSortingRepository<T, ID>,
                QueryByExampleExecutor<T> {
    <S extends T> S insert(S obj);

    <S extends T> List<S> insert(Iterable<S> obj);

    <S extends T> List<S> findAll(@Nullable Example<S> example);
}
