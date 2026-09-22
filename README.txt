Projeto: NoirFelinoPrototipo
============================

Como importar no Eclipse:
1. Abra o Eclipse.
2. Vá em File > Import... > General > Existing Projects into Workspace.
3. Clique em "Select archive file" e escolha o arquivo .zip deste projeto
   (ou extraia o zip antes e use "Select root directory").
4. Marque o projeto "NoirFelinoPrototipo" e clique em Finish.

Como executar:
- Clique com o botão direito em MenuPrincipal.java > Run As > Java Application.
  Isso abre o menu com botões para as telas de Artistas e Eventos.
- Você também pode rodar TelaArtistas.java ou TelaEventos.java
  diretamente (cada uma tem seu próprio método main).

Estrutura:
- src/Artista.java        -> classe de domínio Artista
- src/Evento.java         -> classe de domínio Evento
- src/TelaArtistas.java   -> CSU02: Cadastrar, editar e excluir artistas
- src/TelaEventos.java    -> CSU03: Cadastrar e gerenciar eventos temáticos
- src/MenuPrincipal.java  -> tela inicial de navegação

Observação: os dados são mantidos apenas em memória (ArrayList),
sem conexão com banco de dados. É um protótipo de interface para
validar o fluxo das telas antes de implementar a persistência (MySQL).
