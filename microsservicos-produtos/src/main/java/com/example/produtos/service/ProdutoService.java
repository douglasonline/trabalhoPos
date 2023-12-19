package com.example.produtos.service;

import com.example.produtos.model.Produto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public interface ProdutoService {

    public abstract List<Produto> getAll();

    public abstract Produto create(Produto produto);

    public abstract void deleteById(Long id);

    public abstract Produto update(Long id, Produto produto);

    public abstract Produto getById(Long id);

}
