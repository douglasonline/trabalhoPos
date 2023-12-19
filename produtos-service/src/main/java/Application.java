import configuration.DadosIniciaisService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;
import javax.transaction.Transactional;


@QuarkusMain
public class Application implements QuarkusApplication {



    public static void main(String... args) {

        Quarkus.run(Application.class, args);

    }

    @Override
    public int run(String... args) throws Exception {
        // Seu código principal aqui...
        DadosIniciaisService dadosIniciaisService = new DadosIniciaisService();
        dadosIniciaisService.inicializarBancoDeDados();
       Quarkus.waitForExit();
        return 0;
    }


}