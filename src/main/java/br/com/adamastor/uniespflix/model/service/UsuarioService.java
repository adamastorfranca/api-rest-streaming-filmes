package br.com.adamastor.uniespflix.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.adamastor.uniespflix.exception.AplicacaoException;
import br.com.adamastor.uniespflix.exception.ExceptionValidacoes;
import br.com.adamastor.uniespflix.model.dto.UsuarioRequestDTO;
import br.com.adamastor.uniespflix.model.dto.UsuarioResponseDTO;
import br.com.adamastor.uniespflix.model.entity.Cartao;
import br.com.adamastor.uniespflix.model.entity.Plano;
import br.com.adamastor.uniespflix.model.entity.Usuario;
import br.com.adamastor.uniespflix.model.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private CartaoService cartaoService;
	@Autowired
	private PlanoService planoService;
	@Autowired
	private FavoritoService favoritoService;

	public List<UsuarioResponseDTO> buscarTodosUsuarios() {
		List<Usuario> usuarios = usuarioRepository.findAll();
		return UsuarioResponseDTO.converter(usuarios);
	}

	public Usuario cadastrar(UsuarioRequestDTO form) {
		Optional<Usuario> resultado = usuarioRepository.findByEmail(form.getEmail());
		if (resultado.isPresent()) {
			throw new AplicacaoException(ExceptionValidacoes.ERRO_EMAIL_JA_CADASTRADO);
		}
		if (!form.getSenha().equals(form.getSenhaConfirmar())) {
			throw new AplicacaoException(ExceptionValidacoes.ERRO_SENHAS_NAO_CORRESPONDEM);
		}
		Plano plano = planoService.buscarPlanoPorNome(form.getNomePlano());	
		if (plano == null) {
			throw new AplicacaoException(ExceptionValidacoes.ERRO_PLANO_INEXISTENTE);
		}		
		Cartao cartao = cartaoService.cadastrar(form.getNumeroCartao(), form.dataValidade(), form.getCodigoSeguranca(),	form.getNomeTitular(), form.getCpf());
		Usuario usuario = form.toUsuario();
		favoritoService.salvar(usuario.getFavorito());
		usuario.getPlano().add(plano);
		usuario.setCartao(cartao);

		usuarioRepository.save(usuario);

		return usuario;
	}

//	public UsuarioResponseDTO atualizar(@Valid UsuarioAtualizacaoForm form) {
//		Optional<Usuario> resultado1 = usuarioRepository.findById(form.getId());
//		if (!resultado1.isPresent()) {
//			throw new AplicacaoException(ExceptionValidacoes.ERRO_USUARIO_NAO_ENCONTRADO);
//		}
//		
//		Optional<Usuario> resultado2 = usuarioRepository.findByEmail(form.getEmail());
//		if (resultado2.isPresent()) {
//			throw new AplicacaoException(ExceptionValidacoes.ERRO_EMAIL_JA_CADASTRADO);
//		}
//		if (!form.getSenha().isEmpty() && !form.getSenha().equals(form.getSenhaConfirmar())) {
//			throw new AplicacaoException(ExceptionValidacoes.ERRO_SENHAS_NAO_CORRESPONDEM);
//		}
//		
//		form.atualizarDados(resultado1.get());
//		usuarioRepository.save(resultado1.get());
//		
//		return new UsuarioResponseDTO(resultado1.get());
//	}
//	
//	public boolean deletar(Long id) {
//		Optional<Usuario> resultado = usuarioRepository.findById(id);
//		if (!resultado.isPresent()) {
//			throw new AplicacaoException(ExceptionValidacoes.ERRO_USUARIO_NAO_ENCONTRADO);
//		}
//		
//		usuarioRepository.delete(resultado.get());
//		return resultado.isPresent();
//	}

}