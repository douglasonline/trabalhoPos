package controller;


import configuration.DadosIniciaisService;
import io.smallrye.mutiny.Uni;

import model.Produto;
import model.ProdutoDTO;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import repository.ProdutosRepository;
//import org.jboss.resteasy.reactive.RestResponse;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/")
public class ProdutoController {

    @Inject
    ProdutosRepository produtosRepository;

    public static final String nomeBanco = "microsservicos_produtos";

    //Banco de dados padrão do PostgreSQL (postgres)
    public static final String URL = "jdbc:postgresql://localhost:5432/postgres";

    //Banco de dados utilizado
    public static final String URL2 = "jdbc:postgresql://localhost:5432/microsservicos_produtos";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "1234";

    @Retry(maxRetries = 100)
    @Timeout(900)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Uni<List<ProdutoDTO>> getAll() {

        return produtosRepository.listAll()
                .map(produtos -> produtos.stream()
                        .map(produto -> new ProdutoDTO(
                                produto.getId(),
                                produto.getNome(),
                                produto.getValor()
                        ))
                        .collect(Collectors.toList())
                );
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Uni<Response> getById(@PathParam("id") Long id) {
        return Produto.<Produto>findById(id)
                .onItem().transformToUni(produto -> {
                    if (produto == null) {
                        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND)
                                .entity("{\"Mensagem\": \"Produto não encontrado\"}")
                                .type(MediaType.APPLICATION_JSON)
                                .build());
                    } else {
                        return Uni.createFrom().item(Response.ok(new ProdutoDTO(
                                produto.getId(),
                                produto.getNome(),
                                produto.getValor()
                        )).build());
                    }
                });
    }

    @POST
    @Transactional
    public Uni<Response> create(Produto produto) {
        return produto.persistAndFlush()
                .chain(() -> {
                    // Consulta o banco para obter o produto com o ID atualizado
                    return Produto.<Produto>findById(produto.getId())
                            .replaceWith(Response.status(Response.Status.CREATED)
                                    .entity(new ProdutoDTO(produto.getId(), produto.getNome(), produto.getValor()))
                                    .build());
                });
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Uni<Response> update(Long id, Produto produto) {
        return Produto.<Produto>findById(id)
                .onItem().transformToUni(existing -> {
                    if (existing == null) {
                        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND)
                                .entity("{\"Mensagem\": \"Produto não encontrado\"}")
                                .type(MediaType.APPLICATION_JSON)
                                .build());
                    } else {
                        existing.setNome(produto.getNome());
                        existing.setValor(produto.getValor());
                        return existing.persistAndFlush()
                                .replaceWith(Response.ok(new ProdutoDTO(existing.getId(), existing.getNome(), existing.getValor())).build());
                    }
                });
    }


    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteProdutoById(@PathParam("id") Long id) {
        String deleteQuery = "DELETE FROM produto WHERE id = ?";

        return Uni.createFrom().item(() -> {
            try (Connection connection = DriverManager.getConnection(URL2, USERNAME, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setLong(1, id);
                int rowsDeleted = preparedStatement.executeUpdate();

                if (rowsDeleted > 0) {
                    return Response.ok("{\"Mensagem\": \"Produto com o id " + id + " excluído com sucesso!\"}").build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"Mensagem\": \"Produto não encontrado\"}")
                            .build();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return Response.serverError().entity("{\"Mensagem\": \"Erro durante a exclusão\"}").build();
            }
        });
    }


    /*@DELETE
    @Path("/{id}")
    @Transactional
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Produto.<Produto>findById(id)
                .onItem().transformToUni(existing -> {
                    if (existing != null) {
                        return existing.delete()
                                .map(deleted -> Response.ok("{\"Mensagem\": \"Produto removido com sucesso\"}")
                                        .type(MediaType.APPLICATION_JSON)
                                        .build());
                    } else {
                        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND)
                                .entity("{\"Mensagem\": \"Produto não encontrado\"}")
                                .type(MediaType.APPLICATION_JSON)
                                .build());
                    }
                });


    }*/

}
