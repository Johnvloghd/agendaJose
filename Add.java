btnAgenda.setOnAction(e -> {
            btnAgenda.setStyle(ESTILO_BOTAO_ATIVO);
            btnRelatorios.setStyle(usuarioLogado.getPerfil().equalsIgnoreCase("ADMIN") ? ESTILO_BOTAO_MENU : ESTILO_BOTAO_MENU + " -fx-opacity: 0.3;");
            conteudoCentral.getChildren().clear();
            // Passamos "usuarioLogado" em vez de apenas o e-mail
            conteudoCentral.getChildren().add(new AgendaView(usuarioLogado)); 
        });
