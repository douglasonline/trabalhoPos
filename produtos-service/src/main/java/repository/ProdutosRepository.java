package repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import model.Produto;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutosRepository implements PanacheRepositoryBase<Produto, Long> {
}
