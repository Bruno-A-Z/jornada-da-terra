# 🌱 Jornada da Terra
### Agro & Clima Gamificado — FIAP Java Development

---

## 📋 Sobre o Projeto

O **Jornada da Terra** transforma a gestão da fazenda em uma jornada épica.
Dados complexos de satélites são convertidos em **missões interativas** para
pequenos e médios produtores rurais, tornando o agronegócio acessível e engajador.

---
🔗 Links do Projeto
Link🚀 Deploy (Render)[https://jornada-da-terra.onrender.com](https://jornada-da-terra.onrender.com)
📹 Vídeo de Apresentação https://youtu.be/xxxxx
📄 Swagger (Documentação da API) [https://jornada-da-terra.onrender.com/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
💻 Repositório GitHub [https://github.com/Bruno-A-Z/jornada-da-terra](https://github.com/Bruno-A-Z/jornada-da-terra/)
---

## 🏗️ Arquitetura e Conceitos de POO Aplicados

### Pilares da POO utilizados (conforme estudado)

| Pilar | Onde está no projeto |
|---|---|
| **Abstração** | Classe `Missao` é abstrata — define o contrato sem implementar tudo |
| **Herança** | `MissaoClimatica`, `MissaoMonitoramento`, `MissaoProdutividade` herdam de `Missao` |
| **Encapsulamento** | Todos os atributos são `private` com getters/setters |
| **Polimorfismo** | `validarConclusao()` e `getIcone()` têm comportamento diferente em cada subclasse |

### Estrutura de Pacotes
```
br.com.fiap.jornadaterra
├── enums/
│   ├── StatusMissao      (PENDENTE, EM_ANDAMENTO, CONCLUIDA...)
│   ├── TipoAlerta        (GEADA, SECA, CHUVA_EXCESSIVA...)
│   └── TipoCultura       (SOJA, MILHO, CAFE...)
├── model/
│   ├── Produtor          (usuário do sistema + gamificação)
│   ├── Fazenda           (propriedade rural)
│   ├── Setor             (subdivisão monitorada por satélite)
│   └── missao/
│       ├── Missao              ← CLASSE ABSTRATA (Abstração)
│       ├── MissaoClimatica     ← herda Missao (Herança + Polimorfismo)
│       ├── MissaoMonitoramento ← herda Missao (Herança + Polimorfismo)
│       └── MissaoProdutividade ← herda Missao (Herança + Polimorfismo)
├── repository/           (acesso ao banco de dados - JPA)
├── service/              (regras de negócio)
└── controller/           (endpoints REST da API)
```

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Executar
```bash
# 1. Clonar o repositório
git clone https://github.com/Bruno-A-Z/jornada-da-terra.git
cd jornada-da-terra

# 2. Executar
mvn spring-boot:run
```

### Acessar
- **API REST:** http://localhost:8080/api
- **Banco H2 (console):** http://localhost:8080/h2-console
- **Render [https://jornada-da-terra.onrender.com](https://jornada-da-terra.onrender.com)
---

## 🌐 Endpoints da API

### Produtores
| Método | URL | Descrição |
|---|---|---|
| POST | `/api/produtores` | Cadastrar produtor |
| GET | `/api/produtores` | Listar todos |
| GET | `/api/produtores/{id}` | Buscar por ID |
| GET | `/api/produtores/{id}/perfil` | Perfil gamificado |
| PUT | `/api/produtores/{id}` | Atualizar |
| DELETE | `/api/produtores/{id}` | Deletar |

### Missões
| Método | URL | Descrição |
|---|---|---|
| GET | `/api/missoes/produtor/{id}` | Missões ativas |
| GET | `/api/missoes/produtor/{id}/todas` | Histórico completo |
| POST | `/api/missoes/{id}/iniciar?produtorId=1` | Iniciar missão |
| POST | `/api/missoes/{id}/concluir?produtorId=1` | Concluir + ganhar pontos |
| POST | `/api/missoes/{id}/acao-climatica` | Confirmar ação climática |
| POST | `/api/missoes/{id}/verificar-setor?nomeSetor=Sul` | Verificar setor |

###💡 Fluxo Completo de Exemplo
| Passos | Metodo/Url |
|---|---|
| 1. Criar produtor  |  → POST /produtores |
| 2. Criar fazenda      |  → POST /fazendas/produtor/1 |
| 3. Criar setor        |  → POST /setores/fazenda/1 |
| 4. Simular satélite   |  → PATCH /setores/1/satelital  { temperatura: 1.5 } |
| 5. Gerar missões      |  → POST /fazendas/1/gerar-missoes |
| 6. Iniciar missão     |  → POST /missoes/1/iniciar?produtorId=1 |
| 7. Confirmar ação     |  → POST /missoes/1/acao-climatica |
| 8. Concluir missão    |  → POST /missoes/1/concluir?produtorId=1 |
| 9. Ver perfil         |  → GET  /produtores/1/perfil   → +150 pts 🏆 |

---

## 🎮 Sistema de Gamificação

| Nível | Título | Pontos necessários |
|---|---|---|
| 1 | 🌱 Semeador | 0 |
| 2 | 🌿 Lavrador | 200 |
| 3 | 🌾 Cultivador | 800 |
| 4 | 🏆 Guardião da Colheita | 2.000 |
| 5 | 👑 Mestre da Terra | 5.000 |

### Pontos por tipo de missão
| Missão | Pontos |
|---|---|
| Defesa contra Geada/Granizo | 150 pts |
| Batalha contra a Seca | 120 pts |
| Colheita | 200 pts |
| Plantio | 150 pts |
| Monitoramento de Setor | 50 pts/setor |

---

## 📡 Dados Satelitais Simulados

O `Setor` processa dados como um satélite real enviaria:

```java
setor.atualizarDadosSatelitais(
    temperatura,     // °C — detecta risco de geada
    umidadeSolo,     // % — detecta risco de seca
    indiceVegetacao  // NDVI (0-1) — saúde da lavoura
);
```

O sistema calcula automaticamente o `nivelRisco` (0-5) e gera missões.

---

## 💡 Exemplo de Fluxo Completo

```
1. Satélite detecta temperatura de 1.5°C no Setor Sul
2. Sistema gera MissaoClimatica automaticamente
3. Produtor recebe no app: "🥶 O inverno chegou! Proteja sua colheita!"
4. Produtor inicia a missão → POST /api/missoes/1/iniciar?produtorId=1
5. Produtor toma a ação → POST /api/missoes/1/acao-climatica
6. Produtor conclui → POST /api/missoes/1/concluir?produtorId=1
7. Sistema credita +150 pontos e verifica subida de nível
```

---

## 👥 Equipe - 2TDSPO
|         Nome        |   RM   |
|---------------------|--------|
| Bruno A Zanaeli     | 563736 |
| Christian S Freitas | 566098 |
| Pedro P Biasolli    | 562521 |
| Rodrigo Tiezzi      | 562975 |
| Maheus E Souza      | 562532 |
--------------------------------
