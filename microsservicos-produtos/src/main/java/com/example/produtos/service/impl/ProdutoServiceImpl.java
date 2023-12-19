package com.example.produtos.service.impl;

import com.example.produtos.exception.ProdutoNotFoundException;
import com.example.produtos.model.Produto;
import com.example.produtos.repository.ProdutoRepository;
import com.example.produtos.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ProdutoServiceImpl implements ProdutoService {

    @Autowired
    ProdutoRepository produtoRepository;

    @Override
    public List<Produto> getAll(){
        return produtoRepository.findAll();
    }

    @Override
    public Produto create(Produto produto){

        return produtoRepository.save(produto);

    }

    @Override
    public void deleteById(Long id) {

        produtoRepository.deleteById(id);

    }

    @Override
    public Produto update(Long id, Produto produto){

        produto.setId(id);
        return create(produto);

    }

    public Produto getById(Long id){
        return produtoRepository.findById(id).orElseThrow(ProdutoNotFoundException::new);
    }

}
