package br.com.fiap.jornadaterra;

import br.com.fiap.jornadaterra.enums.TipoAlerta;
import br.com.fiap.jornadaterra.enums.TipoCultura;
import br.com.fiap.jornadaterra.model.Fazenda;
import br.com.fiap.jornadaterra.model.Produtor;
import br.com.fiap.jornadaterra.model.Setor;
import br.com.fiap.jornadaterra.model.missao.MissaoClimatica;
import br.com.fiap.jornadaterra.model.missao.MissaoMonitoramento;
import br.com.fiap.jornadaterra.model.missao.MissaoProdutividade;
import br.com.fiap.jornadaterra.repository.FazendaRepository;
import br.com.fiap.jornadaterra.repository.MissaoRepository;
import br.com.fiap.jornadaterra.repository.ProdutorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JornadaDaTerraApplication {

    public static void main(String[] args) {
        SpringApplication.run(JornadaDaTerraApplication.class, args);
    }

    /**
     * DataLoader: popula o banco com dados de exemplo ao iniciar.
     * Ideal para demonstração acadêmica.
     */
    @Bean
    CommandLineRunner initDatabase(ProdutorRepository produtorRepo,
                                   FazendaRepository fazendaRepo,
                                   MissaoRepository missaoRepo) {
        return args -> {

            // === PRODUTOR DE EXEMPLO ===
            Produtor joao = new Produtor("João da Silva", "123.456.789-50",
                    "joao@fazenda.com.br", "(11) 99999-0001");
            joao = produtorRepo.save(joao);

            Produtor maria = new Produtor("Maria Oliveira", "987.654.321-70",
                    "maria@fazenda.com.br", "(11) 99999-0002");
            maria = produtorRepo.save(maria);

            // === FAZENDA DO JOÃO ===
            Fazenda fazendaSol = new Fazenda(
                    "Fazenda Sol Nascente",
                    -22.9068, -47.0626,  // Coordenadas (Campinas-SP)
                    350.0,
                    "Campinas", "SP",
                    TipoCultura.SOJA,
                    joao
            );
            fazendaSol = fazendaRepo.save(fazendaSol);

            // === SETORES DA FAZENDA ===i
            Setor setorNorte = new Setor("Setor Norte", 80.0, fazendaSol);
            setorNorte.atualizarDadosSatelitais(22.0, 65.0, 0.75); // Normal

            Setor setorSul = new Setor("Setor Sul", 90.0, fazendaSol);
            setorSul.atualizarDadosSatelitais(1.5, 55.0, 0.60); // RISCO DE GEADA!

            Setor setorLeste = new Setor("Setor Leste", 70.0, fazendaSol);
            setorLeste.atualizarDadosSatelitais(24.0, 18.0, 0.30); // RISCO DE SECA!

            Setor setorOeste = new Setor("Setor Oeste", 110.0, fazendaSol);
            setorOeste.atualizarDadosSatelitais(23.0, 70.0, 0.80); // Normal

            // === MISSÕES GERADAS PARA A FAZENDA ===

            // Missão Climática - Geada no Setor Sul
            MissaoClimatica missaoGeada = new MissaoClimatica(
                    TipoAlerta.GEADA, "Setor Sul", 1.5, fazendaSol);
            missaoRepo.save(missaoGeada);

            // Missão Climática - Seca no Setor Leste
            MissaoClimatica missaoSeca = new MissaoClimatica(
                    TipoAlerta.SECA, "Setor Leste", 24.0, fazendaSol);
            missaoRepo.save(missaoSeca);

            // Missão de Monitoramento semanal
            MissaoMonitoramento missaoMonit = new MissaoMonitoramento(
                    4, "semanal", fazendaSol);
            missaoRepo.save(missaoMonit);

            // Missão de Produtividade - Colheita da Soja
            MissaoProdutividade missaoProd = new MissaoProdutividade(
                    TipoCultura.SOJA, "COLHEITA", 58.0, fazendaSol);
            missaoRepo.save(missaoProd);

            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║     🌱 JORNADA DA TERRA - INICIADA!      ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  API:   http://localhost:8080/api        ║");
            System.out.println("║  H2 DB: http://localhost:8080/h2-console ║");
            System.out.println("║Swagger:                                  ║");
            System.out.println("║http://localhost:8080/swagger-ui/index.html");
            System.out.println("╚══════════════════════════════════════════╝\n");
        };
    }
}
