package com.example.produtos.controllers;

import com.example.produtos.exception.ProdutoNotFoundException;
import com.example.produtos.model.Produto;
import com.example.produtos.service.ProdutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ProdutoController {

    @Autowired
    ProdutoService produtoService;

    Logger LOGGER = LoggerFactory.getLogger(ProdutoController.class);

    @GetMapping
    public ResponseEntity getAll(){

        List<Produto> all = produtoService.getAll();
        LOGGER.info("OBTER TUDO: " + all);
        return ResponseEntity.ok(all);

    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id){

        try {

            Produto produto = produtoService.getById(id);
            LOGGER.info("OBTER POR ID: " + id);
            return ResponseEntity.ok(produto);

        } catch (ProdutoNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Mensagem", "Produto não encontrado"));
        }


    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id){

        try {

            Produto produto = produtoService.getById(id);
            produtoService.deleteById(produto.getId());
            LOGGER.info("EXCLUIR: " + id);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("Mensagem", "Produto com o id " + id + " excluido com sucesso!"));

        } catch (ProdutoNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Mensagem", "Produto não encontrado"));
        }

    }

    @PostMapping
    public ResponseEntity create(@RequestBody Produto produto){

        Produto produto1 = produtoService.create(produto);
        LOGGER.info("CRIAR: " + produto1);
        return ResponseEntity.ok(produto1);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody Produto produto){

        try {

            Produto produto1 = produtoService.getById(id);
            Produto updated = produtoService.update(produto1.getId(), produto);
            LOGGER.info("ATUALIZAR: " + updated);
            return ResponseEntity.ok(updated);

        } catch (ProdutoNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Mensagem", "Produto não encontrado"));
        }

    }

}
